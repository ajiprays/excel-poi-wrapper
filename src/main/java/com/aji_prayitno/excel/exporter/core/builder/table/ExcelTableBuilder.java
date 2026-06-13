package com.aji_prayitno.excel.exporter.core.builder.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.aji_prayitno.excel.exporter.model.DataType;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableDefinition;
import com.aji_prayitno.excel.exporter.step.table.ExcelTableStep;

public final class ExcelTableBuilder<T> implements ExcelTableStep<T> {
	
	private final ExcelTableDefinition<T> tableDefinition = new ExcelTableDefinition<>();
	private List<ExcelTableColumnDefinition<T>> columnDefinitions = new ArrayList<>();
	
	
	@Override
	public ExcelTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper) {
		return addColumn(columnHeader, mapper, null);
	}
	
	@Override
	public ExcelTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper, ColumnConfigurer<T> columnConfigurer) {
		if (mapper == null) {
            throw new IllegalArgumentException("Column mapper cannot be null");
        }
		if (columnHeader == null || columnHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Column header cannot be empty");
        }
		ExcelTableColumnDefinition<T> definition = new ExcelTableColumnDefinition<>();
		definition.header(columnHeader);
		definition.mapper(mapper);
		
		if(columnConfigurer != null) {
			ExcelTableColumnConfigBuilder<T> columnconfigBuilder = new ExcelTableColumnConfigBuilder<>();
			columnConfigurer.configure(columnconfigBuilder);
			columnconfigBuilder.build(definition);
		}
		columnDefinitions.add(definition);
		return this;
	}

	@Override
	public SummaryStep addData(List<T> data) {
		tableDefinition.setDataType(DataType.LIST);
		tableDefinition.setData(data);
		return null;
	}

	@Override
	public SummaryStep addData(Iterator<T> data) {
		tableDefinition.setDataType(DataType.ITERATOR);
		tableDefinition.setDataIterator(data);
		return null;
	}

	@Override
	public SummaryStep addData(Stream<T> data) {
		tableDefinition.setDataType(DataType.STREAM);
		tableDefinition.setDataStream(data);
		return null;
	}
	
	public ExcelTableDefinition<T> build() {
		if(columnDefinitions.isEmpty()) {
            throw new IllegalStateException("column is required");
		}
		tableDefinition.setColumns(columnDefinitions);
		return tableDefinition;
	}
}