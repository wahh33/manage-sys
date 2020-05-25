package com.web_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.Need_plan_detail;
import com.web_back.entity.Order;
import com.web_back.entity.Order_detail;
import com.web_back.mapper.IOrderDetailMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("OrderDetailService")
public class OrderDetailService {
	@Autowired
	private IOrderDetailMapper OrderDetailMapper;
	@Autowired
	private OrderService orderService;
	@Autowired
	private NPDetailService npDetailService;
	@Value("${web.sync.error}")
	private String syncError;
	
	public List<Order_detail> getOrderDetail(Order_detail OrderDetail){
		return OrderDetailMapper.getOrderDetail(OrderDetail);
	}
	
	public PageResult getOrderDetailPage(PageModel<Order_detail> orderDetailPage) {
		Page<Order_detail> pageInfo=PageHelper.startPage(orderDetailPage.getStart(),orderDetailPage.getLength(),orderDetailPage.getSortColumn()+" "+orderDetailPage.getSortType());
		return new PageResult(getOrderDetail(orderDetailPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	@Transactional
	public Integer saveOrderDetail(Order_detail orderDetail,boolean useSync) {
		Integer orderDetailId=orderDetail.getId();
		Order odParams=new Order();
		odParams.setId(orderDetail.getOrderId());
		if(useSync){
			Order oldOD=orderService.getOrder(odParams).get(0);
			if(!orderDetail.getSyncTime().equals(oldOD.getSyncTime())){
				throw new RuntimeException(syncError);
			}
		}
		if(orderService.saveOrder(odParams, false)==null){
			return null;
		}
		if(orderDetailId!=null) {
			if(orderDetail.getState()!=null&&orderDetail.getState()==1) {
				if(OrderDetailMapper.updateOrderDetail(orderDetail)>0&&orderService.updateState(orderDetail.getOrderId())&&npDetailService.finishNPDetail(JSON.parseArray(orderDetail.getNpDetailIds(),Integer.class))) {
					return orderDetailId;
				}else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return null;
				}
			}else {
				if(OrderDetailMapper.updateOrderDetail(orderDetail)>0){
					return orderDetailId;
				}else{
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return null;
				}
			}
		}else {
			if( OrderDetailMapper.addOrderDetail(orderDetail)>0){
				return orderDetail.getId();
			}else{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return null;
			}
		}
	}
	
	@Transactional
	public boolean addNPDetailToOrder(int npDetailId,int orderId) {
		//获取npDetail
		Need_plan_detail npDetailParams=new Need_plan_detail();
		npDetailParams.setId(npDetailId);
		npDetailParams=npDetailService.getNPDetail(npDetailParams).get(0);
		//npDetail是否已被选中
		if(npDetailParams.getState().intValue()!=0){
			return false;
		}
		//获取货物编号，添加数量
		int goodsId=npDetailParams.getGoodsId();
		int count=npDetailParams.getCount();
		//设置orderDetail查询条件
		Order_detail orderDetailParams=new Order_detail();
		orderDetailParams.setOrderId(orderId);
		orderDetailParams.setGoodsId(goodsId);
		//判断是否存在orderDetail
		List<Order_detail> orderDetailList = getOrderDetail(orderDetailParams);
		if(orderDetailList.isEmpty()) {
			orderDetailParams.setCount(count);
			orderDetailParams.setNpDetailIds("["+npDetailId+"]");
		}else {
			orderDetailParams=orderDetailList.get(0);
			orderDetailParams.setCount(orderDetailParams.getCount()+count);
			String npDetailIds=orderDetailParams.getNpDetailIds();
			List<Integer> npDetailIdsArr=JSON.parseArray(npDetailIds, Integer.class);
			npDetailIdsArr.add(npDetailId);
			orderDetailParams.setNpDetailIds(JSON.toJSONString(npDetailIdsArr));
		}
		if(saveOrderDetail(orderDetailParams, false)!=null) {
			npDetailParams.setState(1);
			if(npDetailService.saveNPDetail(npDetailParams,false)==null) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}else {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public boolean addNPDetailGroupToOrder(int[] npDetailIds,Integer orderId,Long syncTime){
		Order orderParams=new Order();
		orderParams.setId(orderId);
		orderParams=orderService.getOrder(orderParams).get(0);
		if(!orderParams.getSyncTime().equals(syncTime)){
			throw new RuntimeException(syncError);
		}
		for(int npDetailId : npDetailIds) {
			if(!addNPDetailToOrder(npDetailId,orderId)) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return false;
			}
		}
		return true;
	}
	
	@Transactional
	public boolean deleteOrderDetail(int id,long syncTime) {
		Order_detail orderDetailParams=new Order_detail();
		orderDetailParams.setId(id);
		orderDetailParams=getOrderDetail(orderDetailParams).get(0);
		List<Integer> npDetailIds=JSON.parseArray(orderDetailParams.getNpDetailIds(), Integer.class);
		orderDetailParams.setSyncTime(syncTime);
		orderDetailParams.setDeleted(true);
		if(saveOrderDetail(orderDetailParams,true)==null) {
			return false;
		}
		for(Integer npDetailId : npDetailIds) {
			Need_plan_detail npDetailParams=new Need_plan_detail();
			npDetailParams.setId(npDetailId);
			npDetailParams=npDetailService.getNPDetail(npDetailParams).get(0);
			npDetailParams.setState(0);
			if(npDetailService.saveNPDetail(npDetailParams,false)==null) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return false;
			}
		}
		return true;
	}
	
	public boolean addDetailToOrder(int orderId,int goodsId,int count,long syncTime) {
		//设置orderDetail查询条件
		Order_detail orderDetailParams=new Order_detail();
		orderDetailParams.setOrderId(orderId);
		orderDetailParams.setGoodsId(goodsId);
		//判断是否存在orderDetail
		List<Order_detail> orderDetailList = getOrderDetail(orderDetailParams);
		if(orderDetailList.isEmpty()) {
			orderDetailParams.setCount(count);
			orderDetailParams.setNpDetailIds("[]");
		}else {
			orderDetailParams=orderDetailList.get(0);
			orderDetailParams.setCount(orderDetailParams.getCount()+count);
		}
		orderDetailParams.setSyncTime(syncTime);
		return saveOrderDetail(orderDetailParams, true)!=null?true:false;
	}

	public List<Order_detail> getOrderDetailSum(Integer orderType){
		return OrderDetailMapper.getOrderDetailSum(orderType);
	}
	
	public PageResult getOrderDetailSumPage(PageModel<Integer> orderDetailPage) {
		Page<Order_detail> pageInfo=PageHelper.startPage(orderDetailPage.getStart(),orderDetailPage.getLength());
		return new PageResult(getOrderDetailSum(orderDetailPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
}
