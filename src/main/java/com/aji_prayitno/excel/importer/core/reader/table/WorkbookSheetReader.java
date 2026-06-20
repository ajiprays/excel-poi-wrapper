package com.aji_prayitno.excel.importer.core.reader.table;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRelation;

import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.SheetDefinition;
import com.aji_prayitno.excel.importer.model.metadata.TableMetadata;

public final class WorkbookSheetReader {

    private final TableReader tableReader = new TableReader();
    private final DataReader dataReader = new DataReader();

    public <T> Stream<ImportResult<T>> importAsStream(SheetDefinition<T> sheetDefinition) {
        InputStream inputStream = sheetDefinition.getInputStream();
        OPCPackage opcPackage = null;
        try {
            opcPackage = OPCPackage.open(inputStream);
            TableMetadata tableMetadata = getTableMetadata(opcPackage, sheetDefinition);
            OPCPackage packageToClose = opcPackage;
            return dataReader.streamData(opcPackage, tableMetadata, sheetDefinition.getTable())
                    .onClose(() -> closePackage(packageToClose));

        } catch (IOException e) {
            closePackageQuietly(opcPackage);
            throw new IllegalStateException("Failed to access the Excel workbook stream. Verify the uploaded file is readable.", e);
        } catch (OpenXML4JException e) {
            closePackageQuietly(opcPackage);
            throw new IllegalArgumentException("Unsupported Excel file. This importer requires a valid .xlsx workbook with table support.", e);
        } catch (IllegalArgumentException e) {
            closePackageQuietly(opcPackage);
            throw e;
        } catch (Exception e) {
            closePackageQuietly(opcPackage);
            throw new IllegalStateException(
                    "Failed to open workbook stream for sheet '" + sheetDefinition.getSheetName()
                            + "' and table '" + sheetDefinition.getTable().getTableName() + "'.",
                    e
            );
        }
    }

    private TableMetadata getTableMetadata(OPCPackage opcPackage, SheetDefinition<?> sheetDefinition) {
        String targetSheetName = sheetDefinition.getSheetName();
        String targetTableName = sheetDefinition.getTable().getTableName();

        try {
            XSSFReader reader = new XSSFReader(opcPackage);
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();

            PackagePart sheetPart = findSheetPart(sheetIterator, targetSheetName);
            String tableRelationType = XSSFRelation.TABLE.getRelation();
            boolean hasTableRelationship = false;

            for (PackageRelationship rel : sheetPart.getRelationships()) {
                if (!tableRelationType.equals(rel.getRelationshipType())) {
                    continue;
                }
                hasTableRelationship = true;

                PackagePart tablePart = sheetPart.getRelatedPart(rel);
                try (InputStream is = tablePart.getInputStream()) {
                    TableMetadata metadata = tableReader.read(sheetDefinition.getSheetName(), is);
                    if (targetTableName.equalsIgnoreCase(metadata.tableName())) {
                    	validateColumn(sheetDefinition, targetTableName, metadata);
                        return metadata;
                    }
                }
            }
            if (!hasTableRelationship) {
                throw new IllegalArgumentException(
                        "Worksheet '" + targetSheetName + "' does not contain any Excel table metadata. "
                                + "Create a formatted Excel Table in the .xlsx file before importing."
                );
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read internal .xlsx table metadata for worksheet '" + targetSheetName + "'.", e);
        } catch (OpenXML4JException e) {
            throw new IllegalArgumentException(
                    "Unsupported Excel file. The workbook must be a valid .xlsx OpenXML file with Excel table metadata.",
                    e
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unexpected failure while parsing Excel table metadata for worksheet '" + targetSheetName
                            + "' and table '" + targetTableName + "'.",
                    e
            );
        }

        throw new IllegalArgumentException(
                "Table '" + targetTableName + "' was not found in worksheet '" + targetSheetName
                        + "'. Verify the file is .xlsx and the target range is formatted as an Excel Table."
        );
    }

	private void validateColumn(SheetDefinition<?> sheetDefinition, String targetTableName, TableMetadata metadata) {
		for(var columnDefinition : sheetDefinition.getTable().getColumns()) {
			if(
				!columnDefinition.isIgnoreNotFound() && 
				!metadata.columns().contains(columnDefinition.getHeader())
			) {
				throw new IllegalArgumentException(
					"Column " + columnDefinition.getHeader() + 
					" is not found in table " + targetTableName + "."
				);
			}
		}
	}

    private PackagePart findSheetPart(XSSFReader.SheetIterator sheetIterator, String targetSheetName) {
        while (sheetIterator.hasNext()) {
            try (InputStream is = sheetIterator.next()) {
                if (targetSheetName.equalsIgnoreCase(sheetIterator.getSheetName())) {
                    return sheetIterator.getSheetPart();
                }
            } catch (IOException e) {
                throw new IllegalStateException("An I/O failure occurred while advancing through the worksheet iteration structure.", e);
            }
        }
        throw new IllegalArgumentException("The requested worksheet named '" + targetSheetName + "' was not found in the workbook container.");
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
