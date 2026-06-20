package com.aji_prayitno.excel.exporter.step;

import java.io.OutputStream;

import com.aji_prayitno.excel.exporter.step.SheetStep.SheetStepConfigurer;

public interface WorkbookStep {

	/**
	 * Adds a worksheet to the workbook and configures its content.
	 *
	 * @param sheetName worksheet name
	 * @param configurer callback used to configure the worksheet
	 * @return this workbook step for fluent configuration
	 */
	WorkbookStep addSheet(String sheetName, SheetStepConfigurer configurer);
	
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
