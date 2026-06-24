package com.aji_prayitno.excel.importer.core.reader.xls.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.aji_prayitno.excel.importer.core.Converter;
import com.aji_prayitno.excel.importer.core.reader.BaseReader;
import com.aji_prayitno.excel.importer.core.reader.Util;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.plain.PlainTableDefinition;

public final class SimpleXlsTableReader<T> implements BaseReader<T> {

	private final String sheetName;
	private final PlainTableDefinition<T> tableDefinition;
	
	private final InputStream inputStream;
	
	public SimpleXlsTableReader(SheetDefinition<T> sheetDefinition) {
		this.sheetName = sheetDefinition.getSheetName();
		this.tableDefinition = sheetDefinition.getPlainTable();
		this.inputStream = sheetDefinition.getInputStream();
	}
	
	@Override
	public List<ImportResult<T>> importAsList() {
		return read();
	}
	@Override
	public Stream<ImportResult<T>> importAsStream() {
		return importAsList().stream();
	}
	
	
	private List<ImportResult<T>> read() {
		try (Workbook workbook = new HSSFWorkbook(inputStream)) {
			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new IllegalArgumentException("Worksheet not found in legacy Excel .xls workbook: '" + sheetName + "'.");
			}
			return readSheet(sheet, tableDefinition);
		} catch (OfficeXmlFileException e) {
			throw new IllegalArgumentException(
				"Unsupported Excel file for legacy .xls small-file import. The uploaded file is .xlsx; "
				+ "use table import for .xlsx files instead.", e
			);
		} catch (NotOLE2FileException e) {
			throw new IllegalArgumentException(
				"Unsupported Excel file for legacy .xls small-file import. The uploaded file is not a valid .xls workbook.", e
			);
		} catch (EmptyFileException e) {
			throw new IllegalArgumentException("Unsupported Excel file for legacy .xls small-file import. The uploaded file is empty.", e);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read legacy Excel .xls workbook due to an I/O error.", e);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected failure during legacy Excel .xls small-file import.", e);
		}
	}

	private List<ImportResult<T>> readSheet(Sheet sheet, PlainTableDefinition<T> tableDefinition) {
		int headerRowIndex = tableDefinition.getStartRowIndex();
		Row headerRow = sheet.getRow(headerRowIndex);
		if (headerRow == null) {
			throw new IllegalArgumentException("Header row " + headerRowIndex
					+ " was not found in worksheet " + sheet.getSheetName() + ".");
		}

		Map<String, Integer> columnIndexes = resolveColumnIndexes(headerRow, tableDefinition);
		return readData(sheet, tableDefinition, headerRowIndex, columnIndexes);
	}

	private Map<String, Integer> resolveColumnIndexes(
		Row headerRow, PlainTableDefinition<T> tableDefinition
	) {
		Map<String, Integer> headerIndexes = new LinkedHashMap<>();
		for (Cell cell : headerRow) {
			String header = Converter.readCellValue(cell);
			if (header != null && !header.isBlank()) {
				headerIndexes.putIfAbsent(header.trim(), cell.getColumnIndex());
			}
		}

		Map<String, Integer> columnIndexes = new LinkedHashMap<>();
		for (ColumnDefinition<T, ?> columnDefinition : tableDefinition.getColumns()) {
			Integer columnIndex = headerIndexes.get(columnDefinition.header());
			if (columnIndex == null) {
				if(!columnDefinition.ignoreNotFound()) {
					throw new IllegalArgumentException("Column: " + columnDefinition.header() + " is not found.");					
				}
				continue;
			}
			columnIndexes.put(columnDefinition.header(), columnIndex);
		}
		return columnIndexes;
	}

	private List<ImportResult<T>> readData(
		Sheet sheet, PlainTableDefinition<T> tableDefinition,
		int headerRowIndex, Map<String, Integer> columnIndexes
	) {
		List<ImportResult<T>> results = new ArrayList<>();
		for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null || isRowEmpty(row, columnIndexes)) {
				continue;
			}
			results.add(readRowData(tableDefinition, columnIndexes, row));
		}
		return results;
	}

	private ImportResult<T> readRowData(
		PlainTableDefinition<T> tableDefinition, 
		Map<String, Integer> columnIndexes, Row row
	) {
		T dtoInstance;
		try {
			dtoInstance = tableDefinition.getDtoClass().getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create DTO instance for row .", e);
		}

		Map<String, String> errors = new HashMap<>();
		for (ColumnDefinition<T, ?> columnDefinition : tableDefinition.getColumns()) {
			var colIdx = columnIndexes.get(columnDefinition.header());
			if(colIdx == null) {
				continue;
			}
			Cell cell = row.getCell(colIdx);
			try {
				Util.setColumnValue(dtoInstance, columnDefinition, cell);
			} catch (Exception e) {
				String message = columnDefinition.header() + ":" + e.getMessage();
				errors.compute(columnDefinition.header(), (k, v) -> v != null ? v + ", " + message : message);
			}
		}
		return new ImportResult<>(dtoInstance, errors);
	}

	private boolean isRowEmpty(Row row, Map<String, Integer> columnIndexes) {
		for (Integer columnIndex : columnIndexes.values()) {
			String value = Converter.readCellValue(row.getCell(columnIndex));
			if (value != null && !value.isBlank()) {
				return false;
			}
		}
		return true;
	}

}
