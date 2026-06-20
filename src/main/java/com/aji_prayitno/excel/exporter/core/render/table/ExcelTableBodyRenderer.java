package com.aji_prayitno.excel.exporter.core.render.table;

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
import com.aji_prayitno.excel.exporter.model.table.ExcelTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableDefinition;
import com.aji_prayitno.excel.exporter.style.StyleRegistry;

public final class ExcelTableBodyRenderer {
	private final Logger logger = LoggerFactory.getLogger(ExcelTableBodyRenderer.class);
	
	private final StyleRegistry styles;
	private final CellWriter cellWriter = new CellWriter();

	public ExcelTableBodyRenderer(StyleRegistry styles) {
		this.styles = styles;
	}

	public <T> int render(RenderContext context, ExcelTableDefinition<T> tableDefinition, int lastRowIndex) {
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

	public <T> int renderFromList(RenderContext context, ExcelTableDefinition<T> tableDefinition, int lastRowIndex) {
		Sheet sheet = context.getSheet();
		List<ExcelTableColumnDefinition<T>> columns = tableDefinition.getColumns();
		int columnSize = columns.size();
		int dataSize = tableDefinition.getData().size();
		for (int dataIdx = 0; dataIdx < dataSize; dataIdx++) {
			logger.debug("render body row:{} data index {}", lastRowIndex, dataIdx);
			T item = tableDefinition.getData().get(dataIdx);
			Row row = CellUtil.getRow(lastRowIndex, sheet);
			for (int columnIdx = 0; columnIdx < columnSize; columnIdx++) {
				ExcelTableColumnDefinition<T> column = columns.get(columnIdx);
				Cell cell = CellUtil.getCell(row, columnIdx);
				Object value = column.getMapper().apply(item);
				cellWriter.write(cell, value);
				cell.setCellStyle(styles.data(column));
			}
			lastRowIndex++;
		}
		applyColumnWidth(sheet, columns);
		return lastRowIndex;
	}
	
	public <T> int renderFromIterator(
		RenderContext context, ExcelTableDefinition<T> tableDefinition, 
		int lastRowIndex, Iterator<T> data
	) {
		Sheet sheet = context.getSheet();
		List<ExcelTableColumnDefinition<T>> columns = tableDefinition.getColumns();
		int columnSize = columns.size();
		while(data.hasNext()) {
			logger.debug("render body row:{}", lastRowIndex);
			T item = data.next();
			Row row = CellUtil.getRow(lastRowIndex, sheet);
			for (int columnIdx = 0; columnIdx < columnSize; columnIdx++) {
				ExcelTableColumnDefinition<T> column = columns.get(columnIdx);
				Cell cell = CellUtil.getCell(row, columnIdx);
				Object value = column.getMapper().apply(item);
				cellWriter.write(cell, value);
				cell.setCellStyle(styles.data(column));
			}
			lastRowIndex++;
		}
		applyColumnWidth(sheet, columns);
		return lastRowIndex;
	}
	
	public <T> int renderFromStream(RenderContext context, ExcelTableDefinition<T> tableDefinition, int lastRowIndex) {
		try(Stream<T> dataStream = tableDefinition.getDataStream()){
			Iterator<T> data = dataStream.iterator();
			return renderFromIterator(context, tableDefinition, lastRowIndex, data);
		}
	}

	private <T> void applyColumnWidth(Sheet sheet, List<ExcelTableColumnDefinition<T>> columns) {
		for (int i = 0; i < columns.size(); i++) {
			ExcelTableColumnDefinition<T> column = columns.get(i);
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