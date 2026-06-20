package com.aji_prayitno.excel.exporter.step;

import com.aji_prayitno.excel.exporter.step.border.ManualTableStep.ManualTableConfigurer;
import com.aji_prayitno.excel.exporter.step.table.ExcelTableStep.ExcelTableConfigurer;

public interface SheetStep {

	/**
	 * Sets the worksheet title.
	 *
	 * @param title title text to render on the worksheet
	 * @return this sheet step for fluent configuration
	 */
	SheetStep title(String title);

	/**
	 * Adds a raw table to the worksheet.
	 *
	 * @param dataClass row data type
	 * @param configurer callback used to configure the manual table
	 * @param <T> row data type
	 */
	<T> void addTable(Class<T> dataClass, ManualTableConfigurer<T> configurer);

	/**
	 * Adds an Excel table to the worksheet.
	 *
	 * @param tableName Excel table name
	 * @param dataClass row data type
	 * @param configurer callback used to configure the Excel table
	 * @param <T> row data type
	 */
	<T> void addTable(String tableName, Class<T> dataClass, ExcelTableConfigurer<T> configurer);
	
	@FunctionalInterface
	public interface SheetStepConfigurer {
		/**
		 * Configures a worksheet.
		 *
		 * @param sheetStep sheet step to configure
		 */
		void configure(SheetStep sheetStep);
	}
	
}
