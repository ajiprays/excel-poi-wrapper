package com.aji_prayitno.excel.exporter.style;

import java.util.Objects;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public final class StyleKey {

	private final boolean wrapText;
	private final boolean shrinkToFit;
	private final HorizontalAlignment horizontalAlignment;
	private final VerticalAlignment verticalAlignment;
	private final boolean bold;
	private final Integer fontSize;
	private final BorderStyleType borderStyleType;
	private final BorderStyle borderStyle;
	private final String customDataFormat;

	public StyleKey(
		boolean wrapText, 
		boolean shrinkToFit, HorizontalAlignment horizontalAlignment,
		VerticalAlignment verticalAlignment, boolean bold, Integer fontSize,
		BorderStyleType borderStyleType, BorderStyle borderStyle, 
		String customDataFormat
	) {

		this.wrapText = wrapText;
		this.shrinkToFit = shrinkToFit;
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;
		this.bold = bold;
		this.fontSize = fontSize;
		this.borderStyleType = borderStyleType;
		this.borderStyle = borderStyle;
		this.customDataFormat = customDataFormat;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bold, fontSize, borderStyleType, customDataFormat, horizontalAlignment, shrinkToFit,
				verticalAlignment, wrapText);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StyleKey other = (StyleKey) obj;
		return bold == other.bold && Objects.equals(fontSize, other.fontSize) 
				&& borderStyleType == other.borderStyleType
				&& Objects.equals(customDataFormat, other.customDataFormat)
				&& horizontalAlignment == other.horizontalAlignment && shrinkToFit == other.shrinkToFit
				&& verticalAlignment == other.verticalAlignment && wrapText == other.wrapText;
	}

	public boolean isWrapText() {
		return wrapText;
	}
	public boolean isShrinkToFit() {
		return shrinkToFit;
	}
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}
	public boolean isBold() {
		return bold;
	}
	public Integer getFontSize() {
		return fontSize;
	}

	public String getCustomDataFormat() {
		return customDataFormat;
	}
	public BorderStyleType getBorderStyleType() {
		return borderStyleType;
	}
	public BorderStyle getBorderStyle() {
		return borderStyle;
	}
	public enum BorderStyleType {
		NONE, TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, 
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_BOTTOM, LEFT_RIGHT, 
		TOP_BOTTOM_LEFT, TOP_BOTTOM_RIGHT, FULL
	}
}