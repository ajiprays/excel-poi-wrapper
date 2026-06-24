package com.aji_prayitno.excel.exporter.step;

public interface BuildStep extends WorkbookStep{
	
	/**
	 * Enables or disables streaming workbook generation.
	 *
	 * @param streaming {@code true} to use streaming mode, {@code false} otherwise
	 * @return this build step for fluent configuration
	 */
	BuildStep streaming(boolean streaming);

	/**
	 * Sets the number of rows kept in memory when streaming mode is enabled.
	 *
	 * @param rowAccessWindowSize row access window size
	 * @return this build step for fluent configuration
	 */
	BuildStep rowAccessWindowSize(int rowAccessWindowSize);

}
