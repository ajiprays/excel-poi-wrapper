package com.aji_prayitno.excel.importer.step.xls;

import java.util.List;
import java.util.stream.Stream;

import com.aji_prayitno.excel.importer.model.ImportResult;

public interface XlsWorkbookStep<T> {
	List<ImportResult<T>> importData();
	Stream<ImportResult<T>> importDataAsStream();
}