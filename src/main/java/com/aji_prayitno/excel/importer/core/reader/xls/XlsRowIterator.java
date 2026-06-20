package com.aji_prayitno.excel.importer.core.reader.xls;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactoryInputStream;
import org.apache.poi.hssf.record.SSTRecord;

import com.aji_prayitno.excel.importer.core.Converter;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.xls.XlsTableDefinition;

public final class XlsRowIterator<T> implements Iterator<ImportResult<T>>, AutoCloseable {

	private final RecordFactoryInputStream recordStream;
	private final String sheetName;
	private final int startRowIndex;
	private final Constructor<T> dtoConstructor;
	private final List<ColumnDefinition<T, ?>> columnDefinitions;

	private final List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();
	private final Map<Integer, String> headers = new LinkedHashMap<>();

	private Map<String, String> currentRowValues;
	private ImportResult<T> nextRow;
	private int sheetIndex = -1;
	private int targetSheetIndex = -1;
	private int currentRow = -1;
	private boolean finished;
	private boolean closed;

	private SSTRecord sst = null;
	private boolean hasParsedHeaders = false;
	private boolean hasValidatedHeaders = false;
	
	public XlsRowIterator(
		RecordFactoryInputStream recordStream, 
		String sheetName, XlsTableDefinition<T> tableDefinition
	) {
		this.recordStream = recordStream;
		this.sheetName = sheetName;
		this.startRowIndex = tableDefinition.getStartRowIndex();
		try {
			this.dtoConstructor = tableDefinition.getDtoClass().getDeclaredConstructor();
		} catch (Exception e) {
			throw new IllegalStateException("DTO class must provide a public no-args constructor.", e);
		}
		this.columnDefinitions = tableDefinition.getColumns();
	}

	@Override
	public boolean hasNext() {
		if (closed || finished) {
			return false;
		}
		if (nextRow == null) {
			nextRow = readNextRow();
			if (nextRow == null) {
				finished = true;
				closeQuietly();
				return false;
			}
		}
		return true;
	}

	@Override
	public ImportResult<T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more Excel rows available.");
		}
		ImportResult<T> current = nextRow;
		nextRow = null;
		return current;
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
		} catch (Exception e) {
			throw new IllegalStateException("Failed to close Excel streaming resources.", e);
		}
	}

	private void closeQuietly() {
		try {
			close();
		} catch (Exception ignored) {
		}
	}

	private ImportResult<T> readNextRow() {
		try {
			Record hssfRecord;
			while ((hssfRecord = this.recordStream.nextRecord()) != null) {
				switch (hssfRecord.getSid()) {
					case BoundSheetRecord.sid -> boundSheetRecords.add((BoundSheetRecord) hssfRecord);
					case BOFRecord.sid -> handleSheetStructure((BOFRecord) hssfRecord);
					case SSTRecord.sid -> sst = (SSTRecord) hssfRecord;

					case NumberRecord.sid -> {
						NumberRecord num = (NumberRecord) hssfRecord;
						ImportResult<T> row = dispatchCell(
							num.getRow(), num.getColumn(), 
							String.valueOf(num.getValue())
						);
						if (row != null) return row;
					}
					case LabelSSTRecord.sid -> {
						LabelSSTRecord ls = (LabelSSTRecord) hssfRecord;
						String value = sst != null ? sst.getString(ls.getSSTIndex()).toString() : "";
						ImportResult<T> row = dispatchCell(ls.getRow(), ls.getColumn(), value);
						if (row != null) return row;
					}
					case BoolErrRecord.sid -> {
						BoolErrRecord br = (BoolErrRecord) hssfRecord;
						ImportResult<T> row = dispatchCell(
							br.getRow(), br.getColumn(), 
							String.valueOf(br.getBooleanValue())
						);
						if (row != null) return row;
					}
					case FormulaRecord.sid -> {
						FormulaRecord fr = (FormulaRecord) hssfRecord;
						ImportResult<T> row = dispatchCell(
							fr.getRow(), fr.getColumn(), 
							String.valueOf(fr.getValue())
						);
						if (row != null) return row;
					}
					default -> {
						// Memproses sisa data pada baris paling akhir file biner sebelum EOF
						if (currentRowValues != null) {
							ImportResult<T> finalRow = addCurrentRow(currentRowValues);
							currentRowValues = null;
							return finalRow;
						}
					}
				}
			}
			validateEndOfStream();
			return null;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(
				"Failed while reading legacy Excel .xls stream for sheet '" + 
				sheetName + "', row " + toExcelRowNumber(currentRow) + ".", e
			);
		}
	}
	
	// ================= METHOD MANAGEMENT SHEET =================
	private void handleSheetStructure(BOFRecord bof) {
		if (bof.getType() == BOFRecord.TYPE_WORKSHEET) {
			sheetIndex++;
			if (targetSheetIndex == -1) {
				for (int i = 0; i < boundSheetRecords.size(); i++) {
					if (boundSheetRecords.get(i).getSheetname().equals(this.sheetName)) {
						targetSheetIndex = i;
						break;
					}
				}
			}
		}
	}

	// Pintu masuk utama untuk mendistribusikan sel berdasarkan posisinya
	private ImportResult<T> dispatchCell(int rowNum, int colNum, String value) {
		// Validasi apakah record sel berada pada target sheet yang dicari
		if (targetSheetIndex == -1 || sheetIndex != targetSheetIndex) {
			return null;
		}
		// Jika berada tepat di indeks baris awal, arahkan ke penanganan Header
		if (rowNum == this.startRowIndex) {
			handleHeaderCell(colNum, value);
			return null;
		}
		// Jika berada di bawah baris awal, arahkan ke penanganan Data Row
		if (rowNum > this.startRowIndex && hasParsedHeaders) {
			validateColumn();
			return handleDataCell(rowNum, colNum, value);
		}
		return null;
	}

	// ================= METHOD MANAGEMENT HEADER =================
	private void handleHeaderCell(int colNum, Object value) {
		headers.put(colNum, String.valueOf(value));
		hasParsedHeaders = true;
	}

	private void validateColumn() {
		if (hasValidatedHeaders) {
			return;
		}
		for (ColumnDefinition<T, ?> columnDefinition : columnDefinitions) {
			if (!columnDefinition.isIgnoreNotFound() && !headers.containsValue(columnDefinition.getHeader())) {
				throw new IllegalArgumentException("Column " + columnDefinition.getHeader() + " is not found.");
			}
		}
		hasValidatedHeaders = true;
	}
	
	// ================= METHOD MANAGEMENT DATA ROW =================
	private ImportResult<T> handleDataCell(int rowNum, int colNum, String value) {
		// Mendeteksi lompatan/transisi ke baris baru
		if (rowNum != currentRow) {
			Map<String, String> completedRow = currentRowValues;

			// Inisialisasi map kontainer untuk baris baru saat ini
			currentRow = rowNum;
			currentRowValues = new LinkedHashMap<>();
			headers.values().forEach(h -> currentRowValues.put(h, null));

			// Masukkan nilai sel pertama pada baris baru ini
			String header = headers.get(colNum);
			if (header != null) {
				currentRowValues.put(header, value);
			}

			// Jika data baris sebelumnya sudah lengkap terakit, tukar menjadi DTO dan return
			if (completedRow != null) {
				return addCurrentRow(completedRow);
			}
		} else {
			// Jika masih berada di rentang baris yang sama, terus kumpulkan data kolomnya
			String header = headers.get(colNum);
			if (header != null && currentRowValues != null) {
				currentRowValues.put(header, value);
			}
		}
		return null;
	}

	private ImportResult<T> addCurrentRow(Map<String, String> rowValues) {
		T dtoInstance;
		try {
			dtoInstance = dtoConstructor.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create DTO instance.", e);
		}

		Map<String, String> error = new HashMap<>();
		for (ColumnDefinition<T, ?> columnDefinition : columnDefinitions) {
			try {
				setColumnValue(dtoInstance, columnDefinition, rowValues);
			} catch (Exception e) {
				String message = columnDefinition.getHeader() + ":" + e.getMessage();
				error.compute(
					columnDefinition.getHeader(),
					(k, v) -> v != null ? v + ", " + message : message
				);
			}
		}
		return new ImportResult<>(dtoInstance, error);
	}

	private <V> void setColumnValue(
		T instance, ColumnDefinition<T, V> columnDefinition,
		Map<String, String> rowValues
	) {
		String rawValue = rowValues.get(columnDefinition.getHeader());
        V convertedValue = Converter.convert(rawValue, columnDefinition.getTargetType());
        columnDefinition.getSetter().accept(instance, convertedValue);
	}

	private void validateEndOfStream() {
		if (targetSheetIndex == -1) {
			throw new IllegalArgumentException("Worksheet not found in legacy Excel .xls workbook: '" + sheetName + "'.");
		}
		if (!hasParsedHeaders) {
			throw new IllegalArgumentException("Header row " + (startRowIndex + 1) + " was not found in worksheet '" + sheetName + "'.");
		}
	}

	private int toExcelRowNumber(int zeroBasedRow) {
		return zeroBasedRow < 0 ? -1 : zeroBasedRow + 1;
	}
}
