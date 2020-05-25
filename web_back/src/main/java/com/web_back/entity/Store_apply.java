package com.web_back.entity;

import lombok.Data;

@Data
public class Store_apply {
	private Integer id;
	private Integer userId;
	private Integer entityId;
	private Integer needPlanOrderType;
	private Integer inOutType;
	private String creatorName;
	private String createTime;
	private String proofUrl;
	private Integer state;
	private Boolean deleted;
	private Long syncTime;
}
