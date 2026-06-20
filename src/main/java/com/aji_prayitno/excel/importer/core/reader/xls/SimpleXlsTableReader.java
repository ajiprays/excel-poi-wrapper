package com.aji_prayitno.excel.importer.core.reader.xls;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.aji_prayitno.excel.importer.core.Converter;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.xls.XlsTableDefinition;

public final class SimpleXlsTableReader {

	private SimpleXlsTableReader() {}

	public static <T> List<ImportResult<T>> read(
		SheetDefinition<T> sheetDefinition
	) {
		InputStream inputStream = sheetDefinition.getInputStream();
		String sheetName = sheetDefinition.getSheetName();
		XlsTableDefinition<T> tableDefinition = sheetDefinition.getXlsTable();
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

	private static <T> List<ImportResult<T>> readSheet(Sheet sheet, XlsTableDefinition<T> tableDefinition) {
		int headerRowIndex = tableDefinition.getStartRowIndex();
		Row headerRow = sheet.getRow(headerRowIndex);
		if (headerRow == null) {
			throw new IllegalArgumentException("Header row " + headerRowIndex
					+ " was not found in worksheet " + sheet.getSheetName() + ".");
		}

		Map<String, Integer> columnIndexes = resolveColumnIndexes(headerRow, tableDefinition);
		List<ImportResult<T>> results = new ArrayList<>();
		for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null || isRowEmpty(row, columnIndexes)) {
				continue;
			}
			results.add(readRow(row, rowIndex, tableDefinition, columnIndexes));
		}
		return results;
	}

	private static <T> Map<String, Integer> resolveColumnIndexes(
		Row headerRow, XlsTableDefinition<T> tableDefinition
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
			Integer columnIndex = headerIndexes.get(columnDefinition.getHeader());
			if (columnIndex == null) {
				throw new IllegalArgumentException("Column: " + columnDefinition.getHeader()
						+ " was not found in header row " + tableDefinition.getStartRowIndex() + ".");
			}
			columnIndexes.put(columnDefinition.getHeader(), columnIndex);
		}
		return columnIndexes;
	}

	private static <T> ImportResult<T> readRow(
		Row row, int rowIndex,
		XlsTableDefinition<T> tableDefinition,
		Map<String, Integer> columnIndexes
	) {
		T dtoInstance;
		try {
			dtoInstance = tableDefinition.getDtoClass().getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create DTO instance for row " + (rowIndex + 1) + ".", e);
		}

		Map<String, String> errors = new HashMap<>();
		for (ColumnDefinition<T, ?> columnDefinition : tableDefinition.getColumns()) {
			Cell cell = row.getCell(columnIndexes.get(columnDefinition.getHeader()));
			try {
				setColumnValue(dtoInstance, columnDefinition, cell);
			} catch (Exception e) {
				String message = columnDefinition.getHeader() + ":" + e.getMessage();
				errors.compute(columnDefinition.getHeader(), (k, v) -> v != null ? v + ", " + message : message);
			}
		}
		return new ImportResult<>(dtoInstance, errors);
	}

	private static boolean isRowEmpty(Row row, Map<String, Integer> columnIndexes) {
		for (Integer columnIndex : columnIndexes.values()) {
			String value = Converter.readCellValue(row.getCell(columnIndex));
			if (value != null && !value.isBlank()) {
				return false;
			}
		}
		return true;
	}

	private static <V, T> void setColumnValue(
		T instance,
		ColumnDefinition<T, V> columnDefinition,
		Cell cell
	) {
		columnDefinition.getSetter().accept(
			instance, 
			Converter.convert(
				Converter.readCellValue(cell), columnDefinition.getTargetType()
			)
		);
	}

}
