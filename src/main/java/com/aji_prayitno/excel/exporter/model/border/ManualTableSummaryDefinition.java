package com.aji_prayitno.excel.exporter.model.border;

public class ManualTableSummaryDefinition {
	private String label;
	private Object value;
	private String styleCustom;
	public ManualTableSummaryDefinition(String label, Object value, String styleCustom) {
		this.label = label;
		this.value = value;
		this.styleCustom = styleCustom;
	}
	public String label() {
		return label;
	}
	public Object value() {
		return value;
	}
	public String styleCustom() {
		return styleCustom;
	}
	
}