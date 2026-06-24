package com.aji_prayitno.excel.importer.core.reader.xlsx.plain.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.aji_prayitno.excel.importer.core.Converter;
import com.aji_prayitno.excel.importer.core.reader.BaseReader;
import com.aji_prayitno.excel.importer.core.reader.Util;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.plain.PlainTableDefinition;

public final class SimplePlainTableReader<T> implements BaseReader<T> {

	private final String sheetName;
	private final PlainTableDefinition<T> tableDefinition;
	
	private final InputStream inputStream;
	
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private Map<String, Integer> columnIdxs = new HashMap<>();
	
	public SimplePlainTableReader(SheetDefinition<T> sheetDefinition) {
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
		try (
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream)
		) {
			this.workbook = xssfWorkbook;
			getSheet();
			resolveColumnIndex();
			return readData();
			
		} catch (OLE2NotOfficeXmlFileException e) {
			throw new IllegalArgumentException(
				"Unsupported Excel file for table import. This reader requires a .xlsx workbook with an Excel Table; "
				+ "legacy .xls files must be imported with fromRaw(...).", e
			);
		} catch (NotOfficeXmlFileException e) {
			throw new IllegalArgumentException(
				"Unsupported Excel file for table import. The uploaded file is not a valid .xlsx OpenXML workbook.", e
			);
		} catch (EmptyFileException e) {
			throw new IllegalArgumentException("Unsupported Excel file for table import. The uploaded file is empty.", e);
		} catch (IOException e) {
            throw new IllegalStateException("Failed to read workbook for small-file import due to an I/O error.", e);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected failure during small-file Excel import.", e);
		}
	}
	
	private void getSheet() {
		this.sheet = workbook.getSheet(sheetName);
		if(sheet == null) {
			throw new IllegalArgumentException("Worksheet not found: " + sheetName + ".");
		}
	}
	
	private void resolveColumnIndex() {
		Row row = sheet.getRow(tableDefinition.getStartRowIndex());
		if(row == null) {
			throw new IllegalArgumentException(
				"Header rown is not found in sheet " + sheetName + "."
			);
		}
		for(int headerIdx = 0; headerIdx < row.getPhysicalNumberOfCells(); headerIdx++) {
			Cell cell = row.getCell(headerIdx);
			if(cell != null) {
				columnIdxs.put(Converter.readCellValue(cell), headerIdx);
			}
		}
		for(var columnDefinition : tableDefinition.getColumns()) {
			var columnIdx = columnIdxs.get(columnDefinition.header());
			if(columnIdx == null && !columnDefinition.ignoreNotFound()) {
				throw new IllegalArgumentException(
					"Column " + columnDefinition.header() + 
					" is not found in sheet " + sheetName + "."
				);
			}
		}
	}

	private List<ImportResult<T>> readData() {
		List<ImportResult<T>> results = new ArrayList<>();
		int dataStartRow = tableDefinition.getStartRowIndex()+2;
		for (int rowIndex = dataStartRow; rowIndex <= sheet.getPhysicalNumberOfRows(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null) {
				continue;
			}
			results.add(readRowData(row));
		}
		return results;
	}
	
	private ImportResult<T> readRowData(Row row) {
		T dtoInstance;
		try {
		    dtoInstance = tableDefinition.getDtoClass().getDeclaredConstructor().newInstance();
		} catch (Exception e) {
		    throw new IllegalStateException("Failed to create DTO instance.", e);
		}
		Map<String, String> error = new HashMap<>();
		for(var columnDefinition : tableDefinition.getColumns()) {
			var colIdx = columnIdxs.get(columnDefinition.header());
			if(colIdx == null) {
				continue;
			}
			Cell cell = row.getCell(colIdx);
			try {
				Util.setColumnValue(dtoInstance, columnDefinition, cell);
		    } catch (Exception e) {
		    	String message = columnDefinition.header() + ":" + e.getMessage();
		        error.compute(columnDefinition.header(), (k, v) -> v != null ? v + ", " + message : message);
		    }
		}
		return new ImportResult<>(dtoInstance, error);
	}
	
}
