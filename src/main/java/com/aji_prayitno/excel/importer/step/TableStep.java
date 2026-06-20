package com.aji_prayitno.excel.importer.step;

import com.aji_prayitno.excel.importer.step.ColumnStep.ColumnStepConfigure;
import com.aji_prayitno.excel.importer.step.table.WorkbookStep;
import com.aji_prayitno.excel.importer.step.xls.XlsWorkbookStep;

public interface TableStep {
	
	/**
	 * Imports data from a named Excel table in an .xlsx worksheet.
	 *
	 * @param tableName Excel table name
	 * @param dtoClass target DTO class
	 * @param columnStep callback used to configure column mappings
	 * @param <T> target DTO type
	 * @return workbook import step
	 */
	<T> WorkbookStep<T> fromTable(String tableName, Class<T> dtoClass, ColumnStepConfigure<T> columnStep);

	/**
	 * Imports data from a raw legacy .xls worksheet using a header row index.
	 *
	 * @param startRowIndex zero-based row index containing column headers
	 * @param dtoClass target DTO class
	 * @param columnStep callback used to configure column mappings
	 * @param <T> target DTO type
	 * @return legacy .xls workbook import step
	 */
	<T> XlsWorkbookStep<T> fromRaw(int startRowIndex, Class<T> dtoClass, ColumnStepConfigure<T> columnStep);
}
