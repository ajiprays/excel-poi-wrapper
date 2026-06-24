package com.aji_prayitno.excel.importer.core.reader;

import com.aji_prayitno.excel.importer.core.reader.xls.XlsWorkbookSheetReader;
import com.aji_prayitno.excel.importer.core.reader.xls.simple.SimpleXlsTableReader;
import com.aji_prayitno.excel.importer.core.reader.xlsx.plain.PlainWorkbookSheetReader;
import com.aji_prayitno.excel.importer.core.reader.xlsx.plain.simple.SimplePlainTableReader;
import com.aji_prayitno.excel.importer.core.reader.xlsx.table.TableWorkbookSheetReader;
import com.aji_prayitno.excel.importer.core.reader.xlsx.table.simple.SimpleTableReader;
import com.aji_prayitno.excel.importer.model.SheetDefinition;

public class ReaderFactory {

	private ReaderFactory() {}
	
	public static <T> BaseReader<T> getWorkBookReader(SheetDefinition<T> sheetDefinition, boolean isSimple) {
		if(Boolean.TRUE.equals(sheetDefinition.getIsXls())) {
			if(isSimple) {
				return new SimpleXlsTableReader<>(sheetDefinition);
			}
			return new XlsWorkbookSheetReader<>(sheetDefinition);
		}else if(Boolean.TRUE.equals(sheetDefinition.getIsPlain())) {
			if(isSimple) {
				return new SimplePlainTableReader<>(sheetDefinition);
			}
			return new PlainWorkbookSheetReader<>(sheetDefinition);
		}else if(isSimple) {
			return new SimpleTableReader<>(sheetDefinition);
		}
		return new TableWorkbookSheetReader<>(sheetDefinition);
	}
}
