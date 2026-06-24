package com.aji_prayitno.excel.importer.core.reader.xlsx.plain;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;

import com.aji_prayitno.excel.importer.core.reader.BaseReader;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.plain.PlainTableDefinition;

public final class PlainWorkbookSheetReader<T> implements BaseReader<T> {

    private final DataReader dataReader = new DataReader();

    private final String sheetName;
	private final PlainTableDefinition<T> tableDefinition;
	private final InputStream inputStream;

	
    public PlainWorkbookSheetReader(SheetDefinition<T> sheetDefinition) {
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
        OPCPackage opcPackage = null;
        try {
            opcPackage = OPCPackage.open(inputStream);
            OPCPackage packageToClose = opcPackage;
            return dataReader.streamData(
        		opcPackage, sheetName, tableDefinition
    		).onClose(() -> closePackage(packageToClose));

        } catch (IOException e) {
            closePackageQuietly(opcPackage);
            throw new IllegalStateException(
        		"Failed to access the Excel workbook stream. Verify the uploaded file is readable.", e);
        } catch (OpenXML4JException e) {
            closePackageQuietly(opcPackage);
            throw new IllegalArgumentException(
        		"Unsupported Excel file. This importer requires a valid .xlsx workbook with table support.", e);
        } catch (IllegalArgumentException e) {
            closePackageQuietly(opcPackage);
            throw e;
        } catch (Exception e) {
            closePackageQuietly(opcPackage);
            throw new IllegalStateException(
                    "Failed to open workbook stream for sheet " + sheetName + ".", e
            );
        }
    }

    private void closePackage(OPCPackage opcPackage) {
        if (opcPackage == null) {
            return;
        }
        try {
            opcPackage.close();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close Excel workbook package after import streaming.", e);
        }
    }

    private void closePackageQuietly(OPCPackage opcPackage) {
        if (opcPackage == null) {
            return;
        }
        try {
            opcPackage.close();
        } catch (IOException ignored) {
        }
    }
}
