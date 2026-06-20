package com.aji_prayitno.excel.importer.model;

import java.io.InputStream;

import com.aji_prayitno.excel.importer.model.table.TableDefinition;
import com.aji_prayitno.excel.importer.model.xls.XlsTableDefinition;

public final class SheetDefinition<T> {

	private InputStream inputStream;
    private String sheetName;
    private Boolean isXls;
    private TableDefinition<T> table = new TableDefinition<>();
    private XlsTableDefinition<T> xlsTable = new XlsTableDefinition<>();

    public SheetDefinition(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    public String getSheetName() {
        return sheetName;
    }
    public Boolean getIsXls() {
		return isXls;
	}
	public TableDefinition<T> getTable() {
        return table;
    }
    public void addTable(TableDefinition<T> table) {
    	this.isXls = false;
        this.table = table;
    }
	public XlsTableDefinition<T> getXlsTable() {
		return xlsTable;
	}
	public void addXlsTable(XlsTableDefinition<T> xlsTable) {
		this.isXls = true;
		this.xlsTable = xlsTable;
	}
    
}