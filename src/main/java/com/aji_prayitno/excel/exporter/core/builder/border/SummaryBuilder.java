package com.aji_prayitno.excel.exporter.core.builder.border;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.model.border.ManualTableSummaryDefinition;

public final class SummaryBuilder {
	private final Logger logger = LoggerFactory.getLogger(SummaryBuilder.class);
	private final List<ManualTableSummaryDefinition> summaries = new ArrayList<>();

	public SummaryBuilder add(String label, Object value) {
		summaries.add(new ManualTableSummaryDefinition(label, value, null));
		return this;
	}
	public SummaryBuilder add(String label, Object value, String customDataType) {
		summaries.add(new ManualTableSummaryDefinition(label, value, customDataType));
		return this;
	}
	List<ManualTableSummaryDefinition> build() {
		return summaries;
	}
}