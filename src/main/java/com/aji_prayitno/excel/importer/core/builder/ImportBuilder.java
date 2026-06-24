package com.aji_prayitno.excel.importer.core.builder;


import java.io.InputStream;

import com.aji_prayitno.excel.importer.step.SheetStep;

public final class ImportBuilder {

    public SheetStep from(InputStream inputStream) {
		if(inputStream == null) {
			throw new IllegalArgumentException("inputStream cannot be null.");
		}
    	return new SheetBuilder(inputStream);
    }

}