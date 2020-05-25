package com.web_back.entity;

import lombok.Data;

@Data
public class Client {
	private Integer id;
	private String name;
	private String phoneNum;
	private String eMail;
	private String address;
	private String imgUrl;
	private String description;
	private Double discount;
	private Integer type;
	private Boolean deleted;
	private String clientName;
	private String clientType;
	private String orderCount;
	private String returnCount;
	private String priceCount;
	private Integer clientId;
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
}
