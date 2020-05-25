package com.web_back.entity;

import lombok.Data;

@Data
public class Need_plan_detail {
	private Integer id;
	private Integer goodsId;
	private Integer needPlanId;
	private Integer count;
	private Integer finishCount;
	private Integer state;
	private Boolean deleted;
	private String goodsName;
	private Integer goodsCount;
	private Double defaultPrice;
	private Integer buySellType;
	private Integer needPlanType;
	private Integer npState;
	private Long syncTime;
	private Integer goodsType;
}
