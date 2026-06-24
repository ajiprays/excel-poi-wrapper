package com.aji_prayitno.excel.importer.core.builder;


import java.util.ArrayList;
import java.util.List;

import com.aji_prayitno.excel.importer.core.ReflectionUtil;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.step.ColumnStep;
import com.aji_prayitno.excel.importer.step.DataSetter;

public final class ColumnBuilder<T> implements ColumnStep<T>{
	
	private final Class<T> dtoClass;
    private final List<ColumnDefinition<T, ?>> columns = new ArrayList<>();

    
	public ColumnBuilder(Class<T> dtoClass) {
		this.dtoClass = dtoClass;
	}

	@Override
    public <V> ColumnStep<T> fromColumn(String header, DataSetter<T, V> setter) {
		validateHeader(header);
		validateDataSetter(setter);
        Class<V> targetType = ReflectionUtil.resolve(dtoClass, setter);
        columns.add(new ColumnDefinition<>(header, false, setter, targetType));
        return this;
    }

	@Override
	public <V> ColumnStep<T> fromColumnIgnoreNotFound(String header, DataSetter<T, V> setter) {
		validateHeader(header);
		validateDataSetter(setter);
        Class<V> targetType = ReflectionUtil.resolve(dtoClass, setter);
        columns.add(new ColumnDefinition<>(header, true, setter, targetType));
        return this;
	}
	
    public List<ColumnDefinition<T, ?>> build() {
        return columns;
    }
    
	private void validateHeader(String header) {
		if(header == null || header.trim().isEmpty()) {
			throw new IllegalArgumentException("header cannot be null or empty.");
		}
	}

	private void validateDataSetter(DataSetter<?, ?> setter) {
		if(setter == null) {
			throw new IllegalArgumentException("data setter cannot be null.");
		}
	}
}