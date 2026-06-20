package com.aji_prayitno.excel.exporter.step.border;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;

import com.aji_prayitno.excel.exporter.core.builder.border.ManualTableColumnBuilder;
import com.aji_prayitno.excel.exporter.core.builder.border.ManualTableColumnConfigBuilder;
import com.aji_prayitno.excel.exporter.core.builder.border.SummaryBuilder;
import com.aji_prayitno.excel.exporter.style.TableStyle;

public interface ManualTableStep<T> {

	/**
	 * Sets the table style.
	 *
	 * @param tableStyle table style to apply
	 * @return this manual table step for fluent configuration
	 */
	ManualTableStep<T> tableStyle(TableStyle tableStyle);

	/**
	 * Sets the border style for table cells.
	 *
	 * @param borderStyle border style to apply
	 * @return this manual table step for fluent configuration
	 */
	ManualTableStep<T> borderStyle(BorderStyle borderStyle);

	/**
	 * Adds a simple column to the manual table.
	 *
	 * @param columnHeader column header text
	 * @param mapper mapper used to extract the cell value from each row object
	 * @return this manual table step for fluent configuration
	 */
	ManualTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper);

	/**
	 * Adds a column configured through a column builder.
	 *
	 * @param column callback used to configure the column header structure
	 * @param mapper mapper used to extract the cell value from each row object
	 * @return this manual table step for fluent configuration
	 */
	ManualTableStep<T> addColumn(ColumnConfigurer<T> column, Function<T, ?> mapper);

	/**
	 * Adds a column configured through column and cell configuration callbacks.
	 *
	 * @param column callback used to configure the column header structure
	 * @param mapper mapper used to extract the cell value from each row object
	 * @param config callback used to configure column rendering options
	 * @return this manual table step for fluent configuration
	 */
	ManualTableStep<T> addColumn(ColumnConfigurer<T> column, Function<T, ?> mapper, ColumnConfigConfigurer<T> config);

	/**
	 * Adds all table rows from a list.
	 *
	 * @param data row data
	 * @return summary configuration step
	 */
	SummaryStep addData(List<T> data);

	/**
	 * Adds all table rows from an iterator.
	 *
	 * @param data row data iterator
	 * @return summary configuration step
	 */
	SummaryStep addData(Iterator<T> data);

	/**
	 * Adds all table rows from a stream.
	 *
	 * @param data row data stream
	 * @return summary configuration step
	 */
	SummaryStep addData(Stream<T> data);
	
	@FunctionalInterface
	public interface ManualTableConfigurer<T> {
		/**
		 * Configures a manual table.
		 *
		 * @param tableStep manual table step to configure
		 */
		void configure(ManualTableStep<T> tableStep);
	}
	
	public interface SummaryStep {
		/**
		 * Adds summary configuration to the manual table.
		 *
		 * @param summaryConfigurer callback used to configure summaries
		 */
		void addSummary(SummaryConfigurer summaryConfigurer);
	}
	
	@FunctionalInterface
	public interface ColumnConfigurer<T> {
		/**
		 * Configures a manual table column.
		 *
		 * @param column column builder
		 */
		void configure(ManualTableColumnBuilder<T> column);
	}
	@FunctionalInterface
	public interface ColumnConfigConfigurer<T> {
		/**
		 * Configures rendering options for a manual table column.
		 *
		 * @param config column configuration builder
		 */
		void configure(ManualTableColumnConfigBuilder<T> config);
	}
	@FunctionalInterface
	public interface SummaryConfigurer{
		/**
		 * Configures manual table summary rows or cells.
		 *
		 * @param summaryStep summary builder
		 */
		void configure(SummaryBuilder summaryStep);
	}
}
