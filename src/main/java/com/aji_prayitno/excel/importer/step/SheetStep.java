package com.aji_prayitno.excel.importer.step;

public interface SheetStep {
	
	/**
	 * Selects the worksheet to import from.
	 *
	 * @param sheetName worksheet name
	 * @return table selection step for the chosen worksheet
	 */
	TableStep fromSheet(String sheetName);
	
	@FunctionalInterface
	public interface SheetStepConfigure{
		/**
		 * Configures the worksheet selection step.
		 *
		 * @param sheetStep sheet selection step
		 */
		void configure(SheetStep sheetStep);
	}
}
