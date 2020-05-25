package com.web_back.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_back.entity.Need_plan_detail;
import com.web_back.service.NPDetailService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

import lombok.Data;

@RestController
@RequestMapping("/api/npDetail")
public class NPDetailApi extends BaseApi {
	@Autowired
	NPDetailService npDetailService;

	@PostMapping("/getNPDetailPage")
	public ActionResult getNPDetailPage(@RequestBody PageModel<Need_plan_detail> pageModel){
		return success("",npDetailService.getNPDetailPage(pageModel));
	}

	@PostMapping("/saveNPDetail")
	public ActionResult saveNPDetail(@RequestBody Need_plan_detail npDetail){
		return success("",npDetailService.saveNPDetail(npDetail,true));
	}

	@PostMapping("/deleteNPDetail")
	public ActionResult deleteNPDetail(@RequestBody Need_plan_detail npDetail){
		return success("",npDetailService.deleteNPDetail(npDetail.getId(),npDetail.getNeedPlanId(),npDetail.getSyncTime()));
	}
	
	@PostMapping("/getNPDetail")
	public ActionResult getNPDetail(@RequestBody Need_plan_detail npDetail){
		return success("",npDetailService.getNPDetail(npDetail));
	}
	
	@PostMapping("/addNPDetailList")
	public ActionResult addNPDetailList(@RequestBody NPDetailParams params){
		return success("",npDetailService.addNPDetailList(params.getNeedPlanId(),params.getGoodsIds(),params.getSyncTime()));
	}
	
	@PostMapping("/getNPDetailList")
	public ActionResult getNPDetailList(@RequestBody NPDetailParams params){
		return success("",npDetailService.getNPDetailList(params.getNpDetailIds()));
	}
}

@Data
class NPDetailParams{
	private Integer needPlanId;
	private int[] goodsIds;
	private int[] npDetailIds;
	private Long syncTime;
}
