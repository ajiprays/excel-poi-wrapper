package com.aji_prayitno.excel.exporter.core.builder.table;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.aji_prayitno.excel.exporter.model.table.ExcelTableColumnDefinition;

public final class ExcelTableColumnConfigBuilder<T> {
	
	private Integer width;
	private boolean autoSize = false;
	private boolean shrinkToFit = false;
	private boolean wrapText = false;
	private HorizontalAlignment alignment;
	private boolean bold = false;
	private String styleCustom;
	
	public ExcelTableColumnConfigBuilder<T> styleCustom(String style) {
		this.styleCustom = style;
		return this;
	}
	
	public ExcelTableColumnConfigBuilder<T> width(int character) {
		this.width = character;
		return this;
	}

	public ExcelTableColumnConfigBuilder<T> autoSize() {
        this.autoSize = true;
        return this;
    }
	
	public ExcelTableColumnConfigBuilder<T> shrinkToFit() {
	    this.shrinkToFit = true;
	    this.wrapText = false;
	    this.autoSize = false;
	    return this;
	}

	public ExcelTableColumnConfigBuilder<T> wrapText() {
	    this.wrapText = true;
	    this.autoSize = false;
	    this.shrinkToFit = false;
	    return this;
	}
	
	public ExcelTableColumnConfigBuilder<T> center() {
		this.alignment = HorizontalAlignment.CENTER;
		return this;
	}

	public ExcelTableColumnConfigBuilder<T> right() {
		this.alignment = HorizontalAlignment.RIGHT;
		return this;
	}

	public ExcelTableColumnConfigBuilder<T> left() {
		this.alignment = HorizontalAlignment.LEFT;
		return this;
	}
	
	public ExcelTableColumnConfigBuilder<T> bold() {
		this.bold = true;
		return this;
	}
	
	ExcelTableColumnDefinition<T> build(ExcelTableColumnDefinition<T> definition){
		if (width != null) {
			definition.width(width);
		}
		definition
		.autoSize(autoSize)
		.shrinkToFit(shrinkToFit)
		.wrapText(wrapText)
		.alignment(alignment)
		.bold(bold)
		.customDataFormat(styleCustom);
		return definition;
	}
}