package com.web_back.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.Order;
import com.web_back.entity.Pay_record;
import com.web_back.mapper.IPayRecordMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("PayRecordService")
public class PayRecordService {
	@Autowired
	private IPayRecordMapper payRecordMapper;
	@Autowired
	private OrderService orderService;
	
	public List<Pay_record> getPayRecord(Pay_record payRecord){
		return payRecordMapper.getPayRecord(payRecord);
	}
	
	public PageResult getPayRecordPage(PageModel<Pay_record> payRecordPage) {
		Page<Pay_record> pageInfo=PageHelper.startPage(payRecordPage.getStart(),payRecordPage.getLength(),payRecordPage.getSortColumn()+" "+payRecordPage.getSortType());
		return new PageResult(getPayRecord(payRecordPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	@Transactional
	public Integer savePayRecord(Pay_record payRecord){
		Integer payRecordId=payRecord.getId();
		if(payRecordId!=null) {
			return payRecordMapper.updatePayRecord(payRecord)>0?payRecordId:null;
		}else {
			Order orderParams=new Order();
			orderParams.setId(payRecord.getOrderId());
			Order order=orderService.getOrder(orderParams).get(0);
			BigDecimal totalPrice=new BigDecimal(order.getTotalPrice().toString());
			BigDecimal finishPrice=new BigDecimal(order.getFinishPrice().toString());
			BigDecimal newPrice=new BigDecimal(payRecord.getTotalPrice().toString());
			if(payRecord.getTotalPrice()>totalPrice.subtract(finishPrice).doubleValue()) {
				throw new RuntimeException("金额大于所需金额！");
			}
			orderParams.setFinishPrice(finishPrice.add(newPrice).doubleValue());
			if(payRecordMapper.addPayRecord(payRecord)>0&&orderService.saveOrder(orderParams,false)!=null&&orderService.updateState(order.getId())) {
				return payRecord.getId();
			}else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return null;
			}
		}
	}
	
	public boolean deletePayRecord(int id) {
		Pay_record payRecordParams=new Pay_record();
		payRecordParams.setId(id);
		payRecordParams.setDeleted(true);
		return payRecordMapper.updatePayRecord(payRecordParams)>0?true:false;
	}

	public List<Pay_record> getPayRecordSum(Integer dateFormatType, String startTime, String endTime, Integer clientId){
		Map<String,Object> params=new HashMap<String,Object>();
		List<String> dateFormatArr=new ArrayList<String>();
		dateFormatArr.add("%Y-%m-%d");
		dateFormatArr.add("%Y-%m");
		dateFormatArr.add("%Y");
		params.put("dateFormat", dateFormatArr.get(dateFormatType==null?0:dateFormatType.intValue()));
		params.put("startTime",startTime);
		params.put("endTime",endTime);
		params.put("clientId",clientId);
		return payRecordMapper.getPayRecordSum(params);
	}
}
