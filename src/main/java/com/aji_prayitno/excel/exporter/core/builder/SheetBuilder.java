package com.aji_prayitno.excel.exporter.core.builder;

import com.aji_prayitno.excel.exporter.core.builder.border.ManualTableBuilder;
import com.aji_prayitno.excel.exporter.core.builder.table.ExcelTableBuilder;
import com.aji_prayitno.excel.exporter.model.SheetDefinition;
import com.aji_prayitno.excel.exporter.step.SheetStep;
import com.aji_prayitno.excel.exporter.step.border.ManualTableStep.ManualTableConfigurer;
import com.aji_prayitno.excel.exporter.step.table.ExcelTableStep.ExcelTableConfigurer;

public final class SheetBuilder implements SheetStep {
	
	private SheetDefinition sheetDefinition = new SheetDefinition();
		
	public SheetBuilder(String sheetName) {
		sheetDefinition.setSheetName(sheetName);
	}
	
	@Override
	public SheetStep title(String title) {
		sheetDefinition.getTitles().add(title);
		return this;
	}
	@Override
	public <T> void addTable(Class<T> dataClass, ManualTableConfigurer<T> configurer) {
		ManualTableBuilder<T> tableManualBuilder = new ManualTableBuilder<>();
	    configurer.configure(tableManualBuilder);
	    sheetDefinition.setIsManualTable(true);
	    sheetDefinition.setManualTable(tableManualBuilder.build());
	    sheetDefinition.getManualTable().setDataClass(dataClass);
	}
	@Override
	public <T> void addTable(String tableName, Class<T> dataClass, ExcelTableConfigurer<T> configurer) {
		ExcelTableBuilder<T> tableBuilder = new ExcelTableBuilder<>();
	    configurer.configure(tableBuilder);
	    sheetDefinition.setIsManualTable(false);
	    sheetDefinition.setExcelTable(tableBuilder.build());
	    sheetDefinition.getExcelTable().setName(tableName);
	    sheetDefinition.getExcelTable().setDataClass(dataClass);
	}

	SheetDefinition build() {
		if (sheetDefinition.getSheetName() == null || sheetDefinition.getSheetName().isEmpty()) {
            throw new IllegalStateException("sheet name is required");
        }else if(
    		(!sheetDefinition.getIsManualTable() && sheetDefinition.getExcelTable() == null) || 
    		(sheetDefinition.getIsManualTable() && sheetDefinition.getManualTable() == null)
		) {
            throw new IllegalStateException("table is required");
        }
		return sheetDefinition;
	}
	
}