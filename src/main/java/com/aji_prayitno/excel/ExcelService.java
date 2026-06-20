package com.aji_prayitno.excel;

import com.aji_prayitno.excel.exporter.core.builder.ExcelBuilder;
import com.aji_prayitno.excel.importer.core.builder.ImportBuilder;

public class ExcelService {

	public ExcelBuilder exportBuilder() {
		return new ExcelBuilder();
	}
	
	public ImportBuilder importBuilder() {
		return new ImportBuilder();
	}
}
