package com.aji_prayitno.excel.importer.core.reader.xls;

import java.io.InputStream;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.RecordFactoryInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.aji_prayitno.excel.importer.core.reader.BaseReader;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.plain.PlainTableDefinition;

public final class XlsWorkbookSheetReader<T> implements BaseReader<T> {

	private final String sheetName;
	private final PlainTableDefinition<T> tableDefinition;
	
	private final InputStream inputStream;
	
	public XlsWorkbookSheetReader(SheetDefinition<T> sheetDefinition) {
		this.sheetName = sheetDefinition.getSheetName();
		this.tableDefinition = sheetDefinition.getPlainTable();
		this.inputStream = sheetDefinition.getInputStream();
	}
	
	@Override
	public List<ImportResult<T>> importAsList() {
		try(Stream<ImportResult<T>> stream = importAsStream()){
			return stream.toList();
		}
	}
	@Override
	public Stream<ImportResult<T>> importAsStream() {
		return read();
	}
	
	private Stream<ImportResult<T>> read() {
		POIFSFileSystem fs = null;
		InputStream workbookStream = null;
		try {
			fs = new POIFSFileSystem(inputStream);
			String validWorkbookEntryName = null;
			for (String name : InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES) {
				if (fs.getRoot().hasEntry(name)) {
					validWorkbookEntryName = name;
					break;
				}
			}
			if (validWorkbookEntryName == null) {
				throw new IllegalArgumentException("The provided stream is not a valid legacy Excel .xls workbook.");
			}

			workbookStream = fs.createDocumentInputStream(validWorkbookEntryName);
			RecordFactoryInputStream recordStream = new RecordFactoryInputStream(workbookStream, false);
			
			XlsRowIterator<T> iterator = new XlsRowIterator<>(recordStream, sheetName, tableDefinition);
			Spliterator<ImportResult<T>> spliterator = Spliterators.spliteratorUnknownSize(
				iterator, Spliterator.ORDERED | Spliterator.NONNULL
			);

			InputStream streamToClose = workbookStream;
			POIFSFileSystem fileSystemToClose = fs;
			return StreamSupport
					.stream(spliterator, false)
					.onClose(() -> 
						closeResources(streamToClose, fileSystemToClose)
					);
			
		} catch (RuntimeException e) {
			closeResources(workbookStream, fs);
			throw e;
		} catch (Exception e) {
			closeResources(workbookStream, fs);
			throw new IllegalStateException("Failed to initialize legacy Excel .xls reader for sheet " + sheetName + ".", e);
		}
	}

	private void closeResources(InputStream workbookStream, POIFSFileSystem fs) {
		Exception failure = null;
		if (workbookStream != null) {
			try {
				workbookStream.close();
			} catch (Exception e) {
				failure = e;
			}
		}
		if (fs != null) {
			try {
				fs.close();
			} catch (Exception e) {
				if (failure == null) {
					failure = e;
				} else {
					failure.addSuppressed(e);
				}
			}
		}
		if (failure != null) {
			throw new IllegalStateException("Failed to close legacy Excel .xls reader resources.", failure);
		}
	}
}
