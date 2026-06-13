package com.aji_prayitno.excel.exporter.core.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.core.WorkbookFactory;
import com.aji_prayitno.excel.exporter.core.render.SheetRenderer;
import com.aji_prayitno.excel.exporter.model.SheetDefinition;
import com.aji_prayitno.excel.exporter.step.BuildStep;
import com.aji_prayitno.excel.exporter.step.SheetStep.SheetStepConfigurer;
import com.aji_prayitno.excel.exporter.step.WorkbookStep;

public class ExcelBuilder implements WorkbookStep, BuildStep {
	private final Logger logger = LoggerFactory.getLogger(ExcelBuilder.class);
	
	private boolean streaming;
	private int rowAccessWindowSize = 100;
	private final List<SheetDefinition> sheets = new ArrayList<>();
	
	@Override
	public BuildStep streaming(boolean streaming) {
		this.streaming = streaming;
		return this;
	}

	@Override
	public BuildStep rowAccessWindowSize(int rowAccessWindowSize) {
		if (rowAccessWindowSize <= 0) {
			throw new IllegalArgumentException("Row access window size must be > 0");
		}
		this.rowAccessWindowSize = rowAccessWindowSize;
		return this;
	}

	@Override
	public WorkbookStep addSheet(String sheetName, SheetStepConfigurer configurer) {
		SheetBuilder sheetBuilder = new SheetBuilder(sheetName);
		configurer.configure(sheetBuilder);
		sheets.add(sheetBuilder.build());
		return this;
	}
	
	@Override
	public byte[] build() {
		if(sheets.isEmpty()) {
			throw new IllegalStateException("at least one sheet is required");
		}
		Workbook workbook = WorkbookFactory.create(streaming, rowAccessWindowSize);
		try {
			SheetRenderer renderer = new SheetRenderer(workbook);
			for (SheetDefinition sheet : sheets) {
				renderer.render(sheet);
			}
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				workbook.write(out);
				return out.toByteArray();
			} catch (IOException e) {
				throw new RuntimeException("Error write workbook", e);
			}
		} finally {
			WorkbookFactory.close(workbook);
		}
	}

	@Override
	public void build(OutputStream out) {
		Workbook workbook = WorkbookFactory.create(streaming, rowAccessWindowSize);
		try {
			SheetRenderer renderer = new SheetRenderer(workbook);
			for (SheetDefinition sheet : sheets) {
				logger.debug("render sheet:{}", sheet.getSheetName());
				renderer.render(sheet);
			}
			workbook.write(out);
		} catch (IOException e) {
			throw new RuntimeException("error write workbook", e);
		} finally {
			WorkbookFactory.close(workbook);
		}
	}

}