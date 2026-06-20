package com.aji_prayitno.excel.importer.model.metadata;

import java.util.List;

public record XlsTableMetadata(
	String sheetName,
    int firstRow,
    int lastRow,
    int firstColumn,
    int lastColumn,
    List<String> columns
) {}