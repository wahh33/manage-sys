package com.web_back.entity;

import lombok.Data;

@Data
public class Goods {
	private Integer id;
	private String name;
	private Integer type;
	private Double defaultPrice;
	private Integer count;
	private Integer high;
	private Integer low;
	private Boolean isWarn;
	private String description;
	private Boolean deleted;
	private Integer state;
	private Integer _type;
}
