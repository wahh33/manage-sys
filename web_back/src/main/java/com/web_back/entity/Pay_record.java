package com.web_back.entity;

import lombok.Data;

@Data
public class Pay_record {
	private Integer id;
	private Integer userId;
	private Integer orderId;
	private Double totalPrice;
	private String proofUrl;
	private Boolean deleted;
	private String creatorName;
	private Boolean isReturn;
	private String createTime;
	private String orderType;
}
