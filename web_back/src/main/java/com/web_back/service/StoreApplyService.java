package com.web_back.service;

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
import com.web_back.entity.Need_plan;
import com.web_back.entity.Order;
import com.web_back.entity.Store_apply;
import com.web_back.entity.Store_apply_detail;
import com.web_back.mapper.ISADetailMapper;
import com.web_back.mapper.IStoreApplyMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("StoreApplyService")
public class StoreApplyService {
	@Autowired
	private IStoreApplyMapper StoreApplyMapper;
	@Autowired
	private ISADetailMapper SADetailMapper;
	@Autowired
	private NeedPlanService needPlanService;
	@Autowired
	private OrderService orderService;
	@Value("${web.sync.error}")
	private String syncError;

	public List<Store_apply> getStoreApply(Store_apply storeApply){
		return StoreApplyMapper.getStoreApply(storeApply);
	}
	
	public PageResult getStoreApplyPage(PageModel<Store_apply> storeApplyPage) {
		Page<Store_apply> pageInfo=PageHelper.startPage(storeApplyPage.getStart(),storeApplyPage.getLength(),storeApplyPage.getSortColumn()+" "+storeApplyPage.getSortType());
		return new PageResult(getStoreApply(storeApplyPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	@Transactional
	public Integer saveStoreApply(Store_apply storeApply,boolean useSync) {
		Integer storeApplyId=storeApply.getId();
		Long syncTime=storeApply.getSyncTime();
		storeApply.setSyncTime(System.currentTimeMillis());
		if(storeApplyId!=null) {
			if(useSync){
				Store_apply saParams=new Store_apply();
				saParams.setId(storeApplyId);
				Store_apply oldSA=getStoreApply(saParams).get(0);
				if(!syncTime.equals(oldSA.getSyncTime())){
					throw new RuntimeException(syncError);
				}
			}
			return StoreApplyMapper.updateStoreApply(storeApply)>0?storeApplyId:null;
		}else {
			if(useSync){
				if(storeApply.getNeedPlanOrderType()==0){
					Need_plan npParams=new Need_plan();
					npParams.setId(storeApply.getEntityId());
					Need_plan oldNP=needPlanService.getNeedPlan(npParams).get(0);
					if(!syncTime.equals(oldNP.getSyncTime())){
						throw new RuntimeException(syncError);
					}
					if(needPlanService.saveNeedPlan(npParams, false)==null){
						return null;
					}
				}else{
					Order odParams=new Order();
					odParams.setId(storeApply.getEntityId());
					Order oldOD=orderService.getOrder(odParams).get(0);
					if(!syncTime.equals(oldOD.getSyncTime())){
						throw new RuntimeException(syncError);
					}
					if(orderService.saveOrder(odParams, false)==null){
						return null;
					}
				}
			}
			Store_apply saParams=new Store_apply();
			saParams.setNeedPlanOrderType(storeApply.getNeedPlanOrderType());
			saParams.setEntityId(storeApply.getEntityId());
			saParams.setInOutType(storeApply.getInOutType());
			saParams.setDeleted(false);
			saParams.setState(0);
			boolean hasNotFinishSA=!StoreApplyMapper.getStoreApply(saParams).isEmpty();
			saParams.setState(1);
			hasNotFinishSA=hasNotFinishSA||!StoreApplyMapper.getStoreApply(saParams).isEmpty();
			if(hasNotFinishSA){
				throw new RuntimeException(syncError);
			}
			if(StoreApplyMapper.addStoreApply(storeApply)>0){
				return storeApply.getId();
			}else{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return null;
			}
		}
	}
	
	public boolean deleteStoreApply(int id,long syncTime) {
		Store_apply storeApplyParams=new Store_apply();
		storeApplyParams.setId(id);
		storeApplyParams.setSyncTime(syncTime);
		storeApplyParams.setDeleted(true);
		return saveStoreApply(storeApplyParams,true)!=null?true:false;
	}
	
	@Transactional
	public boolean addStoreApply(Store_apply storeApply,List<Store_apply_detail> saDetailList) {
		if(saveStoreApply(storeApply,true)!=null) {
			int storeApplyId=storeApply.getId();
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("storeApplyId", storeApplyId);
			params.put("saDetailList", saDetailList);
			if(SADetailMapper.addSADetailList(params)>0) {
				return true;
			}else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}
		}
		return false;
	}
	
	public boolean updateState(int storeApplyId) {
		Store_apply_detail saDetailParams=new Store_apply_detail();
		saDetailParams.setStoreApplyId(storeApplyId);
		saDetailParams.setState(0);
		Store_apply storeApplyParams=new Store_apply();
		storeApplyParams.setId(storeApplyId);
		if(SADetailMapper.getSADetail(saDetailParams).isEmpty()) {
			storeApplyParams.setState(2);
		}else {
			storeApplyParams.setState(1);
		}
		return saveStoreApply(storeApplyParams,false)>0?true:false;
	}

	public int getStoreApplyCount(Store_apply storeApply){
		return StoreApplyMapper.getStoreApplyCount(storeApply);
	}
}
