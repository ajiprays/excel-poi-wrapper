package com.aji_prayitno.excel.importer.core.reader.table.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableColumn;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.aji_prayitno.excel.importer.core.Converter;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.table.TableDefinition;

public final class SimpleTableReader {

	private SimpleTableReader() {}
	public static <T> List<ImportResult<T>> read(SheetDefinition<T> sheetDefinition) {
		try (
			XSSFWorkbook workbook = new XSSFWorkbook(sheetDefinition.getInputStream())
		) {
			XSSFSheet sheet = getSheet(sheetDefinition.getSheetName(), workbook);
			if(sheet == null) {
				throw new IllegalArgumentException("Worksheet not found: '" + sheetDefinition.getSheetName() + "'.");
			}
			TableDefinition<T> tableDefinition = sheetDefinition.getTable();
			XSSFTable table = getTable(tableDefinition.getTableName(), sheet)
					.orElseThrow(() -> 
						new IllegalArgumentException("Table '" + tableDefinition.getTableName()
							+ "' was not found in worksheet '" + sheetDefinition.getSheetName() + "'.")
					);
			
			return readTable(tableDefinition, table);
			
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
	
	private static XSSFSheet getSheet(String sheetName, XSSFWorkbook workbook) {
		return workbook.getSheet(sheetName);
	}
	private static Optional<XSSFTable> getTable(String tableName, XSSFSheet sheet) {
		return sheet.getTables().stream()
				.filter(t -> t.getName().equals(tableName))
				.findFirst();
	}
	private static Optional<XSSFTableColumn> getColumn(String columnName, XSSFTable table) {
		return table.getColumns().stream().filter(c -> c.getName().equals(columnName))
				.findFirst();
	}
	
	private static <T> List<ImportResult<T>> readTable(TableDefinition<T> tableDefinition, XSSFTable table) {
		XSSFSheet sheet = table.getXSSFSheet();
		AreaReference area = table.getArea();
		CellReference firstCell = area.getFirstCell();
		CellReference lastCell = area.getLastCell();

		int firstRow = firstCell.getRow();
		int lastRow = lastCell.getRow();

		Map<String, Integer> columnIdx = new HashMap<>();
		tableDefinition.getColumns().forEach(columnDefinition -> 
			columnIdx.putIfAbsent(
				columnDefinition.getHeader(), 
				getColumn(columnDefinition.getHeader(), table).orElseThrow(() -> 
					new IllegalArgumentException("Column " + columnDefinition.getHeader() + 
						" was not found in table " + tableDefinition.getTableName() + "."
					)
				)
				.getColumnIndex()
			)
		);
		
		List<ImportResult<T>> result = new ArrayList<>();
		int dataStartRow = firstRow + 1;
		for (int rowIndex = dataStartRow; rowIndex <= lastRow; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null) {
				continue;
			}
	        T dtoInstance;
	        try {
	            dtoInstance = tableDefinition.getDtoClass().getDeclaredConstructor().newInstance();
	        } catch (Exception e) {
	            throw new IllegalStateException(
	                    "Failed to create DTO instance.",
	                    e
	            );
	        }
			Map<String, String> error = new HashMap<>();
			for(var columnDefinition : tableDefinition.getColumns()) {
				Cell cell = row.getCell(columnIdx.get(columnDefinition.getHeader()));
				try {
					setColumnValue(dtoInstance, columnDefinition, cell);
	            } catch (Exception e) {
	            	String message = columnDefinition.getHeader() + ":" + e.getMessage();
	                error.compute(columnDefinition.getHeader(), (k, v) -> v != null ? v + ", " + message : message);
	            }
			}
			result.add(new ImportResult<>(dtoInstance, error));
		}
		return result;
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
