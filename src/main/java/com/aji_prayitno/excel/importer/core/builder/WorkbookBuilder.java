package com.aji_prayitno.excel.importer.core.builder;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.importer.core.reader.ReaderFactory;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.step.table.WorkbookStep;
import com.aji_prayitno.excel.importer.step.xls.XlsWorkbookStep;

public class WorkbookBuilder<T> implements WorkbookStep<T>, XlsWorkbookStep<T> {
	private static final Logger logger = LoggerFactory.getLogger(WorkbookBuilder.class);

	private final SheetDefinition<T> sheetDefinition;
	
	public WorkbookBuilder(SheetDefinition<T> sheetDefinition) {
		this.sheetDefinition = sheetDefinition;
	}
	
	@Override
	public List<ImportResult<T>> importData() {
		var start = Instant.now();
		logger.debug("start building workbook at {}", start);

		var result = ReaderFactory.getWorkBookReader(sheetDefinition, false).importAsList();
		
		var finish = Instant.now();
		logger.debug(
			"finish building workbook at {} in {} seconds", 
			finish, ((finish.toEpochMilli() - start.toEpochMilli())/1000.0)
		);
		
		return result;
	}
	@Override
	public Stream<ImportResult<T>> importDataAsStream() {
		var start = Instant.now();
		logger.debug("start building workbook at {}", start);

		var result = ReaderFactory.getWorkBookReader(sheetDefinition, false).importAsStream();
		
		var finish = Instant.now();
		logger.debug(
			"finish building workbook at {} in {} seconds", 
			finish, ((finish.toEpochMilli() - start.toEpochMilli())/1000.0)
		);
		
		return result;
	}
	@Override
	public List<ImportResult<T>> importDataSmallFile() {
		var start = Instant.now();
		logger.debug("start building workbook at {}", start);

		var result = ReaderFactory.getWorkBookReader(sheetDefinition, true).importAsList();
		
		var finish = Instant.now();
		logger.debug(
			"finish building workbook at {} in {} seconds", 
			finish, ((finish.toEpochMilli() - start.toEpochMilli())/1000.0)
		);
		
		return result;
	}
}
