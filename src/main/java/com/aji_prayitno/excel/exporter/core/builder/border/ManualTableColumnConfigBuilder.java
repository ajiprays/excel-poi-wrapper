package com.aji_prayitno.excel.exporter.core.builder.border;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.aji_prayitno.excel.exporter.model.border.ManualTableColumnDefinition;

public final class ManualTableColumnConfigBuilder<T> {
	
	private Integer width;
	private boolean autoSize = false;
	private boolean shrinkToFit = false;
	private boolean wrapText = false;
	private HorizontalAlignment alignment;
	private boolean bold = false;
	private String styleCustom;
	
	public ManualTableColumnConfigBuilder<T> styleCustom(String style) {
		this.styleCustom = style;
		return this;
	}
	public ManualTableColumnConfigBuilder<T> width(int character) {
		this.width = character;
		return this;
	}
	public ManualTableColumnConfigBuilder<T> autoSize() {
        this.autoSize = true;
        return this;
    }
	public ManualTableColumnConfigBuilder<T> shrinkToFit() {
	    this.shrinkToFit = true;
	    this.wrapText = false;
	    this.autoSize = false;
	    return this;
	}
	public ManualTableColumnConfigBuilder<T> wrapText() {
	    this.wrapText = true;
	    this.autoSize = false;
	    this.shrinkToFit = false;
	    return this;
	}
	public ManualTableColumnConfigBuilder<T> center() {
		this.alignment = HorizontalAlignment.CENTER;
		return this;
	}
	public ManualTableColumnConfigBuilder<T> right() {
		this.alignment = HorizontalAlignment.RIGHT;
		return this;
	}
	public ManualTableColumnConfigBuilder<T> left() {
		this.alignment = HorizontalAlignment.LEFT;
		return this;
	}
	public ManualTableColumnConfigBuilder<T> bold() {
		this.bold = true;
		return this;
	}
	ManualTableColumnDefinition<T> build(ManualTableColumnDefinition<T> definition){
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