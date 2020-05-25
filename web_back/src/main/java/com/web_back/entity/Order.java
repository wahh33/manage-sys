package com.web_back.entity;

import lombok.Data;

@Data
public class Order {
	private Integer id;
	private Integer userId;
	private Integer clientId;
	private String createTime;
	private Integer type;
	private Integer state;
	private Boolean deleted;
	private String creatorName;
	private String clientName;
	private Double totalPrice;
	private Double finishPrice;
	private Double discount;
	private Boolean isReturn;
	private Long syncTime;
}
