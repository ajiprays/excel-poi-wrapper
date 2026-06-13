package com.aji_prayitno.excel.exporter.core.builder.border;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aji_prayitno.excel.exporter.model.border.ManualTableColumnDefinition;
import com.aji_prayitno.excel.exporter.model.border.HeaderCell;
import com.aji_prayitno.excel.exporter.model.border.HeaderNode;

public final class HeaderTreeBuilder {
	private final Logger logger = LoggerFactory.getLogger(HeaderTreeBuilder.class);
	
	private HeaderTreeBuilder() {
	}

	public static <T> HeaderNode build(List<ManualTableColumnDefinition<T>> columns) {
		HeaderNode root = new HeaderNode("__ROOT__");
		for (ManualTableColumnDefinition<?> column : columns) {
			HeaderCell header = column.getHeader();
			addPath(root, header);
		}

		calculateDepth(root, 0);
		calculateColSpan(root);
		int maxDepth = findMaxDepth(root);
		calculateRowSpan(root, maxDepth);
		assignStartColumn(root, 0);
		return root;
	}

	private static void addPath(HeaderNode root, HeaderCell header) {
		HeaderNode current = root;
		for (String label : header.getPaths()) {
			HeaderNode child = findChild(current, label);
			if (child == null) {
				child = new HeaderNode(label);
				current.addChild(child);
			}
			current = child;
		}
	}

	private static HeaderNode findChild(HeaderNode parent, String label) {
		for (HeaderNode child : parent.getChildren()) {
			if (child.getLabel().equals(label)) {
				return child;
			}
		}
		return null;
	}

	private static void calculateDepth(HeaderNode node, int depth) {
		node.setDepth(depth);
		for (HeaderNode child : node.getChildren()) {
			calculateDepth(child, depth + 1);
		}
	}

	private static int calculateColSpan(HeaderNode node) {
		if (node.isLeaf()) {
			node.setColSpan(1);
			return 1;
		}

		int span = 0;
		for (HeaderNode child : node.getChildren()) {
			span += calculateColSpan(child);
		}

		node.setColSpan(span);
		return span;
	}

	private static int findMaxDepth(HeaderNode node) {
		int max = node.getDepth();
		for (HeaderNode child : node.getChildren()) {
			max = Math.max(max, findMaxDepth(child));
		}
		return max;
	}

	private static void calculateRowSpan(HeaderNode node, int maxDepth) {
		if (node.isLeaf()) {
			node.setRowSpan(maxDepth - node.getDepth() + 1);
			return;
		}
		node.setRowSpan(1);
		for (HeaderNode child : node.getChildren()) {
			calculateRowSpan(child, maxDepth);
		}
	}

	private static int assignStartColumn(HeaderNode node, int startColumn) {
		node.setStartColumn(startColumn);
		if (node.isLeaf()) {
			return startColumn + 1;
		}

		int current = startColumn;
		for (HeaderNode child : node.getChildren()) {
			current = assignStartColumn(child, current);
		}
		return current;
	}
	
	public static List<List<HeaderNode>> levels(HeaderNode root) {
		List<List<HeaderNode>> levels = new ArrayList<>();
		collectLevels(root, levels);
		if (!levels.isEmpty()) {
			levels.remove(0);
		}
		return levels;
	}

	private static void collectLevels(HeaderNode node, List<List<HeaderNode>> levels) {
		while (levels.size() <= node.getDepth()) {
			levels.add(new ArrayList<>());
		}

		levels.get(node.getDepth()).add(node);
		for (HeaderNode child : node.getChildren()) {
			collectLevels(child, levels);
		}
	}
}