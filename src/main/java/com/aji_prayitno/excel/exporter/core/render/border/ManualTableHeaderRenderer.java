package com.aji_prayitno.excel.exporter.core.render.border;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.core.builder.border.HeaderTreeBuilder;
import com.aji_prayitno.excel.exporter.core.render.HeaderRenderer;
import com.aji_prayitno.excel.exporter.core.render.RenderContext;
import com.aji_prayitno.excel.exporter.core.render.Util;
import com.aji_prayitno.excel.exporter.model.SheetDefinition;
import com.aji_prayitno.excel.exporter.model.border.HeaderNode;
import com.aji_prayitno.excel.exporter.model.border.ManualTableDefinition;
import com.aji_prayitno.excel.exporter.style.StyleKey.BorderStyleType;
import com.aji_prayitno.excel.exporter.style.StyleRegistry;

public final class ManualTableHeaderRenderer implements HeaderRenderer {
	
	private final Logger logger = LoggerFactory.getLogger(ManualTableHeaderRenderer.class);
	private final StyleRegistry styles;

	
	public ManualTableHeaderRenderer(StyleRegistry styles) {
		this.styles = styles;
	}

	@Override
	public int render(RenderContext context, SheetDefinition sheetDefinition, int lastRowIndex) {
		return render(context, sheetDefinition.getManualTable(), lastRowIndex);
	}
	
	private int render(RenderContext context, ManualTableDefinition<?> tableDefinition, int lastRowIndex) {
		logger.debug("render sheet {} header", context.getSheet().getSheetName());
		Sheet sheet = context.getSheet();
		int totalColumn = tableDefinition.getColumns().size();

		boolean isGrid = tableDefinition.isGrid();
		boolean isBorder = tableDefinition.isBordered();
		int headerStartRow = lastRowIndex;
		HeaderNode root = HeaderTreeBuilder.build(tableDefinition.getColumns());
		List<List<HeaderNode>> levels = HeaderTreeBuilder.levels(root);
		for (List<HeaderNode> level : levels) {
			logger.debug("render header row {}", lastRowIndex);
			Row row = CellUtil.getRow(lastRowIndex, sheet);
		    for (HeaderNode node : level) {
		    	renderNode(sheet, row, node, lastRowIndex, styles.header(
		    			isGrid ? BorderStyleType.FULL : BorderStyleType.NONE, tableDefinition.getBorderStyle()
	    			));
		    }

		    lastRowIndex++;
		}	
		if(isGrid) {
			fillHeaderGrid(
				sheet, headerStartRow, lastRowIndex - 1, totalColumn, 
				styles.header(BorderStyleType.FULL, tableDefinition.getBorderStyle())
			);			
		}else if(isBorder) {
			Util.applyBorder(
				sheet, 
				new CellRangeAddress(
					headerStartRow, lastRowIndex - 1, 0, totalColumn - 1
				), 
				tableDefinition.getBorderStyle()
			);
		}
		return lastRowIndex;
	}

	private void renderNode(Sheet sheet, Row row, HeaderNode node, int lastRowIndex, CellStyle style) {
		int startCol = node.getStartColumn();
		int endCol = startCol + node.getColSpan() - 1;
		int endRow = lastRowIndex + node.getRowSpan() - 1;

		logger.debug(
			"render node '{}' row={} col={} rowSpan={} colSpan={}", 
			node.getLabel(), lastRowIndex, startCol,
			node.getRowSpan(), node.getColSpan()
		);

		Cell cell = CellUtil.getCell(row, startCol);
		cell.setCellValue(node.getLabel());
		cell.setCellStyle(style);

		Util.fillRegionStyle(sheet, lastRowIndex, endRow, startCol, endCol, style);
		Util.merge(sheet, lastRowIndex, endRow, startCol, endCol);
	}

	private void fillHeaderGrid(
		Sheet sheet, 
		int firstRow, int lastRow, 
		int totalColumns, 
		CellStyle style
	) {
		for (int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++) {
			Row row = CellUtil.getRow(rowIndex, sheet);
			for (int colIndex = 0; colIndex < totalColumns; colIndex++) {
				Cell cell = CellUtil.getCell(row, colIndex);
				cell.setCellStyle(style);
			}
		}
	}

}