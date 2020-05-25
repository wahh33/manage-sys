package com.web_back.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

import com.web_back.entity.Pay_record;
import com.web_back.service.PayRecordService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

@RestController
@RequestMapping("/api/payRecord")
public class PayRecordApi extends BaseApi {
	@Autowired
	PayRecordService payRecordService;

	@PostMapping("/getPayRecordPage")
	public ActionResult getPayRecordPage(@RequestBody PageModel<Pay_record> pageModel){
		return success("",payRecordService.getPayRecordPage(pageModel));
	}
	
	@PostMapping("/savePayRecord")
	public ActionResult savePayRecord(@RequestBody Pay_record payRecord){
		return success("",payRecordService.savePayRecord(payRecord));
	}
	
	@PostMapping("/deletePayRecord")
	public ActionResult deletePayRecord(@RequestBody Pay_record payRecord){
		return success("",payRecordService.deletePayRecord(payRecord.getId()));
	}
	
	@PostMapping("/getPayRecord")
	public 
	ActionResult getPayRecord(@RequestBody Pay_record payRecord){
		return success("",payRecordService.getPayRecord(payRecord));
	}

	@PostMapping("/getPayRecordSum")
	public ActionResult getPayRecordSum(@RequestBody PayRecordParams payRecordParams){
		return success("",payRecordService.getPayRecordSum(payRecordParams.getDateFormatType(),payRecordParams.getDateRange().get(0),payRecordParams.getDateRange().get(1),payRecordParams.getClientId()));
	}
}

@Data
class PayRecordParams{
	private Integer dateFormatType;
	private List<String> dateRange;
	private Integer clientId;
}
