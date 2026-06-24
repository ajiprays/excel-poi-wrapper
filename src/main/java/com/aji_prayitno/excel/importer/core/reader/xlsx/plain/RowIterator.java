package com.aji_prayitno.excel.importer.core.reader.xlsx.plain;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;

import com.aji_prayitno.excel.importer.core.reader.Util;
import com.aji_prayitno.excel.importer.model.ColumnDefinition;
import com.aji_prayitno.excel.importer.model.ImportResult;
import com.aji_prayitno.excel.importer.model.plain.PlainTableDefinition;

public final class RowIterator<T> implements Iterator<ImportResult<T>>, AutoCloseable {

    private final InputStream sheetStream;
    private final XMLStreamReader xmlReader;
    private final ReadOnlySharedStringsTable sharedStrings;
    private final List<ColumnDefinition<T, ?>> columnDefinitions;
    private final Constructor<T> dtoConstructor;
    
    // IDE 1: Menggunakan Map untuk mencatat Hubungan: Index Kolom -> Nama Header Teks
    private final Map<Integer, String> headerMap = new HashMap<>();
    
    // Set pembantu untuk mempercepat pencarian nama kolom yang valid (O(1) lookup)
    private final Set<String> validHeaderNames = new HashSet<>();

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
    private boolean insideHeaderRow;
    private boolean insideDataRow;
    private int currentColumn = -1;
    private int currentRowNumber = -1;
    private String cellType;
    private final int headerRowIndex;

    public RowIterator(
        InputStream sheetStream,
        ReadOnlySharedStringsTable sharedStrings,
        PlainTableDefinition<T> tableDefinition
    ) {
        this.sheetStream = sheetStream;
        this.sharedStrings = sharedStrings;
        this.columnDefinitions = tableDefinition.getColumns();
        this.headerRowIndex = tableDefinition.getStartRowIndex();
        
        // Daftarkan semua nama header yang sah dari konfigurasi user ke dalam Set
        for (var col : columnDefinitions) {
            this.validHeaderNames.add(col.header());
        }

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
        if (closed || finished) return false;
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
        if (!hasNext()) throw new NoSuchElementException("No more rows available.");
        ImportResult<T> current = nextRow;
        nextRow = null;
        return current;
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
                        if (insideHeaderRow || insideDataRow) {
                            contents.append(xmlReader.getText());
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        ImportResult<T> row = handleEndElement();
                        if (row != null) return row;
                        break;
                    default:
                }
            }
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Error reading plain Excel stream at row " + (currentRowNumber + 1), e);
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
            
            insideHeaderRow = (currentRowNumber == headerRowIndex);
            insideDataRow = (currentRowNumber > headerRowIndex);

            if (insideDataRow) {
                currentRowValues = new HashMap<>();
                // IDE 2: Hanya inisialisasi Map data dengan kolom yang BENAR-BENAR terdaftar
                for (String validHeader : validHeaderNames) {
                    currentRowValues.put(validHeader, "");
                }
            }
            return;
        }

        if ((insideHeaderRow || insideDataRow) && TAG_CELL.equals(elementName)) {
            cellType = xmlReader.getAttributeValue(null, ATTR_TYPE);
            String ref = xmlReader.getAttributeValue(null, ATTR_REF);
            if (ref == null || ref.isBlank()) {
                throw new IllegalStateException("Cell inside row " + (currentRowNumber + 1) + " is missing attribute 'r'.");
            }
            currentColumn = Util.convertRefToCol(ref);
        }
    }

    private ImportResult<T> handleEndElement() {
        String elementName = xmlReader.getLocalName();

        if (TAG_VALUE.equals(elementName)) {
            String value = resolveCellValue().trim();
            
            if (insideHeaderRow) {
                // IDE 2: Hanya simpan ke headerMap JIKA nama kolom tersebut terdaftar di columnDefinitions
                if (validHeaderNames.contains(value)) {
                    headerMap.put(currentColumn, value);
                }
            } 
            else if (insideDataRow) {
                // Ambil nama kolom berdasarkan indeks angka fisiknya secara instan (O(1))
                String header = headerMap.get(currentColumn);
                if (header != null) {
                    currentRowValues.put(header, value);
                }
            }
            return null;
        }

        if (TAG_ROW.equals(elementName)) {
            if (insideHeaderRow) {
                insideHeaderRow = false;
                
                // VALIDASI: Dipicu sekali di akhir baris. Memastikan kolom wajib (not ignored) benar-benar ada di Map
                for (var columnDefinition : columnDefinitions) {
                    if (!columnDefinition.ignoreNotFound() && !headerMap.containsValue(columnDefinition.header())) {
                        throw new IllegalArgumentException("Column " + columnDefinition.header() + " is not found.");
                    }
                }
                return null; 
            }
            if (insideDataRow) {
                insideDataRow = false;
                return addCurrentRow(currentRowValues);
            }
        }
        return null;
    }

    private String resolveCellValue() {
        String val = contents.toString();
        if (TYPE_SHARED_STRING.equals(cellType) && sharedStrings != null) {
            int idx = Integer.parseInt(val);
            return sharedStrings.getItemAt(idx).toString();
        }
        return val;
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        try { xmlReader.close(); } catch (Exception ignored) {}
        try { sheetStream.close(); } catch (Exception ignored) {}
    }

    private void closeQuietly() {
        try { close(); } catch (Exception ignored) {}
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
                Util.setColumnValue(dtoInstance, columnDefinition, currentRowValues);
            } catch (Exception e) {
                String message = columnDefinition.header() + ":" + e.getMessage();
                error.compute(columnDefinition.header(), (k, v) -> v != null ? v + ", " + message : message);
            }
        }
        return new ImportResult<>(dtoInstance, error);
    }

}
