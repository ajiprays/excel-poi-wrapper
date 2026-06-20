package com.aji_prayitno.excel.importer.step;

import java.io.Serializable;

public interface ColumnMappingStep<T> {

	/**
	 * Registers a nested column mapper.
	 *
	 * @param columnMapper mapper callback
	 * @param <V> nested column target type
	 */
	 <V> void map(ColumnMapper<T, V> columnMapper);
	
	 @FunctionalInterface
	 public interface ColumnMapper<T, V> extends Serializable {
		/**
		 * Configures mappings for a nested DTO column group.
		 *
		 * @param dto parent DTO instance
		 * @param column nested column step
		 */
	     void accept(T dto, ColumnStep<V> column);
	 }
	 
	@FunctionalInterface
	public interface ColumnMappingStepConfigure<T>{
		/**
		 * Configures column mapping definitions.
		 *
		 * @param dtoMapping mapping step to configure
		 */
		void configure(ColumnMappingStep<T> dtoMapping);
	}
}
