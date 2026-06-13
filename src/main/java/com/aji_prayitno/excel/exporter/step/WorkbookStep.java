package com.aji_prayitno.excel.exporter.step;

import java.io.OutputStream;

import com.aji_prayitno.excel.exporter.step.SheetStep.SheetStepConfigurer;

public interface WorkbookStep {

	WorkbookStep addSheet(String sheetName, SheetStepConfigurer configurer);
	
	byte[] build();
	void build(OutputStream out);
}