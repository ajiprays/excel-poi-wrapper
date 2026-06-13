package com.aji_prayitno.excel.exporter.core.render;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class RenderContext {

	private final Workbook workbook;
	private final Sheet sheet;

	public RenderContext(Workbook workbook, Sheet sheet) {
		this.workbook = workbook;
		this.sheet = sheet;
	}
	public Workbook getWorkbook() {
		return workbook;
	}
	public Sheet getSheet() {
		return sheet;
	}
}