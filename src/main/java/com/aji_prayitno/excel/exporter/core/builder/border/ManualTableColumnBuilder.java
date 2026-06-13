package com.aji_prayitno.excel.exporter.core.builder.border;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ManualTableColumnBuilder<T> {
	
	private final List<String> headers = new ArrayList<>();
	
	public ManualTableColumnBuilder<T> add(String columnHeader) {
		if (columnHeader == null || columnHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Column header cannot be empty");
        }
        this.headers.add(columnHeader);
        return this;
	}
	
	public void add(String... columnHeaders) {
		if (columnHeaders == null || columnHeaders.length == 0) {
            throw new IllegalArgumentException("Column header cannot be empty");
        }
		this.headers.addAll(Arrays.asList(columnHeaders));
	}

	List<String> build() {
		if (headers.isEmpty()) {
            throw new IllegalStateException("Column header required");
        }
		return headers;
	}
}