package com.aji_prayitno.excel.importer.model;

import com.aji_prayitno.excel.importer.step.DataSetter;

public final class ColumnDefinition<T, V> {

    private final String header;
    private final boolean ignoreNotFound;
    private final DataSetter<T, V> setter;
    private final Class<V> targetType;

    public ColumnDefinition(
            String header,
            boolean ignoreNotFound,
            DataSetter<T, V> setter,
            Class<V> targetType
    ) {
        this.header = header;
        this.ignoreNotFound = ignoreNotFound;
        this.setter = setter;
        this.targetType = targetType;
    }
    public String getHeader() {
        return header;
    }
    public boolean isIgnoreNotFound() {
		return ignoreNotFound;
	}
	public DataSetter<T, V> getSetter() {
        return setter;
    }
    public Class<V> getTargetType() {
        return targetType;
    }
}