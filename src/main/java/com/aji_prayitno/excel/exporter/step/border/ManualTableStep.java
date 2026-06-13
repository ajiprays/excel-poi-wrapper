package com.aji_prayitno.excel.exporter.step.border;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;

import com.aji_prayitno.excel.exporter.core.builder.border.ManualTableColumnConfigBuilder;
import com.aji_prayitno.excel.exporter.core.builder.border.ManualTableColumnBuilder;
import com.aji_prayitno.excel.exporter.core.builder.border.SummaryBuilder;
import com.aji_prayitno.excel.exporter.style.TableStyle;

public interface ManualTableStep<T> {

	ManualTableStep<T> tableStyle(TableStyle tableStyle);
	ManualTableStep<T> borderStyle(BorderStyle borderStyle);
	ManualTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper);
	ManualTableStep<T> addColumn(ColumnConfigurer<T> column, Function<T, ?> mapper);
	ManualTableStep<T> addColumn(ColumnConfigurer<T> column, Function<T, ?> mapper, ColumnConfigConfigurer<T> config);
	SummaryStep addData(List<T> data);
	SummaryStep addData(Iterator<T> data);
	SummaryStep addData(Stream<T> data);
	
	@FunctionalInterface
	public interface ManualTableConfigurer<T> {
		void configure(ManualTableStep<T> tableStep);
	}
	
	public interface SummaryStep {
		void addSummary(SummaryConfigurer summaryConfigurer);
	}
	
	@FunctionalInterface
	public interface ColumnConfigurer<T> {
		void configure(ManualTableColumnBuilder<T> column);
	}
	@FunctionalInterface
	public interface ColumnConfigConfigurer<T> {
		void configure(ManualTableColumnConfigBuilder<T> config);
	}
	@FunctionalInterface
	public interface SummaryConfigurer{
		void configure(SummaryBuilder summaryStep);
	}
}