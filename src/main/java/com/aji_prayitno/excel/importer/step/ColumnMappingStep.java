package com.aji_prayitno.excel.importer.step;

import java.io.Serializable;

public interface ColumnMappingStep<T> {

	 <V> void map(ColumnMapper<T, V> columnMapper);
	
	 @FunctionalInterface
	 public interface ColumnMapper<T, V> extends Serializable {
	     void accept(T dto, ColumnStep<V> column);
	 }
	 
	@FunctionalInterface
	public interface ColumnMappingStepConfigure<T>{
		void configure(ColumnMappingStep<T> dtoMapping);
	}
}
