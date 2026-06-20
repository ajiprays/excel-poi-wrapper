package com.aji_prayitno.excel.exporter.step;

import java.io.OutputStream;


public interface BuildStep {
	
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

	/**
	 * Builds the workbook and returns its binary Excel content.
	 *
	 * @return generated workbook bytes
	 */
	byte[] build();

	/**
	 * Builds the workbook and writes it to the provided output stream.
	 *
	 * @param out destination stream for the generated workbook
	 */
	void build(OutputStream out);
	
}
