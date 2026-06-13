package com.aji_prayitno.excel.exporter.core.render;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CellWriter {
	private final Logger logger = LoggerFactory.getLogger(CellWriter.class);
	
	public void write(Cell cell, Object value) {
		if (value == null) {
			logger.debug("write null value");
			return;
		}
		if (value instanceof String newValue) {
			logger.debug("write string value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof Integer newValue) {
			logger.debug("write Integer value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof Long newValue) {
			logger.debug("write Long value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof BigInteger newValue) {
			logger.debug("write BigInteger value: {}", newValue);
			cell.setCellValue(newValue.longValue());
			return;
		}
		if (value instanceof Double newValue) {
			logger.debug("write Double value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof Float newValue) {
			logger.debug("write string value: {}", newValue);
			cell.setCellValue(newValue.doubleValue());
			return;
		}
		if (value instanceof BigDecimal newValue) {
			logger.debug("write BigDecimal value: {}", newValue);
			cell.setCellValue(newValue.doubleValue());
			return;
		}
		if (value instanceof Boolean newValue) {
			logger.debug("write Boolean value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof Date newValue) {
			logger.debug("write Date value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof LocalDate newValue) {
			logger.debug("write LocalDate value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof LocalDateTime newValue) {
			logger.debug("write LocalDateTime value: {}", newValue);
			cell.setCellValue(newValue);
			return;
		}
		if (value instanceof ZonedDateTime newValue) {
			logger.debug("write ZonedDateTime value: {}", newValue);
			cell.setCellValue(LocalDateTime.ofInstant(newValue.toInstant(), newValue.getZone()));
			return;
		}
		String newValue = value.toString(); 
		logger.debug("write undefined as String value: {}", newValue);
		cell.setCellValue(newValue);
	}
}