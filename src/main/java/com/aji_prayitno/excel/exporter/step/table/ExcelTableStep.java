package com.aji_prayitno.excel.exporter.step.table;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.aji_prayitno.excel.exporter.core.builder.table.ExcelTableColumnConfigBuilder;

public interface ExcelTableStep<T> {

	/**
	 * Adds a column to the Excel table.
	 *
	 * @param columnHeader column header text
	 * @param mapper mapper used to extract the cell value from each row object
	 * @return this table step for fluent configuration
	 */
	ExcelTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper);

	/**
	 * Adds a column to the Excel table with additional column configuration.
	 *
	 * @param columnHeader column header text
	 * @param mapper mapper used to extract the cell value from each row object
	 * @param config callback used to configure the column
	 * @return this table step for fluent configuration
	 */
	ExcelTableStep<T> addColumn(String columnHeader, Function<T, ?> mapper, ColumnConfigurer<T> config);

	/**
	 * Adds all table rows from a list.
	 *
	 * @param data row data
	 */
	void addData(List<T> data);

	/**
	 * Adds all table rows from an iterator.
	 *
	 * @param data row data iterator
	 */
	void addData(Iterator<T> data);

	/**
	 * Adds all table rows from a stream.
	 *
	 * @param data row data stream
	 */
	void addData(Stream<T> data);
	
	@FunctionalInterface
	public interface ExcelTableConfigurer<T> {
		/**
		 * Configures an Excel table.
		 *
		 * @param tableStep table step to configure
		 */
		void configure(ExcelTableStep<T> tableStep);
	}
	
	
	@FunctionalInterface
	public interface ColumnConfigurer<T> {
		/**
		 * Configures a table column.
		 *
		 * @param config column configuration builder
		 */
		void configure(ExcelTableColumnConfigBuilder<T> config);
	}
	
}
