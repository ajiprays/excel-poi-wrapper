package com.aji_prayitno.excel.importer.core.builder;


import java.util.ArrayList;
import java.util.List;

import com.aji_prayitno.excel.importer.core.ReflectionUtil;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.step.ColumnStep;
import com.aji_prayitno.excel.importer.step.DataSetter;

public final class ColumnBuilder<T> implements ColumnStep<T>{
	
    private final List<ColumnDefinition<T, ?>> columns = new ArrayList<>();

	@Override
    public <V> ColumnStep<T> fromColumn(String header, DataSetter<T, V> setter) {
    	@SuppressWarnings("unchecked")
        Class<V> targetType = (Class<V>)ReflectionUtil.resolveParameterType(setter);
        columns.add(new ColumnDefinition<>(header, false, setter, targetType));
        return this;
    }
    
	@Override
	public <V> ColumnStep<T> fromColumnIgnoreNotFound(String header, DataSetter<T, V> setter) {
    	@SuppressWarnings("unchecked")
        Class<V> targetType = (Class<V>)ReflectionUtil.resolveParameterType(setter);
        columns.add(new ColumnDefinition<>(header, true, setter, targetType));
        return this;
	}
	
    public List<ColumnDefinition<T, ?>> build() {
        return columns;
    }
}