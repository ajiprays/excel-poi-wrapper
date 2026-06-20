package com.aji_prayitno.excel.importer.step;


public interface ColumnStep<T> {

	/**
	 * Adds a required column mapping.
	 *
	 * <p>If the header is not found in the source spreadsheet, the import fails
	 * before row data is processed. Use {@link #fromColumnIgnoreNotFound(String, DataSetter)}
	 * for optional columns.</p>
	 *
	 * @param header spreadsheet header name to read
	 * @param setter setter used to assign the converted cell value to the DTO
	 * @param <V> target value type accepted by the setter
	 * @return this step for fluent column configuration
	 */
	 <V> ColumnStep<T> fromColumn(String header, DataSetter<T, V> setter);

	/**
	 * Adds an optional column mapping.
	 *
	 * <p>If the header is not found in the source spreadsheet, this column is
	 * ignored and the import continues. If the header exists, values are converted
	 * and passed to the supplied setter.</p>
	 *
	 * @param header spreadsheet header name to read when present
	 * @param setter setter used to assign the converted cell value to the DTO
	 * @param <V> target value type accepted by the setter
	 * @return this step for fluent column configuration
	 */
	 <V> ColumnStep<T> fromColumnIgnoreNotFound(String header, DataSetter<T, V> setter);
	 
	/**
	 * Callback used by table and raw import steps to configure all column mappings.
	 *
	 * @param <T> target DTO type produced by the importer
	 */
	@FunctionalInterface
	public interface ColumnStepConfigure<T>{

		/**
		 * Registers column mappings on the provided {@link ColumnStep}.
		 *
		 * @param columnStep fluent column mapping step
		 */
		void configure(ColumnStep<T> columnStep);
	}
}
