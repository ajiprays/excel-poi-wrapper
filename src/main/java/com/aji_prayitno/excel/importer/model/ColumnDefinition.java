package com.aji_prayitno.excel.importer.model;

import com.aji_prayitno.excel.importer.step.DataSetter;

public final record ColumnDefinition<T, V> (
    String header,
    boolean ignoreNotFound,
    DataSetter<T, V> setter,
    Class<V> targetType
){}
