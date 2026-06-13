package com.aji_prayitno.excel.exporter.model.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.aji_prayitno.excel.exporter.model.DataType;

public class ExcelTableDefinition<T> {

	private String name;
	private List<ExcelTableColumnDefinition<T>> columns = new ArrayList<>();

	private Class<?> dataClass;
	private DataType dataType;
	private List<T> data = new ArrayList<>();
	private Iterator<T> dataIterator;
	private Stream<T> dataStream;
	
	public ExcelTableDefinition() {}
	public ExcelTableDefinition(
		String name, List<ExcelTableColumnDefinition<T>> columns, Class<T> dataClass,
		DataType dataType, List<T> data, Iterator<T> dataIterator, Stream<T> dataStream
	) {
		this.name = name;
		this.columns = columns;
		this.dataClass = dataClass;
		this.dataType = dataType;
		this.data = data;
		this.dataIterator = dataIterator;
		this.dataStream = dataStream;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ExcelTableColumnDefinition<T>> getColumns() {
		return columns;
	}
	public void setColumns(List<ExcelTableColumnDefinition<T>> columns) {
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
}