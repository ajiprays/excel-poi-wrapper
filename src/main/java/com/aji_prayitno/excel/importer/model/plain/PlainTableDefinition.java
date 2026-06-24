package com.aji_prayitno.excel.importer.model.plain;

import java.util.List;

import com.aji_prayitno.excel.importer.model.ColumnDefinition;

public final class PlainTableDefinition<T> {
	private int startRowIndex;
    private Class<T> dtoClass;
    private List<ColumnDefinition<T, ?>> columns;
    
	public int getStartRowIndex() {
		return startRowIndex;
	}
	public void setStartRowIndex(int startRowIndex) {
		this.startRowIndex = startRowIndex;
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