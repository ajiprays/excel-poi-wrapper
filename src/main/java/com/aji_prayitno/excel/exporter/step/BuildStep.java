package com.aji_prayitno.excel.exporter.step;

import java.io.OutputStream;


public interface BuildStep {
	
	BuildStep streaming(boolean streaming);
	BuildStep rowAccessWindowSize(int rowAccessWindowSize);

	byte[] build();
	void build(OutputStream out);
	
}