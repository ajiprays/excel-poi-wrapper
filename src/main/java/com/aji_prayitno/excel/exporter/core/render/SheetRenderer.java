package com.aji_prayitno.excel.exporter.core.render;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.model.SheetDefinition;


public final class SheetRenderer {
	private final Logger logger = LoggerFactory.getLogger(SheetRenderer.class);
	
	private final Workbook workbook;
	private final RendererFactory rendererFactory;

	public SheetRenderer(Workbook workbook) {
		this.workbook = workbook;
		this.rendererFactory = new RendererFactory(workbook);
	}

	public void render(SheetDefinition sheetDefinition) {
		logger.debug("render sheet:{}", sheetDefinition.getSheetName());
		RenderContext context = new RenderContext(workbook, workbook.createSheet(sheetDefinition.getSheetName()));
		Sheet sheet = context.getSheet();
		
		int currentRowIndex = 0;
		currentRowIndex = rendererFactory.titleRenderer()
				.render(context, sheetDefinition, currentRowIndex);
		int startRowIndex = currentRowIndex + 1;
		trackAutoSizeColumn(sheet, sheetDefinition);
		currentRowIndex = rendererFactory.headerRenderer(sheetDefinition.getIsManualTable())
				.render(context, sheetDefinition, currentRowIndex);	
		currentRowIndex = rendererFactory.bodyRenderer(sheetDefinition.getIsManualTable())
				.render(context, sheetDefinition, currentRowIndex);
		
		if(Boolean.TRUE.equals(sheetDefinition.getIsManualTable())) {
			rendererFactory.summaryRenderer().render(
				context, sheetDefinition.getManualTable(), currentRowIndex
			);
		}else {
			rendererFactory.excelTableRenderer().render(
				context, sheetDefinition.getExcelTable(), 
				startRowIndex-1, currentRowIndex-1
			);
		}
	}

	private void trackAutoSizeColumn(Sheet sheet, SheetDefinition sheetDefinition) {
		if (sheetDefinition.isAutoSizeFound() && sheet instanceof SXSSFSheet sxssfSheet) {
			sxssfSheet.trackAllColumnsForAutoSizing();
		}
	}
}