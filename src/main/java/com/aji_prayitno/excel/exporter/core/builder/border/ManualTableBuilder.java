package com.aji_prayitno.excel.exporter.core.builder.border;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;

import com.aji_prayitno.excel.exporter.model.DataType;
import com.aji_prayitno.excel.exporter.model.border.HeaderCell;
import com.aji_prayitno.excel.exporter.model.border.ManualTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.border.ManualTableDefinition;
import com.aji_prayitno.excel.exporter.step.border.ManualTableStep;
import com.aji_prayitno.excel.exporter.step.border.ManualTableStep.SummaryStep;
import com.aji_prayitno.excel.exporter.style.TableStyle;

public final class ManualTableBuilder<T> implements ManualTableStep<T>, SummaryStep  {
	
	private ManualTableDefinition<T> tableDefinition = new ManualTableDefinition<>();
	
	@Override
	public ManualTableStep<T> tableStyle(TableStyle tableStyle) {
		tableDefinition.setTableStyle(tableStyle);
		return this;
	}
			
	@Override
	public ManualTableStep<T> borderStyle(BorderStyle borderStyle) {
		tableDefinition.setBorderStyle(borderStyle);
		return this;
	}
	
	@Override
	public ManualTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper) {
	    ManualTableColumnDefinition<T> columnDefinition = new ManualTableColumnDefinition<>(
    		HeaderCell.of(columnHeader), mapper
		);
	    tableDefinition.getColumns().add(columnDefinition);
	    return this;
	}
	
	@Override
	public ManualTableStep<T> addColumn(ColumnConfigurer<T> columnConfigurer, Function<T, ?> mapper) {
		return addColumn(columnConfigurer, mapper, null);
	}
	
	@Override
	public ManualTableStep<T> addColumn(
		ColumnConfigurer<T> columnConfigurer, Function<T, ?> mapper,
		ColumnConfigConfigurer<T> configConfigurer
	) {
		ManualTableColumnBuilder<T> columnBuilder = new ManualTableColumnBuilder<>();
	    columnConfigurer.configure(columnBuilder);
	    
	    ManualTableColumnDefinition<T> columnDefinition = new ManualTableColumnDefinition<>(
    		HeaderCell.of(columnBuilder.build().toArray(new String[0])), mapper
		);	    
	    if(configConfigurer != null) {
		    ManualTableColumnConfigBuilder<T> configBuilder = new ManualTableColumnConfigBuilder<>();
		    configConfigurer.configure(configBuilder);	    	
		    configBuilder.build(columnDefinition);
	    }

	    tableDefinition.getColumns().add(columnDefinition);
	    return this;
	}
	
	@Override
	public SummaryStep addData(List<T> data) {
		tableDefinition.setDataType(DataType.LIST);
		tableDefinition.setData(data);
		return this;
	}
	@Override
	public SummaryStep addData(Iterator<T> data) {
		tableDefinition.setDataType(DataType.ITERATOR);
		tableDefinition.setDataIterator(data);
		return this;
	}
	@Override
	public SummaryStep addData(Stream<T> data) {
		tableDefinition.setDataType(DataType.STREAM);
		tableDefinition.setDataStream(data);
		return this;
	}
	
	@Override
	public void addSummary(SummaryConfigurer configurer) {
	    SummaryBuilder builder = new SummaryBuilder();
	    configurer.configure(builder);
	    tableDefinition.getSummaries().addAll(builder.build());
	}

	public ManualTableDefinition<T> build() {
		if(tableDefinition.getColumns().isEmpty()) {
            throw new IllegalStateException("column is required");
		}
		return tableDefinition;
	}
}