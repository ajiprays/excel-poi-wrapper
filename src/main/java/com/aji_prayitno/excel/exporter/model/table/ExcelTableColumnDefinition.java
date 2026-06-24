package com.aji_prayitno.excel.exporter.model.table;

import java.util.function.Function;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

public final class ExcelTableColumnDefinition<T> {

	private String header;
	private Function<T, ?> mapper;
	private Integer width;
	private boolean autoSize = false;
	private boolean shrinkToFit;
	private boolean wrapText = false;
	private HorizontalAlignment alignment;
	private boolean bold;
	private String customDataFormat;

	public String getHeader() {
		return header;
	}
	public Function<T, ?> getMapper() {
		return mapper;
	}
	public Integer getWidth() {
		return width;
	}
	public boolean isAutoSize() {
		return autoSize;
	}
	public boolean isShrinkToFit() {
		return shrinkToFit;
	}
	public boolean isWrapText() {
		return wrapText;
	}
	public HorizontalAlignment getAlignment() {
		return alignment;
	}
	public boolean isBold() {
		return bold;
	}
	public String getCustomDataFormat() {
		return customDataFormat;
	}

	
	public ExcelTableColumnDefinition<T> header(String header) {
		this.header = header;
		return this;
	}
	public ExcelTableColumnDefinition<T> mapper(Function<T, ?> mapper) {
		this.mapper = mapper;
		return this;
	}
	public ExcelTableColumnDefinition<T> alignment(HorizontalAlignment alignment) {
		this.alignment = alignment;
		return this;
	}
	public ExcelTableColumnDefinition<T> bold(boolean bold) {
		this.bold = bold;
		return this;
	}
	public ExcelTableColumnDefinition<T> width(int width) {
		this.width = width;
		this.autoSize = false;
		return this;
	}
	public ExcelTableColumnDefinition<T> autoSize(boolean autoSize) {
		this.autoSize = autoSize;
		if(autoSize) {
			this.shrinkToFit = false;
			this.wrapText = false;
		}
		return this;
	}
	public ExcelTableColumnDefinition<T> shrinkToFit(boolean shrinkToFit) {
	    this.shrinkToFit = shrinkToFit;
	    return this;
	}
	public ExcelTableColumnDefinition<T> wrapText(boolean wrapText) {
	    this.wrapText = wrapText;
	    return this;
	}
	public ExcelTableColumnDefinition<T> customDataFormat(String customDataFormat) {
		this.customDataFormat = customDataFormat;
		return this;
	}

}