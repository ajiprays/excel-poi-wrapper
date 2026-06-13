package com.aji_prayitno.excel.exporter.core.render.border;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.core.render.CellWriter;
import com.aji_prayitno.excel.exporter.core.render.RenderContext;
import com.aji_prayitno.excel.exporter.model.border.ManualTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.border.ManualTableDefinition;
import com.aji_prayitno.excel.exporter.style.StyleKey.BorderStyleType;
import com.aji_prayitno.excel.exporter.style.StyleRegistry;

public final class ManualTableBodyRenderer {
	private final Logger logger = LoggerFactory.getLogger(ManualTableBodyRenderer.class);
	private final StyleRegistry styles;
	private final CellWriter cellWriter = new CellWriter();

	public ManualTableBodyRenderer(StyleRegistry styles) {
		this.styles = styles;
	}

	public <T> int render(RenderContext context, ManualTableDefinition<T> tableDefinition, int lastRowIndex) {
		switch (tableDefinition.getDataType()) {
		case LIST :
			return renderFromList(context, tableDefinition, lastRowIndex);
		case ITERATOR :
			return renderFromIterator(context, tableDefinition, lastRowIndex, tableDefinition.getDataIterator());
		case STREAM :
			return renderFromStream(context, tableDefinition, lastRowIndex);
		default:
			return lastRowIndex;
		}
	}

	public <T> int renderFromList(RenderContext context, ManualTableDefinition<T> tableDefinition, int lastRowIndex) {
		Sheet sheet = context.getSheet();
		List<ManualTableColumnDefinition<T>> columns = tableDefinition.getColumns();
		boolean isBordered = tableDefinition.isBordered();
		boolean isGrid = tableDefinition.isGrid();
		int columnSize = columns.size();
		int dataSize = tableDefinition.getData().size();
		for (int dataIdx = 0; dataIdx < dataSize; dataIdx++) {
			T item = tableDefinition.getData().get(dataIdx);
			Row row = CellUtil.getRow(lastRowIndex, sheet);
			for (int columnIdx = 0; columnIdx < columnSize; columnIdx++) {
				ManualTableColumnDefinition<T> column = columns.get(columnIdx);
				Cell cell = CellUtil.getCell(row, columnIdx);
				Object value = column.getMapper().apply(item);
				cellWriter.write(cell, value);
				cell.setCellStyle(styles.data(
					getBorderType(
						(dataIdx == 0), (dataIdx == dataSize - 1), 
						(columnIdx == 0), (columnIdx == columnSize - 1), 
						isBordered, isGrid
					),
					tableDefinition.getBorderStyle(),
					column
				));
			}
			lastRowIndex++;
		}
		applyColumnWidth(sheet, columns);
		return lastRowIndex;
	}
	
	public <T> int renderFromIterator(
		RenderContext context, ManualTableDefinition<T> tableDefinition, 
		int lastRowIndex, Iterator<T> data
	) {
		Sheet sheet = context.getSheet();
		List<ManualTableColumnDefinition<T>> columns = tableDefinition.getColumns();
		boolean isBordered = tableDefinition.isBordered();
		boolean isGrid = tableDefinition.isGrid();
		int columnSize = columns.size();
		int dataIdx = 0;
		while(data.hasNext()) {
			T item = data.next();
			Row row = CellUtil.getRow(lastRowIndex, sheet);
			for (int columnIdx = 0; columnIdx < columnSize; columnIdx++) {
				ManualTableColumnDefinition<T> column = columns.get(columnIdx);
				Cell cell = CellUtil.getCell(row, columnIdx);
				Object value = column.getMapper().apply(item);
				cellWriter.write(cell, value);
				cell.setCellStyle(styles.data(
					getBorderType(
						(dataIdx == 0), !data.hasNext(), 
						(columnIdx == 0), (columnIdx == columnSize - 1), 
						isBordered, isGrid
					),
					tableDefinition.getBorderStyle(),
					column
				));
			}
			dataIdx++;
			lastRowIndex++;
		}
		applyColumnWidth(sheet, columns);
		return lastRowIndex;
	}
	
	public <T> int renderFromStream(RenderContext context, ManualTableDefinition<T> tableDefinition, int lastRowIndex) {
		try(Stream<T> dataStream = tableDefinition.getDataStream()){
			Iterator<T> data = dataStream.iterator();
			return renderFromIterator(context, tableDefinition, lastRowIndex, data);
		}
	}
	
	private BorderStyleType getBorderType(
		boolean isTop, boolean isBottom, 
		boolean isLeft, boolean isRight, 
		boolean isBordered, boolean isGrid
	) {
		if(isGrid) {
			return BorderStyleType.FULL;
		}
		if(!isBordered) {
			return BorderStyleType.NONE;
		}
	    
	    if(isTop && isBottom && isLeft && isRight) {
	    	return BorderStyleType.FULL;
	    }
	    if(isLeft) {
	    	return getLeftBorder(isTop, isBottom);
	    }
	    if(isRight) {
	    	return getRightBorder(isTop, isBottom);
	    }
		
		if(isTop && isBottom) {
			return BorderStyleType.TOP_BOTTOM;
		}
		if(isTop) {
			return BorderStyleType.TOP;
		}
		if(isBottom) {
			return BorderStyleType.BOTTOM;
		}
		return BorderStyleType.NONE;
	}

	private BorderStyleType getRightBorder(boolean isTop, boolean isBottom) {
		if(isTop && isBottom) {
			return BorderStyleType.TOP_BOTTOM_RIGHT;
		}else if(isTop) {
			return BorderStyleType.TOP_RIGHT;
		}else if(isBottom) {
			return BorderStyleType.BOTTOM_RIGHT;
		}
		return BorderStyleType.RIGHT;
	}

	private BorderStyleType getLeftBorder(boolean isTop, boolean isBottom) {
		if(isTop && isBottom) {
			return BorderStyleType.TOP_BOTTOM_LEFT;
		}else if(isTop) {
			return BorderStyleType.TOP_LEFT;
		}else if(isBottom) {
			return BorderStyleType.BOTTOM_LEFT;
		}
		return BorderStyleType.LEFT;
	}

	private <T> void applyColumnWidth(Sheet sheet, List<ManualTableColumnDefinition<T>> columns) {
		for (int i = 0; i < columns.size(); i++) {
			ManualTableColumnDefinition<T> column = columns.get(i);
			if (column.getWidth() != null) {
				sheet.setColumnWidth(i, column.getWidth() * 256);
				continue;
			}
			if (column.isAutoSize()) {
				sheet.autoSizeColumn(i);
			}
		}
	}

}