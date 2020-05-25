package com.web_back.entity;

import lombok.Data;

@Data
public class Store_apply_detail {
	private Integer id;
	private Integer goodsId;
	private Integer storeApplyId;
	private Integer preInOutCount;
	private Integer reaInOutCount;
	private Integer state;
	private String goodsName;
	private Integer goodsCount;
	private Boolean deleted;
	private String createTime;
	private Integer totalCount;
	private Integer inCount;
	private Integer outCount;
	private Integer inOutType;
	private Long syncTime;
}
