package com.aji_prayitno.excel.importer.step;

import java.io.Serializable;

@FunctionalInterface
public interface DataSetter<T, V> extends Serializable {
    /**
     * Assigns an imported value to the target DTO.
     *
     * @param target target DTO instance
     * @param value converted cell value
     */
    void accept(T target, V value);
}
