package com.aji_prayitno.excel.importer.core.builder;

import java.io.InputStream;

import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.table.TableDefinition;
import com.aji_prayitno.excel.importer.model.xls.XlsTableDefinition;
import com.aji_prayitno.excel.importer.step.ColumnStep.ColumnStepConfigure;
import com.aji_prayitno.excel.importer.step.TableStep;
import com.aji_prayitno.excel.importer.step.table.WorkbookStep;
import com.aji_prayitno.excel.importer.step.xls.XlsWorkbookStep;

public final class TableBuilder implements TableStep {

	private final InputStream inputStream;
	private final String sheetName;
	
	public TableBuilder(InputStream inputStream, String sheetName) {
		this.inputStream = inputStream;
		this.sheetName = sheetName;
	}

	@Override
    public <T> WorkbookStep<T> fromTable(String tableName, Class<T> dtoClass, ColumnStepConfigure<T> columnStep) {
        ColumnBuilder<T> columnBuilder = new ColumnBuilder<>();
        columnStep.configure(columnBuilder);
        TableDefinition<T> tableDefinition = new TableDefinition<>();
        tableDefinition.setTableName(tableName);
        tableDefinition.setDtoClass(dtoClass);
        tableDefinition.setColumns(columnBuilder.build());
        SheetDefinition<T> sheetDefinition = new SheetDefinition<>(inputStream);
        sheetDefinition.setSheetName(sheetName);
        sheetDefinition.addTable(tableDefinition);
    	return new WorkbookBuilder<>(sheetDefinition);
    }
    
	@Override
	public <T> XlsWorkbookStep<T> fromRaw(int startRowIndex, Class<T> dtoClass, ColumnStepConfigure<T> columnStep) {
		ColumnBuilder<T> columnBuilder = new ColumnBuilder<>();
        columnStep.configure(columnBuilder);
        XlsTableDefinition<T> tableDefinition = new XlsTableDefinition<>();
        tableDefinition.setStartRowIndex(startRowIndex);
        tableDefinition.setDtoClass(dtoClass);
        tableDefinition.setColumns(columnBuilder.build());
        SheetDefinition<T> sheetDefinition = new SheetDefinition<>(inputStream);
        sheetDefinition.setSheetName(sheetName);
        sheetDefinition.addXlsTable(tableDefinition);
    	return new WorkbookBuilder<>(sheetDefinition);
	}
}