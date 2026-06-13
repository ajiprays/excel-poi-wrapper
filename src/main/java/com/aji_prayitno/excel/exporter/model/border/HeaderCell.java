package com.aji_prayitno.excel.exporter.model.border;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class HeaderCell {

	private final List<String> paths;

	private HeaderCell(List<String> paths) {
		if (paths == null || paths.isEmpty()) {
			throw new IllegalArgumentException("Header path cannot be empty");
		}
		this.paths = Collections.unmodifiableList(paths);
	}

	public static HeaderCell of(String... paths) {
		if (paths == null || paths.length == 0) {
			throw new IllegalArgumentException("Header path cannot be empty");
		}
		return new HeaderCell(Arrays.asList(paths));
	}

	public List<String> getPaths() {
		return paths;
	}

	public int depth() {
		return paths.size();
	}

	public String last() {
		return paths.get(paths.size() - 1);
	}
}