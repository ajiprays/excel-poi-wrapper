package com.aji_prayitno.excel.exporter.model.border;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;

import com.aji_prayitno.excel.exporter.model.DataType;
import com.aji_prayitno.excel.exporter.style.TableStyle;

public class ManualTableDefinition<T> {

	private TableStyle tableStyle;
	private BorderStyle borderStyle;
	
	private List<ManualTableColumnDefinition<T>> columns = new ArrayList<>();
	private Class<?> dataClass;
	private DataType dataType;
	private List<T> data = new ArrayList<>();
	private Iterator<T> dataIterator;
	private Stream<T> dataStream;
	private List<ManualTableSummaryDefinition> summaries = new ArrayList<>();
	
	public TableStyle getTableStyle() {
		return tableStyle;
	}
	public void setTableStyle(TableStyle tableStyle) {
		this.tableStyle = tableStyle;
	}
	public BorderStyle getBorderStyle() {
		if(borderStyle == null && isBordered()) {
			return BorderStyle.THIN;
		}
		return borderStyle;
	}
	public void setBorderStyle(BorderStyle borderStyle) {
		this.borderStyle = borderStyle;
	}
	public List<ManualTableColumnDefinition<T>> getColumns() {
		return columns;
	}
	public void setColumns(List<ManualTableColumnDefinition<T>> columns) {
		this.columns = columns;
	}
	public Class<?> getDataClass() {
		return dataClass;
	}
	public void setDataClass(Class<?> dataClass) {
		this.dataClass = dataClass;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public Iterator<T> getDataIterator() {
		return dataIterator;
	}
	public void setDataIterator(Iterator<T> dataIterator) {
		this.dataIterator = dataIterator;
	}
	public Stream<T> getDataStream() {
		return dataStream;
	}
	public void setDataStream(Stream<T> dataStream) {
		this.dataStream = dataStream;
	}
	public List<ManualTableSummaryDefinition> getSummaries() {
		return summaries;
	}
	public void setSummaries(List<ManualTableSummaryDefinition> summaries) {
		this.summaries = summaries;
	}
	public boolean isGrid() {
		return TableStyle.GRID.equals(tableStyle);
	}
	public boolean isBordered() {
		return tableStyle != null && !TableStyle.NONE.equals(tableStyle);
	}
	
}