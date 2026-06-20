package com.aji_prayitno.excel.importer.step;

import java.io.Serializable;

@FunctionalInterface
public interface DataSetter<T, V> extends Serializable {
    void accept(T target, V value);
}