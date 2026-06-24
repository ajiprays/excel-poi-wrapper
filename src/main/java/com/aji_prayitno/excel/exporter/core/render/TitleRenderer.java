package com.aji_prayitno.excel.exporter.core.render;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.model.SheetDefinition;
import com.aji_prayitno.excel.exporter.style.StyleRegistry;

final class TitleRenderer {
	
	private final Logger logger = LoggerFactory.getLogger(TitleRenderer.class);
	private final StyleRegistry styles;

	public TitleRenderer(StyleRegistry styles) {
		this.styles = styles;
	}

	public int render(RenderContext context, SheetDefinition sheetDefinition, int lastRowIndex) {
		logger.debug("render sheet:{} title", context.getSheet().getSheetName());
		Sheet sheet = context.getSheet();
		int totalColumn = 0;
		if(Boolean.TRUE.equals(sheetDefinition.getIsManualTable())) {
			totalColumn = sheetDefinition.getManualTable().getColumns().size();
		}else {
			totalColumn = sheetDefinition.getExcelTable().getColumns().size();
		}
		
		for(String title : sheetDefinition.getTitles()) {
			Row titleRow = CellUtil.getRow(lastRowIndex, sheet);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(title);
			titleCell.setCellStyle(styles.title());
			
			Util.fillRegionStyle(sheet, lastRowIndex, lastRowIndex, 0, totalColumn - 1, styles.title());
			Util.merge(sheet, lastRowIndex, lastRowIndex, 0, totalColumn - 1);
			lastRowIndex++;
		}
		lastRowIndex++;

		return lastRowIndex;
	}

}