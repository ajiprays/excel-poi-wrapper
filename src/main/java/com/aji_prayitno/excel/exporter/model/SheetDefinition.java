package com.aji_prayitno.excel.exporter.model;

import java.util.ArrayList;
import java.util.List;

import com.aji_prayitno.excel.exporter.model.border.ManualTableDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableDefinition;

public class SheetDefinition {
	
	private Boolean isManualTable;
	private String sheetName;
	private List<String> titles = new ArrayList<>();
	private ManualTableDefinition<?> manualTable;
	private ExcelTableDefinition<?> excelTable;
	
	public SheetDefinition() {}
	public SheetDefinition(String sheetName, List<String> titles, ManualTableDefinition<?> table) {
		this.isManualTable = true;
		this.sheetName = sheetName;
		this.titles.addAll(titles);
		this.manualTable = table;
		this.excelTable = null;
	}
	public SheetDefinition(String sheetName, List<String> titles, ExcelTableDefinition<?> table) {
		this.isManualTable = false;
		this.sheetName = sheetName;
		this.titles.addAll(titles);
		this.excelTable = table;
		this.manualTable = null;
	}
	public Boolean getIsManualTable() {
		return isManualTable;
	}
	public void setIsManualTable(Boolean isManualTable) {
		this.isManualTable = isManualTable;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public List<String> getTitles() {
		return titles;
	}
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	public ManualTableDefinition<?> getManualTable() {
		return manualTable;
	}
	public void setManualTable(ManualTableDefinition<?> manualTable) {
		this.manualTable = manualTable;
	}
	public ExcelTableDefinition<?> getExcelTable() {
		return excelTable;
	}
	public void setExcelTable(ExcelTableDefinition<?> excelTable) {
		this.excelTable = excelTable;
	}
}