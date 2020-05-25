package com.web_back.entity;

import lombok.Data;

@Data
public class User {
	private Integer id;
	private String name;
	private String password;
	private String phoneNum;
	private String eMail;
	private String role;
	private String imgUrl;
	private Boolean deleted;
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
}
