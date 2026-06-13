package com.aji_prayitno.excel.exporter.style;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import com.aji_prayitno.excel.exporter.core.render.Util;
import com.aji_prayitno.excel.exporter.model.border.ManualTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableColumnDefinition;
import com.aji_prayitno.excel.exporter.style.StyleKey.BorderStyleType;

public final class StyleRegistry {

	private final Workbook workbook;
	private final DataFormat dataFormat;
	private final Map<StyleKey, CellStyle> cache = new HashMap<>();
	
	public StyleRegistry(Workbook workbook) {
		this.workbook = workbook;
		this.dataFormat = workbook.getCreationHelper().createDataFormat();
	}

	public CellStyle data(BorderStyleType borderStyleType, BorderStyle borderStyle, ManualTableColumnDefinition<?> column) {
		StyleKey key = new StyleKey(
					column.isWrapText(),
					column.isShrinkToFit(),
					column.getAlignment(),
					null,
					column.isBold(),
					null,
					borderStyleType,
					borderStyle,
					column.getCustomDataFormat()
				);

		return cache.computeIfAbsent(key, this::createDataStyle);
	}
	
	public CellStyle data(ExcelTableColumnDefinition<?> column) {
		StyleKey key = new StyleKey(
					column.isWrapText(),
					column.isShrinkToFit(),
					column.getAlignment(),
					null,
					column.isBold(),
					null,
					BorderStyleType.NONE,
					BorderStyle.NONE,
					column.getCustomDataFormat()
				);

		return cache.computeIfAbsent(key, this::createDataStyle);
	}
	
	private CellStyle createDataStyle(StyleKey key) {
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(key.isWrapText());
		style.setShrinkToFit(key.isShrinkToFit());
		if (key.getHorizontalAlignment() != null) {
			style.setAlignment(key.getHorizontalAlignment());
		}
		if (key.getVerticalAlignment() != null) {
			style.setVerticalAlignment(key.getVerticalAlignment());
		}
		Util.applyBorder(style, key.getBorderStyleType(), key.getBorderStyle());
		if(key.getCustomDataFormat() != null && !key.getCustomDataFormat().isEmpty()) {
			style.setDataFormat(dataFormat.getFormat(key.getCustomDataFormat()));					
		}
		if (key.isBold()) {
			Font font = workbook.createFont();
			font.setBold(true);
			if(key.getFontSize() != null) {
				font.setFontHeightInPoints(key.getFontSize().shortValue());
			}
			style.setFont(font);
		}

		return style;
	}
	
	public CellStyle title() {
		StyleKey key = new StyleKey(
				false,
				false,
				HorizontalAlignment.LEFT,
				VerticalAlignment.CENTER,
				true,
				14,
				BorderStyleType.NONE,
				BorderStyle.NONE,
				null
		);
		
		return cache.computeIfAbsent(
				key,
				this::createDataStyle
		);
	}

	public CellStyle header(BorderStyleType borderStyleType, BorderStyle borderStyle) {
		StyleKey key = new StyleKey(
				false,
				false,
				HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER,
				true,
				null,
				borderStyleType,
				borderStyle,
				null
		);
		
		return cache.computeIfAbsent(
				key,
				this::createDataStyle
		);
	}

	public CellStyle summary(BorderStyleType borderStyleType, BorderStyle borderStyle, HorizontalAlignment horizontalAlignment) {
		StyleKey key = new StyleKey(
				false,
				false,
				horizontalAlignment,
				VerticalAlignment.CENTER,
				true,
				null,
				borderStyleType,
				borderStyle,
				null
		);
		
		return cache.computeIfAbsent(
				key,
				this::createDataStyle
		);
	}
	
	public CellStyle summary(
			BorderStyleType borderStyleType, BorderStyle borderStyle, 
			HorizontalAlignment horizontalAlignment, String customDataFormat) {
		StyleKey key = new StyleKey(
				false,
				false,
				horizontalAlignment,
				VerticalAlignment.CENTER,
				true,
				null,
				borderStyleType,
				borderStyle,
				customDataFormat
		);
		
		return cache.computeIfAbsent(
				key,
				this::createDataStyle
		);
	}
}