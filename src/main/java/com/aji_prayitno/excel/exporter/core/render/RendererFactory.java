package com.aji_prayitno.excel.exporter.core.render;

import org.apache.poi.ss.usermodel.Workbook;

import com.aji_prayitno.excel.exporter.core.render.border.ManualTableBodyRenderer;
import com.aji_prayitno.excel.exporter.core.render.border.ManualTableHeaderRenderer;
import com.aji_prayitno.excel.exporter.core.render.border.ManualTableSummaryRenderer;
import com.aji_prayitno.excel.exporter.core.render.table.ExcelTableBodyRenderer;
import com.aji_prayitno.excel.exporter.core.render.table.ExcelTableHeaderRenderer;
import com.aji_prayitno.excel.exporter.core.render.table.ExcelTableRenderer;
import com.aji_prayitno.excel.exporter.style.StyleRegistry;

final class RendererFactory {

	private final StyleRegistry styles;

	public RendererFactory(Workbook workbook) {
		this.styles = new StyleRegistry(workbook);
	}
	public TitleRenderer titleRenderer() {
		return new TitleRenderer(styles);
	}
	public ManualTableHeaderRenderer manualTableHeaderRenderer() {
		return new ManualTableHeaderRenderer(styles);
	}
	public ManualTableBodyRenderer manualTableBodyRenderer() {
		return new ManualTableBodyRenderer(styles);
	}
	public ManualTableSummaryRenderer summaryRenderer() {
		return new ManualTableSummaryRenderer(styles);
	}
	public ExcelTableHeaderRenderer excelTableHeaderRenderer() {
		return new ExcelTableHeaderRenderer(styles);
	}
	public ExcelTableBodyRenderer excelTableBodyRenderer() {
		return new ExcelTableBodyRenderer(styles);
	}
	public ExcelTableRenderer excelTableRenderer() {
		return new ExcelTableRenderer();
	}
	public StyleRegistry styles() {
		return styles;
	}
}