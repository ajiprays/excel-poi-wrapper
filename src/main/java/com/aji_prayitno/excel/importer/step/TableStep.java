package com.aji_prayitno.excel.importer.step;

import com.aji_prayitno.excel.importer.step.ColumnStep.ColumnStepConfigure;
import com.aji_prayitno.excel.importer.step.table.WorkbookStep;
import com.aji_prayitno.excel.importer.step.xls.XlsWorkbookStep;

public interface TableStep {
	
	<T> WorkbookStep<T> fromTable(String tableName, Class<T> dtoClass, ColumnStepConfigure<T> columnStep);
	<T> XlsWorkbookStep<T> fromRaw(int startRowIndex, Class<T> dtoClass, ColumnStepConfigure<T> columnStep);
}
