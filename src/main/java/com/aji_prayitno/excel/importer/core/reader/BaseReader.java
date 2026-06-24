package com.aji_prayitno.excel.importer.core.reader;

import java.util.List;
import java.util.stream.Stream;

import com.aji_prayitno.excel.importer.model.ImportResult;

public interface BaseReader<T> {

	List<ImportResult<T>> importAsList();
	Stream<ImportResult<T>> importAsStream();
	
}
