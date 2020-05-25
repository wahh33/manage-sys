package com.web_back.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_back.entity.Order;
import com.web_back.service.OrderService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

@RestController
@RequestMapping("/api/order")
public class OrderApi extends BaseApi {
	@Autowired
	OrderService orderService;

	@PostMapping("/getOrderPage")
	public ActionResult getOrderPage(@RequestBody PageModel<Order> pageModel){
		return success("",orderService.getOrderPage(pageModel));
	}
	
	@PostMapping("/saveOrder")
	public ActionResult saveOrder(@RequestBody Order order){
		return success("",orderService.saveOrder(order,true));
	}
	
	@PostMapping("/deleteOrder")
	public ActionResult deleteOrder(@RequestBody Order order){
		return success("",orderService.deleteOrder(order.getId(),order.getSyncTime()));
	}
}
