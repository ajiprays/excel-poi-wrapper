package com.aji_prayitno.excel.importer.step;


public interface ColumnStep<T> {

	 <V> ColumnStep<T> fromColumn(String header, DataSetter<T, V> setter);
	 
	@FunctionalInterface
	public interface ColumnStepConfigure<T>{
		void configure(ColumnStep<T> columnStep);
	}
}
