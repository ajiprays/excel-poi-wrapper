package com.aji_prayitno.excel.importer.core.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.eventusermodel.XSSFReader;

import com.aji_prayitno.excel.importer.core.Converter;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;

public class Util {

	private Util() {}

	public static InputStream findSheetStream(XSSFReader reader, String sheetName) {
		XSSFReader.SheetIterator sheetIterator;
		try {
			sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
		} catch (InvalidFormatException | IOException e) {
			throw new IllegalStateException("Failed to load sheet streams from the workbook package.", e);
		}
		while (sheetIterator.hasNext()) {
			InputStream is = sheetIterator.next();
			if (sheetName.equalsIgnoreCase(sheetIterator.getSheetName())) {
				return is;
			}
			try {
				is.close();
			} catch (IOException e) {
				throw new IllegalStateException(
					"Failed to close unselected worksheet stream while searching for sheet " + sheetName + ".", e
				);
			}
		}
		throw new IllegalArgumentException("sheet : " + sheetName + " is not found.");
	}
	
    public static int convertRefToCol(String ref) {
        int col = 0;
        for (int i = 0; i < ref.length(); i++) {
            char c = ref.charAt(i);
            if (Character.isDigit(c)) break;
            col = col * 26 + (c - 'A' + 1);
        }
        return col - 1;
    }
    
	@SuppressWarnings("unchecked")
	public static <V, T> void setColumnValue(
		T instance,
		ColumnDefinition<T, V> columnDefinition,
		Cell cell
	) {
		String rawValue = Converter.readCellValue(cell);
		if(columnDefinition.targetType() != null) {
	        V convertedValue = Converter.convert(rawValue, columnDefinition.targetType());
	        columnDefinition.setter().accept(instance, convertedValue);
	        return;
		}
		columnDefinition.setter().accept(instance, (V) rawValue);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> void setColumnValue(
		T instance, ColumnDefinition<T, V> columnDefinition,
		Map<String, String> rowValues
	) {
		String rawValue = rowValues.get(columnDefinition.header());
		if(columnDefinition.targetType() != null) {
	        V convertedValue = Converter.convert(rawValue, columnDefinition.targetType());
	        columnDefinition.setter().accept(instance, convertedValue);
	        return;
		}
		columnDefinition.setter().accept(instance, (V) rawValue);
	}
}
