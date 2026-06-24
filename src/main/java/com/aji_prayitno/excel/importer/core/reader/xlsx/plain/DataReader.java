package com.aji_prayitno.excel.importer.core.reader.xlsx.plain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;

import com.aji_prayitno.excel.importer.core.reader.Util;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.plain.PlainTableDefinition;


public final class DataReader {

	public <T> Stream<ImportResult<T>> streamData(
        OPCPackage opcPackage, String sheetName, PlainTableDefinition<T> tableDefinition
	) {
	    try {
	        XSSFReader reader = new XSSFReader(opcPackage);
	        ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(opcPackage);
	        InputStream sheetStream = Util.findSheetStream(reader, sheetName);

	        RowIterator<T> iterator = new RowIterator<>(
                sheetStream, sharedStrings, tableDefinition
            );

	        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL),
                false
	        ).onClose(iterator::close);

	    } catch (IOException e) {
	        throw new IllegalStateException(
        		"Failed to stream worksheet data for sheet " + sheetName + " due to an I/O error.", e
	        );
	    } catch (OpenXML4JException e) {
	        throw new IllegalArgumentException(
        		"Failed to read OpenXML worksheet data for sheet " + sheetName + 
        		". The file is invalid, corrupted, or not an .xlsx workbook.", e
	        );
	    } catch (RuntimeException e) {
	    	throw e;
	    } catch (Exception e) {
	    	throw new IllegalStateException(
    			"Unexpected failure while preparing worksheet data stream for sheet " + sheetName + ".", e
	    	);
	    }
	}
	    
}
