package com.aji_prayitno.excel.importer.model.table;

import java.util.List;

import com.aji_prayitno.excel.importer.model.ColumnDefinition;

public final class TableDefinition<T> {

    private String tableName;
    private Class<T> dtoClass;
    private List<ColumnDefinition<T, ?>> columns;
    
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Class<T> getDtoClass() {
		return dtoClass;
	}
	public void setDtoClass(Class<T> dtoClass) {
		this.dtoClass = dtoClass;
	}
	public List<ColumnDefinition<T, ?>> getColumns() {
		return columns;
	}
	public void setColumns(List<ColumnDefinition<T, ?>> columns) {
		this.columns = columns;
	}

}