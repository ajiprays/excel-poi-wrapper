package com.aji_prayitno.excel.importer.model;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.poifs.filesystem.FileMagic;

import com.aji_prayitno.excel.importer.model.plain.PlainTableDefinition;
import com.aji_prayitno.excel.importer.model.table.TableDefinition;

public final class SheetDefinition<T> {

	private InputStream inputStream;
    private String sheetName;
    private Boolean isXls;
    private Boolean isPlain;
    private TableDefinition<T> table = new TableDefinition<>();
    private PlainTableDefinition<T> plainTable = new PlainTableDefinition<>();

    public SheetDefinition(InputStream inputStream) {
		this.inputStream = FileMagic.prepareToCheckMagic(inputStream);
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
    	if(isXls == null) {
    		FileMagic magic;
			try {
				magic = FileMagic.valueOf(inputStream);
			} catch (IOException e) {
	            throw new IllegalStateException("Failed to read file import due to an I/O error.", e);
			}
    		if (magic == FileMagic.OOXML) {
    			isXls = false;                
            } else if (magic == FileMagic.OLE2) {
            	isXls = true;
            } else {
                throw new IllegalArgumentException("invalid file.");
            }
    	}
		return isXls;
	}
	public Boolean getIsPlain() {
		return isPlain;
	}
	public TableDefinition<T> getTable() {
        return table;
    }
    public void addTable(TableDefinition<T> table) {
    	this.isPlain = false;
        this.table = table;
    }
	public PlainTableDefinition<T> getPlainTable() {
		return plainTable;
	}
	public void addPlainTable(PlainTableDefinition<T> plainTable) {
		this.isPlain = true;
		this.plainTable = plainTable;
	}
    
}