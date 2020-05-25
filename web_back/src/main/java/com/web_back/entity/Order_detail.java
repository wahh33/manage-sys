package com.web_back.entity;

import lombok.Data;

@Data
public class Order_detail {
	private Integer id;
	private Integer orderId;
	private Integer goodsId;
	private Integer count;
	private Integer finishCount;
	private Double price;
	private String npDetailIds;
	private Integer state;
	private Boolean deleted;
	private String goodsName;
	private Double defaultPrice;
	private Integer goodsCount;
	private Boolean isReturn;
	private Integer orderType;
	private Long syncTime;
}
