package com.web_back.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.Goods;
import com.web_back.entity.Need_plan_detail;
import com.web_back.entity.Order_detail;
import com.web_back.entity.Store_apply;
import com.web_back.entity.Store_apply_detail;
import com.web_back.mapper.ISADetailMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("SADetailService")
public class SADetailService {
	@Autowired
	private ISADetailMapper SADetailMapper;
	@Autowired
	private StoreApplyService storeApplyService;
	@Autowired
	private OrderDetailService orderDetailService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private NPDetailService npDetailService;
	@Value("${web.sync.error}")
	private String syncError;

	public List<Store_apply_detail> getSADetail(Store_apply_detail SADetail){
		return SADetailMapper.getSADetail(SADetail);
	}
	
	public PageResult getSADetailPage(PageModel<Store_apply_detail> saDetailPage) {
		Page<Store_apply_detail> pageInfo=PageHelper.startPage(saDetailPage.getStart(),saDetailPage.getLength(),saDetailPage.getSortColumn()+" "+saDetailPage.getSortType());
		return new PageResult(getSADetail(saDetailPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	@Transactional
	public Integer saveSADetail(Store_apply_detail saDetail,boolean useSync) {
		Integer saDetailId=saDetail.getId();
		Store_apply saParams=new Store_apply();
		saParams.setId(saDetail.getStoreApplyId());
		if(useSync){
			Store_apply oldSA=storeApplyService.getStoreApply(saParams).get(0);
			if(!saDetail.getSyncTime().equals(oldSA.getSyncTime())){
				throw new RuntimeException(syncError);
			}
		}
		if(storeApplyService.saveStoreApply(saParams, false)==null){
			return null;
		}
		if(saDetailId!=null) {
			if(saDetail.getState()!=null&&saDetail.getState()==1) {
				if(SADetailMapper.updateSADetail(saDetail)>0&&storeApplyService.updateState(saDetail.getStoreApplyId())) {
					return saDetailId;
				}else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return null;
				}
			}else {
				if(SADetailMapper.updateSADetail(saDetail)>0){
					return saDetailId;
				}else{
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return null;
				}
			}
		}else {
			if(SADetailMapper.addSADetail(saDetail)>0){
				return saDetail.getId();
			}else{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return null;
			}
		}
	}
	
	public boolean deleteSADetail(int id,int storeApplyId,long syncTime) {
		Store_apply_detail saDetailParams=new Store_apply_detail();
		saDetailParams.setId(id);
		saDetailParams.setStoreApplyId(storeApplyId);
		saDetailParams.setSyncTime(syncTime);
		saDetailParams.setDeleted(true);
		return saveSADetail(saDetailParams,true)!=null?true:false;
	}
	
	@Transactional
	public boolean finishSADetail(Store_apply_detail saDetail) {
		//获取reaInOutCount
		int reaInOutCount=saDetail.getReaInOutCount();
		long syncTime=saDetail.getSyncTime();
		Store_apply_detail saDetailParams=new Store_apply_detail();
		saDetailParams.setId(saDetail.getId());
		saDetail=SADetailMapper.getSADetail(saDetailParams).get(0);
		saDetail.setReaInOutCount(reaInOutCount);
		saDetail.setSyncTime(syncTime);
		//获取库存申请id
		int storeApplyId=saDetail.getStoreApplyId();
		//获取goodsId
		int goodsId=saDetail.getGoodsId();
		//设置saDetai状态为完成
		saDetail.setState(1);
		//判断是否update成功
		if(saveSADetail(saDetail,true)==null) {
			return false;
		}
		//获取库存申请
		Store_apply storeApplyParams=new Store_apply();
		storeApplyParams.setId(storeApplyId);
		Store_apply storeApply=storeApplyService.getStoreApply(storeApplyParams).get(0);
		//获取出入类型
		int inOutType=storeApply.getInOutType();
		//获取needPlanOrderType
		if(storeApply.getNeedPlanOrderType()==1) {
			//若为order,获取orderId
			int orderId=storeApply.getEntityId();
			//获取orderDetail
			Order_detail orderDetailParams=new Order_detail();
			orderDetailParams.setOrderId(orderId);
			orderDetailParams.setGoodsId(goodsId);
			Order_detail orderDetail=orderDetailService.getOrderDetail(orderDetailParams).get(0);
			//添加finishCount
			orderDetail.setFinishCount(orderDetail.getFinishCount()+reaInOutCount);
			//若finishCount大于等于count则orderDetail完成
			if(orderDetail.getCount()<=orderDetail.getFinishCount()) {
				orderDetail.setState(1);
			}
			//保存orderDetail
			if(orderDetailService.saveOrderDetail(orderDetail,false)==null) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return false;
			}
		}else {
			//若为needPlan，获取needPlanId
			int needPlanId=storeApply.getEntityId();
			//获取npDetail
			Need_plan_detail npDetailParams=new Need_plan_detail();
			npDetailParams.setNeedPlanId(needPlanId);
			npDetailParams.setGoodsId(goodsId);
			Need_plan_detail npDetail=npDetailService.getNPDetail(npDetailParams).get(0);
			//添加finishCount
			npDetail.setFinishCount(npDetail.getFinishCount()+reaInOutCount);
			//若finishCount大于等于count则npDetail完成
			if(npDetail.getCount().doubleValue()<=npDetail.getFinishCount().doubleValue()) {
				npDetail.setState(2);
			}else {
				npDetail.setState(1);
			}
			//保存npDetail
			if(npDetailService.saveNPDetail(npDetail,false)==null) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return false;
			}
		}
		//获取goods
		Goods goodsParams=new Goods();
		goodsParams.setId(goodsId);
		Goods goods=goodsService.getGoods(goodsParams).get(0);
		//修改goods的库存
		if(inOutType==0) {
			//入库
			goodsParams.setCount(goods.getCount()+reaInOutCount);
		}else {
			//出库
			goodsParams.setCount(goods.getCount()-reaInOutCount);
		}
		if(goodsService.saveGoods(goodsParams)==null) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		return true;
	}

	public List<Store_apply_detail> getSADetailSum(Integer dateFormatType, String startTime, String endTime, Integer goodsId){
		Map<String,Object> params=new HashMap<String,Object>();
		List<String> dateFormatArr=new ArrayList<String>();
		dateFormatArr.add("%Y-%m-%d");
		dateFormatArr.add("%Y-%m");
		dateFormatArr.add("%Y");
		params.put("dateFormat", dateFormatArr.get(dateFormatType==null?0:dateFormatType.intValue()));
		params.put("startTime",startTime);
		params.put("endTime",endTime);
		params.put("goodsId",goodsId);
		return SADetailMapper.getSADetailSum(params);
	}
}
