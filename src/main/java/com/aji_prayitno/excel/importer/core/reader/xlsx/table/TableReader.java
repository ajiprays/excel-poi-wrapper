
package com.aji_prayitno.excel.importer.core.reader.xlsx.table;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.TableDocument;

import com.aji_prayitno.excel.importer.model.metadata.TableMetadata;

public final class TableReader {

	public TableMetadata read(String sheetName, InputStream inputStream) {
        try {
            TableDocument tableDoc = TableDocument.Factory.parse(inputStream);
            CTTable ctTable = tableDoc.getTable();
            String tableRef = ctTable.getRef();
            if (tableRef == null && ctTable.getAutoFilter() != null) {
                tableRef = ctTable.getAutoFilter().getRef();
            }

            if (tableRef == null || tableRef.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    String.format("The required coordinate boundary attribute 'ref' was not found for table '%s' inside worksheet '%s'.", 
                    ctTable.getDisplayName(), sheetName)
                );
            }

            AreaReference area = new AreaReference(tableRef, SpreadsheetVersion.EXCEL2007);
            CellReference first = area.getFirstCell();
            CellReference last = area.getLastCell();
            
            List<String> headers = new ArrayList<>();
            for (CTTableColumn column : ctTable.getTableColumns().getTableColumnList()) {
                headers.add(column.getName());
            }

            return new TableMetadata(
                sheetName, 
                ctTable.getDisplayName(),
                first.getRow(), 
                last.getRow(),
                first.getCol(), 
                last.getCol(),
                headers
            );

        } catch (XmlException e) {
            throw new IllegalArgumentException("Failed to compile internal Excel table definitions due to a malformed XML schema state.", e);
        } catch (IOException e) {
            throw new IllegalStateException("An unexpected I/O failure occurred while streaming Excel table structural metadata.", e);
        }
    }
}