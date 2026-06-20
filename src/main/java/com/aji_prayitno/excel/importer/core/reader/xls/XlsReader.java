package com.aji_prayitno.excel.importer.core.reader.xls;

import java.io.InputStream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.RecordFactoryInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.xls.XlsTableDefinition;

public final class XlsReader {

	private XlsReader() {}
	
	public static <T> Stream<ImportResult<T>> read(SheetDefinition<T> sheetDefinition) {
		InputStream inputStream = sheetDefinition.getInputStream();
		String sheetName = sheetDefinition.getSheetName();
		XlsTableDefinition<T> tableDefinition = sheetDefinition.getXlsTable();
		
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
			return StreamSupport.stream(spliterator, false)
					.onClose(() -> closeResources(streamToClose, fileSystemToClose));
		} catch (RuntimeException e) {
			closeResources(workbookStream, fs);
			throw e;
		} catch (Exception e) {
			closeResources(workbookStream, fs);
			throw new IllegalStateException("Failed to initialize legacy Excel .xls reader for sheet '" + sheetName + "'.", e);
		}
	}

	private static void closeResources(InputStream workbookStream, POIFSFileSystem fs) {
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
