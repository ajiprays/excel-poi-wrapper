package com.aji_prayitno.excel.exporter.core.render.table;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.core.render.HeaderRenderer;
import com.aji_prayitno.excel.exporter.core.render.RenderContext;
import com.aji_prayitno.excel.exporter.model.SheetDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableDefinition;
import com.aji_prayitno.excel.exporter.style.StyleKey.BorderStyleType;
import com.aji_prayitno.excel.exporter.style.StyleRegistry;

public final class ExcelTableHeaderRenderer implements HeaderRenderer {
	
	private final Logger logger = LoggerFactory.getLogger(ExcelTableHeaderRenderer.class);
	private final StyleRegistry styles;

	public ExcelTableHeaderRenderer(StyleRegistry styles) {
		this.styles = styles;
	}

	@Override
	public int render(RenderContext context, SheetDefinition sheetDefinition, int lastRowIndex) {
		return render(context, sheetDefinition.getExcelTable(), lastRowIndex);
	}
	
	private <T> int render(RenderContext context, ExcelTableDefinition<T> tableDefinition, int lastRowIndex) {
		logger.debug("render sheet {} header", context.getSheet().getSheetName());
		Sheet sheet = context.getSheet();
		int startCol = 0;
		for (ExcelTableColumnDefinition<T> columnDefinition : tableDefinition.getColumns()) {
			logger.debug("render header row:{} column:{}", lastRowIndex, columnDefinition.getHeader());
			Row row = CellUtil.getRow(lastRowIndex, sheet);
			Cell cell = CellUtil.getCell(row, startCol);
			cell.setCellValue(columnDefinition.getHeader());
			cell.setCellStyle(styles.header(BorderStyleType.NONE, BorderStyle.NONE));
		    startCol++;
		}	
		lastRowIndex++;
		return lastRowIndex;
	}
	
}