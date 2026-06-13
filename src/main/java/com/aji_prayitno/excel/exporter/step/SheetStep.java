package com.aji_prayitno.excel.exporter.step;

import com.aji_prayitno.excel.exporter.step.border.ManualTableStep.ManualTableConfigurer;
import com.aji_prayitno.excel.exporter.step.table.ExcelTableStep.ExcelTableConfigurer;

public interface SheetStep {

	SheetStep title(String title);
	<T> void addTable(Class<T> dataClass, ManualTableConfigurer<T> configurer);
	<T> void addTable(String tableName, Class<T> dataClass, ExcelTableConfigurer<T> configurer);
	
	@FunctionalInterface
	public interface SheetStepConfigurer {
		void configure(SheetStep sheetStep);
	}
	
}
