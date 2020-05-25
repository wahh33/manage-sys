package com.web_back.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_back.entity.Need_plan;
import com.web_back.service.NeedPlanService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

@RestController
@RequestMapping("/api/needPlan")
public class NeedPlanApi extends BaseApi {
	@Autowired
	NeedPlanService NeedPlanService;

	@PostMapping("/getNeedPlanPage")
	public ActionResult getNeedPlanPage(@RequestBody PageModel<Need_plan> pageModel){
		return success("",NeedPlanService.getNeedPlanPage(pageModel));
	}

	@PostMapping("/saveNeedPlan")
	public ActionResult saveNeedPlan(@RequestBody Need_plan needPlan){
		return success("",NeedPlanService.saveNeedPlan(needPlan,true));
	}

	@PostMapping("/deleteNeedPlan")
	public ActionResult deleteNeedPlan(@RequestBody Need_plan needPlan){
		return success("",NeedPlanService.deleteNeedPlan(needPlan.getId(),needPlan.getSyncTime()));
	}
}
