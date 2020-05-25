package com.web_back.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_back.entity.Order_detail;
import com.web_back.service.OrderDetailService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

import lombok.Data;

@RestController
@RequestMapping("/api/orderDetail")
public class OrderDetailApi extends BaseApi {
	@Autowired
	OrderDetailService orderDetailService;

	@PostMapping("/getOrderDetailPage")
	public ActionResult getOrderDetailPage(@RequestBody PageModel<Order_detail> pageModel){
		return success("",orderDetailService.getOrderDetailPage(pageModel));
	}
	
	@PostMapping("/saveOrderDetail")
	public ActionResult saveOrderDetail(@RequestBody Order_detail orderDetail){
		return success("",orderDetailService.saveOrderDetail(orderDetail,true));
	}

	@PostMapping("/addNPDetailToOrder")
	public ActionResult addNPDetailToOrder(@RequestBody OrderDetailParams orderDetailParams){
		return success("",orderDetailService.addNPDetailGroupToOrder(orderDetailParams.getNpDetailIds(), orderDetailParams.getOrderId(), orderDetailParams.getSyncTime()));
	}

	@PostMapping("/deleteOrderDetail")
	public ActionResult deleteOrderDetail(@RequestBody Order_detail orderDetail){
		return success("",orderDetailService.deleteOrderDetail(orderDetail.getId(),orderDetail.getSyncTime()));
	}
	
	@PostMapping("/getOrderDetail")
	public ActionResult getOrderDetail(@RequestBody Order_detail orderDetail){
		return success("",orderDetailService.getOrderDetail(orderDetail));
	}
	
	@PostMapping("/addDetailToOrder")
	public ActionResult addDetailToOrder(@RequestBody OrderDetailParams orderDetailParams){
		return success("",orderDetailService.addDetailToOrder(orderDetailParams.getOrderId(),orderDetailParams.getGoodsId(),orderDetailParams.getCount(),orderDetailParams.getSyncTime()));
	}

	@PostMapping("/getOrderDetailSumPage")
	public ActionResult getOrderDetailSumPage(@RequestBody PageModel<Integer> pageModel){
		return success("",orderDetailService.getOrderDetailSumPage(pageModel));
	}
}

@Data
class OrderDetailParams{
	private Integer orderId;
	private int[] npDetailIds;
	private Integer goodsId;
	private Integer count;
	private Long syncTime;
}