package com.web_back.entity;

import lombok.Data;

@Data
public class Need_plan {
	private Integer id;
	private Integer creatorId;
	private Integer surerId;
	private Integer level;
	private String startTime;
	private String endTime;
	private String description;
	private String creatorName;
	private String surerName;
	private Integer needPlanType;
	private Integer buySellType;
	private Integer state;
	private Boolean deleted;
	private Long syncTime;
}
