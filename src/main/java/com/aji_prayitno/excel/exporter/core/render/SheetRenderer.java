package com.aji_prayitno.excel.exporter.core.render;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.model.SheetDefinition;
import com.aji_prayitno.excel.exporter.model.border.ManualTableDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableDefinition;


public final class SheetRenderer {
	private final Logger logger = LoggerFactory.getLogger(SheetRenderer.class);
	
	private final Workbook workbook;
	private final RendererFactory rendererFactory;

	public SheetRenderer(Workbook workbook) {
		this.workbook = workbook;
		this.rendererFactory = new RendererFactory(workbook);
	}

	public void render(SheetDefinition sheetDefinition) {
		RenderContext context = new RenderContext(workbook, workbook.createSheet(sheetDefinition.getSheetName()));
		Sheet sheet = context.getSheet();
		
		int currentRowIndex = 0;
		currentRowIndex = rendererFactory.titleRenderer().render(context, sheetDefinition, currentRowIndex);
		int startRowIndex = currentRowIndex + 1;
		if(Boolean.TRUE.equals(sheetDefinition.getIsManualTable())) {
			logger.debug("render sheet:{} manualtable", sheetDefinition.getSheetName());
			ManualTableDefinition<?> tableDefinition = sheetDefinition.getManualTable();
			boolean autoSizeFound = tableDefinition.getColumns().stream()
					.anyMatch(column -> column.isAutoSize() && column.getWidth() == null);
			if (autoSizeFound && sheet instanceof SXSSFSheet sxssfSheet) {
				sxssfSheet.trackAllColumnsForAutoSizing();
			}
			currentRowIndex = rendererFactory.manualTableHeaderRenderer().render(context, tableDefinition, currentRowIndex);	
			currentRowIndex = rendererFactory.manualTableBodyRenderer().render(context, tableDefinition, currentRowIndex);
			rendererFactory.summaryRenderer().render(context, tableDefinition, currentRowIndex);
		}else {
			logger.debug("render sheet:{} exceltable", sheetDefinition.getSheetName());
			ExcelTableDefinition<?> excelTableDefinition = sheetDefinition.getExcelTable(); 
			boolean autoSizeFound = excelTableDefinition.getColumns().stream()
					.anyMatch(column -> column.isAutoSize() && column.getWidth() == null);
			if (autoSizeFound && sheet instanceof SXSSFSheet sxssfSheet) {
				sxssfSheet.trackAllColumnsForAutoSizing();
			}
			currentRowIndex = rendererFactory.excelTableHeaderRenderer().render(context, excelTableDefinition, currentRowIndex);
			currentRowIndex = rendererFactory.excelTableBodyRenderer().render(context, excelTableDefinition, currentRowIndex);
			rendererFactory.excelTableRenderer().render(context, excelTableDefinition, startRowIndex-1, currentRowIndex-1);
		}
	}
}