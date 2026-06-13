package com.aji_prayitno.excel.exporter.core.render;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;

import com.aji_prayitno.excel.exporter.style.StyleKey.BorderStyleType;

public final class Util {

	private Util() {
	}

	public static CellRangeAddress merge(
		Sheet sheet, 
		int firstRow, int lastRow, 
		int firstCol, int lastCol
	) {
		CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
		if (firstRow == lastRow && firstCol == lastCol) {
			return region;
		}
		sheet.addMergedRegion(region);
		return region;
	}

	public static void applyBorder(Sheet sheet, CellRangeAddress region, BorderStyle borderStyle) {
		RegionUtil.setBorderTop(borderStyle, region, sheet);
		RegionUtil.setBorderBottom(borderStyle, region, sheet);
		RegionUtil.setBorderLeft(borderStyle, region, sheet);
		RegionUtil.setBorderRight(borderStyle, region, sheet);
	}
	
	public static void applyBorder(
	        CellStyle style,
	        BorderStyleType borderStyleType,
	        BorderStyle borderStyle
	) {
		switch (borderStyleType) {
		case FULL:

			style.setBorderTop(borderStyle);
			style.setBorderBottom(borderStyle);
			style.setBorderLeft(borderStyle);
			style.setBorderRight(borderStyle);
			break;

		case TOP:

			style.setBorderTop(borderStyle);
			break;

		case BOTTOM:

			style.setBorderBottom(borderStyle);
			break;

		case LEFT:

			style.setBorderLeft(borderStyle);
			break;

		case RIGHT:

			style.setBorderRight(borderStyle);
			break;

		case TOP_BOTTOM:

			style.setBorderTop(borderStyle);
			style.setBorderBottom(borderStyle);
			break;

		case LEFT_RIGHT:

			style.setBorderLeft(borderStyle);
			style.setBorderRight(borderStyle);
			break;

		case TOP_LEFT:

			style.setBorderTop(borderStyle);
			style.setBorderLeft(borderStyle);
			break;

		case TOP_RIGHT:

			style.setBorderTop(borderStyle);
			style.setBorderRight(borderStyle);
			break;

		case BOTTOM_LEFT:

			style.setBorderBottom(borderStyle);
			style.setBorderLeft(borderStyle);
			break;

		case BOTTOM_RIGHT:

			style.setBorderBottom(borderStyle);
			style.setBorderRight(borderStyle);
			break;

		case TOP_BOTTOM_LEFT:

			style.setBorderTop(borderStyle);
			style.setBorderBottom(borderStyle);
			style.setBorderLeft(borderStyle);
			break;
			
		case TOP_BOTTOM_RIGHT:

			style.setBorderTop(borderStyle);
			style.setBorderBottom(borderStyle);
			style.setBorderRight(borderStyle);
			break;
			
		default:
			break;
		}
	}
	
	public static void fillRegionStyle(
		Sheet sheet, 
		int firstRow, int lastRow, 
		int firstCol, int lastCol, 
		CellStyle style
	) {
		for (int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++) {
			Row row = CellUtil.getRow(rowIndex, sheet);
			for (int colIndex = firstCol; colIndex <= lastCol; colIndex++) {
				Cell cell = CellUtil.getCell(row, colIndex);
				cell.setCellStyle(style);
			}
		}
	}
}