package com.web_back.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

import java.util.List;

import com.web_back.entity.Store_apply_detail;
import com.web_back.service.SADetailService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

@RestController
@RequestMapping("/api/saDetail")
public class SADetailApi extends BaseApi {
	@Autowired
	SADetailService saDetailService;

	@PostMapping("/getSADetailPage")
	public ActionResult getSADetailPage(@RequestBody PageModel<Store_apply_detail> pageModel){
		return success("",saDetailService.getSADetailPage(pageModel));
	}

	@PostMapping("/saveSADetail")
	public ActionResult saveSADetail(@RequestBody Store_apply_detail saDetail){
		return success("",saDetailService.saveSADetail(saDetail,true));
	}

	@PostMapping("/deleteSADetail")
	public ActionResult deleteSADetail(@RequestBody Store_apply_detail saDetail){
		return success("",saDetailService.deleteSADetail(saDetail.getId(),saDetail.getStoreApplyId(),saDetail.getSyncTime()));
	}
	
	@PostMapping("/getSADetail")
	public ActionResult getSADetail(@RequestBody Store_apply_detail saDetail){
		return success("",saDetailService.getSADetail(saDetail));
	}
	
	@PostMapping("/finishSADetail")
	public ActionResult finishSADetail(@RequestBody Store_apply_detail saDetail){
		return success("",saDetailService.finishSADetail(saDetail));
	}

	@PostMapping("/getSADetailSum")
	public ActionResult getSADetailSum(@RequestBody SADetailParams saDetailParams){
		return success("",saDetailService.getSADetailSum(saDetailParams.getDateFormatType(),saDetailParams.getDateRange().get(0),saDetailParams.getDateRange().get(1),saDetailParams.getGoodsId()));
	}
}

@Data
class SADetailParams{
	private Integer dateFormatType;
	private List<String> dateRange;
	private Integer goodsId;
}
