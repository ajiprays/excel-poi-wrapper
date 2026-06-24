package com.aji_prayitno.excel.importer.core.reader.xlsx.table;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRelation;

import com.aji_prayitno.excel.importer.core.reader.BaseReader;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.metadata.TableMetadata;
import com.aji_prayitno.excel.importer.model.table.TableDefinition;

public final class TableWorkbookSheetReader<T> implements BaseReader<T>{

    private final TableReader tableReader = new TableReader();
    private final DataReader dataReader = new DataReader();

	private final String sheetName;
	private final TableDefinition<T> tableDefinition;
	private final InputStream inputStream;

	
    public TableWorkbookSheetReader(SheetDefinition<T> sheetDefinition) {
    	this.sheetName = sheetDefinition.getSheetName();
		this.tableDefinition = sheetDefinition.getTable();
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
            TableMetadata tableMetadata = getTableMetadata(opcPackage);
            OPCPackage packageToClose = opcPackage;
            return dataReader.streamData(
        		opcPackage, tableMetadata, tableDefinition
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
                "Failed to open workbook stream for sheet " + sheetName + 
                " and table " + tableDefinition.getTableName() + ".", e
            );
        }
    }

    private TableMetadata getTableMetadata(OPCPackage opcPackage) {
        String targetTableName = tableDefinition.getTableName();
        try {
            XSSFReader reader = new XSSFReader(opcPackage);
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();

            PackagePart sheetPart = findSheetPart(sheetIterator);
            String tableRelationType = XSSFRelation.TABLE.getRelation();
            boolean hasTableRelationship = false;

            for (PackageRelationship rel : sheetPart.getRelationships()) {
                if (!tableRelationType.equals(rel.getRelationshipType())) {
                    continue;
                }
                hasTableRelationship = true;

                PackagePart tablePart = sheetPart.getRelatedPart(rel);
                try (InputStream is = tablePart.getInputStream()) {
                    TableMetadata metadata = tableReader.read(sheetName, is);
                    if (targetTableName.equalsIgnoreCase(metadata.tableName())) {
                    	validateColumn(metadata);
                        return metadata;
                    }
                }
            }
            if (!hasTableRelationship) {
                throw new IllegalArgumentException(
                        "Worksheet " + sheetName + " does not contain any Excel table metadata. "
                                + "Create a formatted Excel Table in the .xlsx file before importing."
                );
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read internal .xlsx table metadata for worksheet " + sheetName + ".", e);
        } catch (OpenXML4JException e) {
            throw new IllegalArgumentException(
                    "Unsupported Excel file. The workbook must be a valid .xlsx OpenXML file with Excel table metadata.",
                    e
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unexpected failure while parsing Excel table metadata for worksheet " + sheetName
                            + " and table " + tableDefinition.getTableName() + ".",
                    e
            );
        }

        throw new IllegalArgumentException(
                "Table " + targetTableName + " was not found in worksheet " + sheetName
                        + ". Verify the file is .xlsx and the target range is formatted as an Excel Table."
        );
    }

	private void validateColumn(TableMetadata metadata) {
		for(var columnDefinition : tableDefinition.getColumns()) {
			if(
				!columnDefinition.ignoreNotFound() && 
				!metadata.columns().contains(columnDefinition.header())
			) {
				throw new IllegalArgumentException(
					"Column " + columnDefinition.header() + 
					" is not found in table " + tableDefinition.getTableName() + "."
				);
			}
		}
	}

    private PackagePart findSheetPart(XSSFReader.SheetIterator sheetIterator) {
        while (sheetIterator.hasNext()) {
            try (InputStream is = sheetIterator.next()) {
                if (sheetName.equalsIgnoreCase(sheetIterator.getSheetName())) {
                    return sheetIterator.getSheetPart();
                }
            } catch (IOException e) {
                throw new IllegalStateException("An I/O failure occurred while advancing through the worksheet iteration structure.", e);
            }
        }
        throw new IllegalArgumentException("The requested worksheet named " + sheetName + " was not found in the workbook container.");
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
