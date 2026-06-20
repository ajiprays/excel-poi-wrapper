package com.aji_prayitno.excel.importer.step.table;

import java.util.List;
import java.util.stream.Stream;

import com.aji_prayitno.excel.importer.model.ImportResult;

public interface WorkbookStep<T> {
	
	/**
	 * Imports records from the source file and returns all results as a list.
	 *
	 * <p>This method consumes the entire import stream and collects all
	 * processed records into memory before returning.</p>
	 *
	 * <p>For large files, consider using {@link #importDataAsStream()}
	 * to process records incrementally and reduce memory usage.</p>
	 *
	 * @return a list containing all imported records and their processing results
	 */
	List<ImportResult<T>> importData();
	
	/**
	 * Imports records from the source file using a streaming approach.
	 *
	 * <p>This method is designed for large files and processes records
	 * sequentially without loading the entire dataset into memory.</p>
	 *
	 * <p>The returned stream should be closed after use to release any
	 * underlying resources associated with the import process.</p>
	 *
	 * @return a stream of imported records and their processing results
	 */
	Stream<ImportResult<T>> importDataAsStream();
	
	/**
	 * Imports all records from the source file and loads the entire result
	 * into memory before returning.
	 *
	 * <p>This method is suitable for small files where memory consumption
	 * is not a concern and random access to the imported data may be required.</p>
	 *
	 * @return a list containing all imported records and their processing results
	 */
	List<ImportResult<T>> importDataSmallFile();
}
