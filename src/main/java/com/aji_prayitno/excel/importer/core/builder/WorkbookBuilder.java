package com.aji_prayitno.excel.importer.core.builder;

import java.util.List;
import java.util.stream.Stream;

import com.aji_prayitno.excel.importer.core.reader.table.WorkbookSheetReader;
import com.aji_prayitno.excel.importer.core.reader.table.simple.SimpleTableReader;
import com.aji_prayitno.excel.importer.core.reader.xls.SimpleXlsTableReader;
import com.aji_prayitno.excel.importer.core.reader.xls.XlsReader;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.step.table.WorkbookStep;
import com.aji_prayitno.excel.importer.step.xls.XlsWorkbookStep;

public class WorkbookBuilder<T> implements WorkbookStep<T>, XlsWorkbookStep<T> {
	private final SheetDefinition<T> sheetDefinition;
	
	public WorkbookBuilder(SheetDefinition<T> sheetDefinition) {
		this.sheetDefinition = sheetDefinition;
	}
	@Override
	public List<ImportResult<T>> importData() {
		try(Stream<ImportResult<T>> stream = importDataAsStream()){
			return stream.toList();
		}
	}
	
	@Override
	public Stream<ImportResult<T>> importDataAsStream() {
		if(Boolean.TRUE.equals(sheetDefinition.getIsXls())) {
			try {
				return XlsReader.read(
					sheetDefinition.getInputStream(), 
					sheetDefinition.getSheetName(), 
					sheetDefinition.getXlsTable()
				);
			} catch (Exception e) {
				throw new IllegalStateException("error reading xls file", e);
			}
		}
		WorkbookSheetReader workbookSheetReader = new WorkbookSheetReader();
		return workbookSheetReader.importAsStream(sheetDefinition);
	}
	
	@Override
	public List<ImportResult<T>> importDataSmallFile() {
		if(Boolean.TRUE.equals(sheetDefinition.getIsXls())) {
			return SimpleXlsTableReader.read(sheetDefinition);
		}
		return SimpleTableReader.read(sheetDefinition);
	}
}
