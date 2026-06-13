package com.aji_prayitno.excel.exporter;

import com.aji_prayitno.excel.exporter.core.builder.ExcelBuilder;

public class ExcelService {

	public ExcelBuilder builder() {
		return new ExcelBuilder();
	}
}
