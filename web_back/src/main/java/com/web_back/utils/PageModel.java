package com.web_back.utils;


public class PageModel<T> {
	public T filter;
	public int start;
	public int length;
	public String sortColumn="`id`";
	public String sortType="asc";
	public T getFilter() {
		return filter;
	}
	public void setFilter(T filter) {
		this.filter = filter;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
	public String getSortType() {
		return sortType;
	}
	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
}
