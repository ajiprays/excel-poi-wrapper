package com.aji_prayitno.excel.exporter.model.border;

import java.util.function.Function;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class ManualTableColumnDefinition<T> {

	private HeaderCell header;
	private Function<T, ?> mapper;
	private Integer width;
	private boolean autoSize = false;
	private boolean shrinkToFit;
	private boolean wrapText = false;
	private HorizontalAlignment alignment;
	private boolean bold;
	private String customDataFormat;
	
	public ManualTableColumnDefinition() {}
	public ManualTableColumnDefinition(HeaderCell header, Function<T, ?> mapper) {
		if (header == null) {
			throw new IllegalArgumentException("Header cannot be null");
		}
		if (mapper == null) {
			throw new IllegalArgumentException("Mapper cannot be null");
		}
		this.header = header;
		this.mapper = mapper;
	}

	public HeaderCell getHeader() {
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

	public ManualTableColumnDefinition<T> header(HeaderCell header) {
		this.header = header;
		return this;
	}
	public ManualTableColumnDefinition<T> mapper(Function<T, ?> mapper) {
		this.mapper = mapper;
		return this;
	}
	public ManualTableColumnDefinition<T> alignment(HorizontalAlignment alignment) {
		this.alignment = alignment;
		return this;
	}
	public ManualTableColumnDefinition<T> bold(boolean bold) {
		this.bold = bold;
		return this;
	}
	public ManualTableColumnDefinition<T> width(int width) {
		this.width = width;
		this.autoSize = false;
		return this;
	}
	public ManualTableColumnDefinition<T> autoSize(boolean autoSize) {
		this.autoSize = autoSize;
		if(autoSize) {
			this.shrinkToFit = false;
			this.wrapText = false;
		}
		return this;
	}
	public ManualTableColumnDefinition<T> shrinkToFit(boolean shrinkToFit) {
	    this.shrinkToFit = shrinkToFit;
	    return this;
	}
	public ManualTableColumnDefinition<T> wrapText(boolean wrapText) {
	    this.wrapText = wrapText;
	    return this;
	}
	public ManualTableColumnDefinition<T> customDataFormat(String customDataFormat) {
		this.customDataFormat = customDataFormat;
		return this;
	}

}