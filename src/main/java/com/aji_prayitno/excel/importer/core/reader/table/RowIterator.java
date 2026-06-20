package com.aji_prayitno.excel.importer.core.reader.table;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.poi.xssf.model.SharedStringsTable;

import com.aji_prayitno.excel.importer.core.Converter;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.metadata.TableMetadata;
import com.aji_prayitno.excel.importer.model.table.TableDefinition;

public final class RowIterator<T> implements 
Iterator<ImportResult<T>>, AutoCloseable {

    private final InputStream sheetStream;
    private final XMLStreamReader xmlReader;
    private final SharedStringsTable sharedStrings;
    private final TableMetadata metadata;

    private final Constructor<T> dtoConstructor;
    private final List<ColumnDefinition<T, ?>> columnDefinitions;

    private Map<String, String> currentRowValues;
    private ImportResult<T> nextRow;
    
    private boolean finished;
    private boolean closed;

    private static final String TAG_ROW = "row";
    private static final String TAG_CELL = "c";
    private static final String TAG_VALUE = "v";
    private static final String ATTR_REF = "r";
    private static final String ATTR_TYPE = "t";
    private static final String TYPE_SHARED_STRING = "s";
    
    private final StringBuilder contents = new StringBuilder();
    private boolean insideTableRow;
    private int currentColumn = -1;
    private int currentRowNumber = -1;
    private String cellType;

    
    public RowIterator(
        InputStream sheetStream,
        SharedStringsTable sharedStrings,
        TableMetadata metadata,
        TableDefinition<T> tableDefinition
    ) {
        this.sheetStream = sheetStream;
        this.sharedStrings = sharedStrings;
        this.metadata = metadata;
        this.columnDefinitions = tableDefinition.getColumns();

        try {
            this.dtoConstructor = tableDefinition.getDtoClass().getDeclaredConstructor();
        } catch (Exception e) {
            throw new IllegalStateException("DTO class must provide a public no-args constructor.", e);
        }

        try {
            XMLInputFactory factory = XMLInputFactory.newFactory();
       	 	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
       	 	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
       	 	factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            factory.setProperty(XMLInputFactory.IS_COALESCING, true);
            this.xmlReader = factory.createXMLStreamReader(sheetStream);
        } catch (XMLStreamException e) {
            throw new IllegalStateException("Failed to initialize XML stream reader.", e);
        }
    }

    @Override
    public boolean hasNext() {
        if (closed || finished) {
            return false;
        }
        if (nextRow == null) {
            nextRow = readNextRow();
            if (nextRow == null) {
                finished = true;
                closeQuietly();
                return false;
            }
        }
        return true;
    }

    @Override
    public ImportResult<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more Excel rows available.");
        }
        ImportResult<T> current = nextRow;
        nextRow = null;
        return current;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        Exception failure = null;
        try {
            xmlReader.close();
        } catch (Exception e) {
            failure = e;
        }
        try {
            sheetStream.close();
        } catch (Exception e) {
            if (failure == null) {
                failure = e;
            } else {
                failure.addSuppressed(e);
            }
        }
        if (failure != null) {
            throw new IllegalStateException("Failed to close Excel streaming resources.", failure);
        }
    }

    private void closeQuietly() {
        try {
            close();
        } catch (Exception ignored) {
        	
        }
    }

    private ImportResult<T> readNextRow() {
        try {
            while (xmlReader.hasNext()) {
                int event = xmlReader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        handleStartElement();
                        break;
                    case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA:
                        if (insideTableRow) {
                            contents.append(xmlReader.getText());
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        ImportResult<T> row = handleEndElement();
                        if (row != null) {
                            return row;
                        }
                        break;
                    default:
                        break;
                }
            }
            return null;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed while reading Excel stream for sheet " + metadata.sheetName() + 
                ", table " + metadata.tableName() + ", row " + toExcelRowNumber(currentRowNumber) + ".", e
            );
        }
    }

    private void handleStartElement() {
        String elementName = xmlReader.getLocalName();
        contents.setLength(0);
        if (TAG_ROW.equals(elementName)) {
            String rowRef = xmlReader.getAttributeValue(null, ATTR_REF);
            if (rowRef == null || rowRef.isBlank()) {
                throw new IllegalStateException("Worksheet row is missing required reference attribute 'r'.");
            }
            currentRowNumber = Integer.parseInt(rowRef) - 1;
            insideTableRow = currentRowNumber > metadata.firstRow() && currentRowNumber <= metadata.lastRow();

            if (insideTableRow) {
                currentRowValues = new HashMap<>();
                for (String header : metadata.columns()) {
                    currentRowValues.put(header, "");
                }
            }
            return;
        }

        if (insideTableRow && TAG_CELL.equals(elementName)) {
            cellType = xmlReader.getAttributeValue(null, ATTR_TYPE);
            String ref = xmlReader.getAttributeValue(null, ATTR_REF);
            if (ref == null || ref.isBlank()) {
                throw new IllegalStateException("Cell inside row " + toExcelRowNumber(currentRowNumber)
                        + " is missing required reference attribute 'r'.");
            }
            currentColumn = convertRefToCol(ref);
        }
    }
    
    private ImportResult<T> handleEndElement() {
        if (!insideTableRow) {
            return null;
        }
        String elementName = xmlReader.getLocalName();
        if (TAG_VALUE.equals(elementName)) {
            String value = resolveCellValue();
            if (currentColumn >= metadata.firstColumn() && currentColumn <= metadata.lastColumn()) {
                int headerIndex = currentColumn - metadata.firstColumn();
                String header = metadata.columns().get(headerIndex);
                currentRowValues.put(header, value);
            }
            return null;
        }

        if (TAG_ROW.equals(elementName)) {
            insideTableRow = false;
            return addCurrentRow(currentRowValues);
        }
        return null;
    }
    
    private int convertRefToCol(String ref) {
        int col = 0;
        for (int i = 0; i < ref.length(); i++) {
            char c = ref.charAt(i);
            if (Character.isDigit(c)) {
                break;
            }
            col = col * 26 + (c - 'A' + 1);
        }
        return col - 1;
    }
    
    private String resolveCellValue() {
        String value = contents.toString();
        if (!TYPE_SHARED_STRING.equals(cellType)) {
            return value;
        }
        try {
            int index = Integer.parseInt(value);
            return sharedStrings
                    .getItemAt(index)
                    .getString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to resolve shared string value at row "
                    + toExcelRowNumber(currentRowNumber) + ", column " + toExcelColumnNumber(currentColumn) + ".", e);
        }
    }
    
    private ImportResult<T> addCurrentRow(Map<String, String> currentRowValues) {
        T dtoInstance;
        try {
            dtoInstance = dtoConstructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create DTO instance.", e);
        }
        Map<String, String> error = new HashMap<>();
        for (ColumnDefinition<T, ?> columnDefinition : columnDefinitions) {
            try {
                setColumnValue(dtoInstance, columnDefinition, currentRowValues);
            } catch (Exception e) {
                String message = columnDefinition.getHeader() + ":" + e.getMessage();
                error.compute(columnDefinition.getHeader(), (k, v) -> v != null ? v + ", " + message : message);
            }
        }
        return new ImportResult<>(dtoInstance, error);
    }

    private <V> void setColumnValue(
        T instance,
        ColumnDefinition<T, V> columnDefinition,
        Map<String, String> currentRowValues
    ) {
        String rawValue = currentRowValues.get(columnDefinition.getHeader());
        V convertedValue = Converter.convert(rawValue, columnDefinition.getTargetType());
        columnDefinition.getSetter().accept(instance, convertedValue);
    }

    private int toExcelRowNumber(int zeroBasedRow) {
        return zeroBasedRow < 0 ? -1 : zeroBasedRow + 1;
    }

    private int toExcelColumnNumber(int zeroBasedColumn) {
        return zeroBasedColumn < 0 ? -1 : zeroBasedColumn + 1;
    }
}
