package com.web_back.utils;

public class PageResult {
	public Object data;
	public long total;
	public int pageNum;
	public int pageSize;
	public PageResult(Object data,long total,int pageNum,int pageSize) {
		this.data=data;
		this.total=total;
		this.pageNum=pageNum;
		this.pageSize=pageSize;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
