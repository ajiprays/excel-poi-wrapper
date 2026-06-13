package com.aji_prayitno.excel.exporter.core.render.table;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

import com.aji_prayitno.excel.exporter.core.render.RenderContext;
import com.aji_prayitno.excel.exporter.model.SheetDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.table.ExcelTableDefinition;

public class ExcelTableRenderer {

	public <T> void render(
		RenderContext context,  
		ExcelTableDefinition<T> excelTableDefinition, 
		int startRowIndex, int lastRow
	) {
		XSSFSheet xssfSheet = null;
		Sheet sheet = context.getSheet();
		if (context.getWorkbook() instanceof SXSSFWorkbook) {
			xssfSheet = ((SXSSFWorkbook) context.getWorkbook()).getXSSFWorkbook()
					.getSheet(sheet.getSheetName());
		} else {
			xssfSheet = (XSSFSheet) sheet;
		}

		int lastColumn = excelTableDefinition.getColumns().size() - 1;
		AreaReference area = new AreaReference(new CellReference(startRowIndex, 0),
				new CellReference(lastRow, lastColumn), SpreadsheetVersion.EXCEL2007);
		XSSFTable table = xssfSheet.createTable(area);
		table.getCTTable().setId(1);
		table.setName(excelTableDefinition.getName());
		table.setDisplayName(excelTableDefinition.getName());
		applyColumns(table, excelTableDefinition);
		applyStyle(table);
	}

	private <T> void applyColumns(XSSFTable table, ExcelTableDefinition<T> excelTableDefinition) {
		CTTable ctTable = table.getCTTable();
		CTTableColumns columns = ctTable.getTableColumns();
		if (columns == null) {
			columns = ctTable.addNewTableColumns();
		}
		columns.setTableColumnArray(new CTTableColumn[0]);
		columns.setCount(excelTableDefinition.getColumns().size());

		long id = 1;
		for (ExcelTableColumnDefinition<T> column : excelTableDefinition.getColumns()) {
			CTTableColumn tableColumn = columns.addNewTableColumn();
			tableColumn.setId(id++);
			tableColumn.setName(column.getHeader());
		}
	}

	private <T> void applyStyle(XSSFTable table) {
		CTTableStyleInfo style = table.getCTTable().addNewTableStyleInfo();
		if (style == null) {
			style = table.getCTTable().addNewTableStyleInfo();
		}
		style.setName("TableStyleMedium2");
		style.setShowRowStripes(true);
		style.setShowColumnStripes(false);
	}
}
