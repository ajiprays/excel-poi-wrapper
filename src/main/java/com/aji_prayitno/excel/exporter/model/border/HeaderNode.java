package com.aji_prayitno.excel.exporter.model.border;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HeaderNode {

	private final String label;
	private final List<HeaderNode> children = new ArrayList<>();
	private HeaderNode parent;
	private int startColumn;
	private int depth;
	private int colSpan;
	private int rowSpan;

	public HeaderNode(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public List<HeaderNode> getChildren() {
		return Collections.unmodifiableList(children);
	}
	public void addChild(HeaderNode child) {
		child.parent = this;
		children.add(child);
	}
	public HeaderNode getParent() {
		return parent;
	}
	public int getStartColumn() {
		return startColumn;
	}
	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}
	public boolean isLeaf() {
		return children.isEmpty();
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getColSpan() {
		return colSpan;
	}
	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}
	public int getRowSpan() {
		return rowSpan;
	}
	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}
}