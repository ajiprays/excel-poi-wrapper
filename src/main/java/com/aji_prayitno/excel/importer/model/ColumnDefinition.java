package com.aji_prayitno.excel.importer.model;

import com.aji_prayitno.excel.importer.step.DataSetter;

public final class ColumnDefinition<T, V> {

    private final String header;
    private final DataSetter<T, V> setter;
    private final Class<V> targetType;

    public ColumnDefinition(
            String header,
            DataSetter<T, V> setter,
            Class<V> targetType
    ) {
        this.header = header;
        this.setter = setter;
        this.targetType = targetType;
    }

    public String getHeader() {
        return header;
    }

    public DataSetter<T, V> getSetter() {
        return setter;
    }

    public Class<V> getTargetType() {
        return targetType;
    }
}