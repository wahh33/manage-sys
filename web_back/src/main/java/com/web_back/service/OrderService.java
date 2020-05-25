package com.web_back.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.Order;
import com.web_back.entity.Order_detail;
import com.web_back.mapper.IOrderDetailMapper;
import com.web_back.mapper.IOrderMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("OrderService")
public class OrderService {
	@Autowired
	private IOrderMapper orderMapper;
	@Autowired
	private IOrderDetailMapper orderDetailMapper;
	@Autowired
	private OrderDetailService orderDetailService;
	@Value("${web.sync.error}")
	private String syncError;

	public List<Order> getOrder(Order order){
		return orderMapper.getOrder(order);
	}
	
	public PageResult getOrderPage(PageModel<Order> orderPage) {
		Page<Order> pageInfo=PageHelper.startPage(orderPage.getStart(),orderPage.getLength(),orderPage.getSortColumn()+" "+orderPage.getSortType());
		return new PageResult(getOrder(orderPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	public Integer saveOrder(Order order,boolean useSync) {
		Integer orderId=order.getId();
		Long syncTime=order.getSyncTime();
		order.setSyncTime(System.currentTimeMillis());
		if(orderId!=null) {
			if(useSync){
				Order odParams=new Order();
				odParams.setId(orderId);
				Order oldOD=getOrder(odParams).get(0);
				if(!syncTime.equals(oldOD.getSyncTime())){
					throw new RuntimeException(syncError);
				}
			}
			//若修改状态为进行中
			if(order.getState()!=null&&order.getState()==1) {
				//获取discount
				Order orderParams=new Order();
				orderParams.setId(orderId);
				Order orderInfo=getOrder(orderParams).get(0);
				BigDecimal discount=new BigDecimal(orderInfo.getDiscount().toString());
				discount=discount.divide(new BigDecimal("10"));
				//获取所有orderDetail
				Order_detail orderDetailParams=new Order_detail();
				orderDetailParams.setOrderId(orderId);
				List<Order_detail> orderDetailList=orderDetailService.getOrderDetail(orderDetailParams);
				//计算totalPrice
				BigDecimal totalPrice=new BigDecimal("0");
				for(Order_detail orderDetail : orderDetailList) {
					BigDecimal count=new BigDecimal(orderDetail.getCount().toString());
					BigDecimal price=new BigDecimal(orderDetail.getPrice()==null?"0":orderDetail.getPrice().toString());
					totalPrice=totalPrice.add(count.multiply(price));
				}
				order.setTotalPrice(totalPrice.multiply(discount).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			return orderMapper.updateOrder(order)>0?orderId:null;
		}else {
			return orderMapper.addOrder(order)>0?order.getId():null;
		}
	}
	
	public boolean deleteOrder(int id, Long syncTime) {
		Order orderParams=new Order();
		orderParams.setId(id);
		orderParams.setSyncTime(syncTime);
		orderParams.setDeleted(true);
		return saveOrder(orderParams,true)!=null?true:false;
	}
	
	public boolean updateState(int orderId) {
		Order_detail orderDetailParams=new Order_detail();
		orderDetailParams.setOrderId(orderId);
		orderDetailParams.setState(0);
		Order orderParams=new Order();
		orderParams.setId(orderId);
		Order order=getOrder(orderParams).get(0);
		if(order.getState()==1&&order.getFinishPrice().equals(order.getTotalPrice())&&orderDetailMapper.getOrderDetail(orderDetailParams).isEmpty()) {
			orderParams.setState(2);
			return saveOrder(orderParams,false)!=null?true:false;
		}
		return true;
	}

	public int getOrdeCount(Order order){
		return orderMapper.getOrderCount(order);
	}
}
