package com.web_back.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_back.entity.Store_apply;
import com.web_back.entity.Store_apply_detail;
import com.web_back.service.StoreApplyService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

import lombok.Data;

@RestController
@RequestMapping("/api/storeApply")
public class StoreApplyApi extends BaseApi {
	@Autowired
	StoreApplyService StoreApplyService;

	@PostMapping("/getStoreApplyPage")
	public ActionResult getStoreApplyPage(@RequestBody PageModel<Store_apply> pageModel){
		return success("",StoreApplyService.getStoreApplyPage(pageModel));
	}

	@PostMapping("/saveStoreApply")
	public ActionResult saveStoreApply(@RequestBody Store_apply storeApply){
		return success("",StoreApplyService.saveStoreApply(storeApply,true));
	}

	@PostMapping("/deleteStoreApply")
	public ActionResult deleteStoreApply(@RequestBody Store_apply storeApply){
		return success("",StoreApplyService.deleteStoreApply(storeApply.getId(),storeApply.getSyncTime()));
	}
	
	@PostMapping("/addStoreApply")
	public ActionResult addStoreApply(@RequestBody StoreApplyParams storeApplyParams){
		return success("",StoreApplyService.addStoreApply(storeApplyParams.getStoreApply(),storeApplyParams.getSaDetailList()));
	}
	
	@PostMapping("/getStoreApply")
	public ActionResult getStoreApply(@RequestBody Store_apply storeApply){
		return success("",StoreApplyService.getStoreApply(storeApply));
	}
}

@Data
class StoreApplyParams{
	private Store_apply storeApply;
	private List<Store_apply_detail>  saDetailList;
}
