package com.aji_prayitno.excel.importer.core.reader.table;

import java.io.IOException;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;

import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.metadata.TableMetadata;
import com.aji_prayitno.excel.importer.model.table.TableDefinition;

public final class DataReader {

	public <T> Stream<ImportResult<T>> streamData(
        OPCPackage opcPackage, TableMetadata tableMetadata,
        TableDefinition<T> tableDefinition
	) {
	    try {
	        XSSFReader reader = new XSSFReader(opcPackage);
	        SharedStringsTable sharedStrings = (SharedStringsTable) reader.getSharedStringsTable();
	        InputStream sheetStream = findSheetStream(reader, tableMetadata.sheetName());

	        RowIterator<T> iterator = new RowIterator<>(
                sheetStream, sharedStrings,
                tableMetadata, tableDefinition
            );

	        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL),
                false
	        ).onClose(iterator::close);

	    } catch (IOException e) {
	        throw new IllegalStateException(
        		"Failed to stream worksheet data for sheet " + tableMetadata.sheetName() + 
        		" and table " + tableMetadata.tableName() + " due to an I/O error.", e
	        );
	    } catch (OpenXML4JException e) {
	        throw new IllegalArgumentException(
        		"Failed to read OpenXML worksheet data for sheet " + tableMetadata.sheetName() + 
        		" and table " + tableMetadata.tableName() + ". The file is invalid, corrupted, or not an .xlsx workbook.", e
	        );
	    } catch (RuntimeException e) {
	    	throw e;
	    } catch (Exception e) {
	    	throw new IllegalStateException(
    			"Unexpected failure while preparing worksheet data stream for sheet " + 
				tableMetadata.sheetName() + " and table " + tableMetadata.tableName() + ".", e
	    	);
	    }
	}
	
	private InputStream findSheetStream(XSSFReader reader, String sheetName) {
		XSSFReader.SheetIterator sheetIterator;
		try {
			sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
		} catch (InvalidFormatException | IOException e) {
			throw new IllegalStateException("Failed to load sheet streams from the workbook package.", e);
		}
		while (sheetIterator.hasNext()) {
			InputStream is = sheetIterator.next();
			if (sheetName.equalsIgnoreCase(sheetIterator.getSheetName())) {
				return is;
			}
			try {
				is.close();
			} catch (IOException e) {
				throw new IllegalStateException(
					"Failed to close unselected worksheet stream while searching for sheet " + sheetName + ".", e
				);
			}
		}
		throw new IllegalArgumentException("Worksheet not found while preparing data stream: " + sheetName + ".");
	}
	    
}
