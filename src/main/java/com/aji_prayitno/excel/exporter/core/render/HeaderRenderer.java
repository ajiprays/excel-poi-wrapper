package com.aji_prayitno.excel.exporter.core.render;

import com.aji_prayitno.excel.exporter.model.SheetDefinition;

public interface HeaderRenderer {
	int render(RenderContext context, SheetDefinition sheetDefinition, int lastRowIndex);
}