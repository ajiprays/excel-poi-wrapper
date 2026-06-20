package com.aji_prayitno.excel.importer.core.builder;

import java.io.InputStream;

import com.aji_prayitno.excel.importer.step.SheetStep;
import com.aji_prayitno.excel.importer.step.TableStep;

public final class SheetBuilder implements SheetStep {

	private final InputStream inputStream;
	
	public SheetBuilder(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
    public TableStep fromSheet(String sheetName) {
    	return new TableBuilder(inputStream, sheetName);
    }

}