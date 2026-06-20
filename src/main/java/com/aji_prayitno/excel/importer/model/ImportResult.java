package com.aji_prayitno.excel.importer.model;

import java.util.Map;

public record ImportResult<T>(
        T data,
        /**
         * dto column, error message
         */
        Map<String, String> error
) {
}