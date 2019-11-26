package com.oppscience.sgevt.extraction.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CustomExcelTab {
	
	private String fileName;
	private String tabName;
	private List<String> columns;
	private Map<Integer,ArrayList<Object>> rows;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	public Map<Integer, ArrayList<Object>> getRows() {
		return rows;
	}
	
	public void setRows(Map<Integer, ArrayList<Object>> rows) {
		this.rows = rows;
	}
	
	
}