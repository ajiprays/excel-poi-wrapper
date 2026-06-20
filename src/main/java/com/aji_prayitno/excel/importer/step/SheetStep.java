package com.aji_prayitno.excel.importer.step;

public interface SheetStep {
	
	TableStep fromSheet(String sheetName);
	
	@FunctionalInterface
	public interface SheetStepConfigure{
		void configure(SheetStep sheetStep);
	}
}
