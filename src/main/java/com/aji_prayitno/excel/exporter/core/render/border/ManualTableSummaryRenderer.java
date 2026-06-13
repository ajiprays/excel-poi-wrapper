package com.aji_prayitno.excel.exporter.core.render.border;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.core.render.CellWriter;
import com.aji_prayitno.excel.exporter.core.render.RenderContext;
import com.aji_prayitno.excel.exporter.model.border.ManualTableDefinition;
import com.aji_prayitno.excel.exporter.model.border.ManualTableSummaryDefinition;
import com.aji_prayitno.excel.exporter.style.StyleKey.BorderStyleType;
import com.aji_prayitno.excel.exporter.style.StyleRegistry;

public final class ManualTableSummaryRenderer {
	private final Logger logger = LoggerFactory.getLogger(ManualTableSummaryRenderer.class);
	private final StyleRegistry styles;
	private final CellWriter cellWriter = new CellWriter();
	
	public ManualTableSummaryRenderer(StyleRegistry styles) {
		this.styles = styles;
	}

	public int render(RenderContext context, ManualTableDefinition<?> manualTableDefinition, int lastRowIndex) {
		if (manualTableDefinition.getSummaries().isEmpty()) {
			return lastRowIndex;
		}

		Row row = CellUtil.getRow(lastRowIndex, context.getSheet());
		int cellIndex = 0;
		for (ManualTableSummaryDefinition summary : manualTableDefinition.getSummaries()) {
			List<?> columns = manualTableDefinition.getColumns();
			for (int columnIdx = 0; columnIdx < columns.size(); columnIdx++) {
				if(columnIdx == 0) {
					Cell labelCell = CellUtil.getCell(row, cellIndex++);
					labelCell.setCellValue(summary.label());
					labelCell.setCellStyle(styles.summary(
							BorderStyleType.TOP_BOTTOM_LEFT, manualTableDefinition.getBorderStyle(), 
							HorizontalAlignment.LEFT
						));					
				}else if(columnIdx == columns.size() -1) {
					Cell valueCell = CellUtil.getCell(row, cellIndex++);
					cellWriter.write(valueCell, summary.value());
					valueCell.setCellStyle(styles.summary(
							BorderStyleType.TOP_BOTTOM_RIGHT, manualTableDefinition.getBorderStyle(), 
							HorizontalAlignment.RIGHT, summary.styleCustom()
						));								
				}else {
					Cell blankCell = CellUtil.getCell(row, cellIndex++);
					blankCell.setCellStyle(styles.summary(
							BorderStyleType.TOP_BOTTOM, manualTableDefinition.getBorderStyle(), 
							null
						));	
				}
	
			}
		}
		return lastRowIndex + 1;
	}

}