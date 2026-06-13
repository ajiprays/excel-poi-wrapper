package com.aji_prayitno.excel.exporter.step.table;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.aji_prayitno.excel.exporter.core.builder.border.SummaryBuilder;
import com.aji_prayitno.excel.exporter.core.builder.table.ExcelTableColumnConfigBuilder;

public interface ExcelTableStep<T> {

	ExcelTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper);
	ExcelTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper, ColumnConfigurer<T> config);
	SummaryStep addData(List<T> data);
	SummaryStep addData(Iterator<T> data);
	SummaryStep addData(Stream<T> data);
	
	@FunctionalInterface
	public interface ExcelTableConfigurer<T> {
		void configure(ExcelTableStep<T> tableStep);
	}
	
	public interface SummaryStep {
		void addSummary(SummaryConfigurer summaryConfigurer);
	}
	
	@FunctionalInterface
	public interface ColumnConfigurer<T> {
		void configure(ExcelTableColumnConfigBuilder<T> config);
	}
	@FunctionalInterface
	public interface SummaryConfigurer{
		void configure(SummaryBuilder summaryStep);
	}
}