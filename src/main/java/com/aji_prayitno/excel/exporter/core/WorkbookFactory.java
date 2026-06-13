package com.aji_prayitno.excel.exporter.core;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorkbookFactory {

	private static final Logger logger = LoggerFactory.getLogger(WorkbookFactory.class);
	
	private WorkbookFactory() {
	}

	public static Workbook create(boolean streaming, int rowAccessWindowSize) {
		logger.debug("create() streaming:{}, rowAccessWindowSize:{}", streaming, rowAccessWindowSize);
		if (streaming) {
			return new SXSSFWorkbook(rowAccessWindowSize);
		}
		return new XSSFWorkbook();
	}

	public static void close(Workbook workbook) {
		if (workbook == null) {
			return;
		}
		try {
			workbook.close();
		} catch (IOException e) {
			logger.error("close() error: ", e.getCause());
			throw new RuntimeException("error close workbook", e);
		}
	}
}