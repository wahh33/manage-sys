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
import com.web_back.entity.Need_plan_detail;
import com.web_back.mapper.INPDetailMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("NPDetailService")
public class NPDetailService {
	@Autowired
	private INPDetailMapper NPDetailMapper;
	@Autowired
	private NeedPlanService needPlanService;
	@Value("${web.sync.error}")
	private String syncError;

	public List<Need_plan_detail> getNPDetail(Need_plan_detail NPDetail){
		return NPDetailMapper.getNPDetail(NPDetail);
	}
	
	public PageResult getNPDetailPage(PageModel<Need_plan_detail> npDetailPage) {
		Page<Need_plan_detail> pageInfo=PageHelper.startPage(npDetailPage.getStart(),npDetailPage.getLength(),npDetailPage.getSortColumn()+" "+npDetailPage.getSortType());
		return new PageResult(getNPDetail(npDetailPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	@Transactional
	public Integer saveNPDetail(Need_plan_detail npDetail,boolean useSync) {
		Integer npDetailId=npDetail.getId();
		Need_plan npParams=new Need_plan();
		npParams.setId(npDetail.getNeedPlanId());
		if(useSync){
			Need_plan oldNP=needPlanService.getNeedPlan(npParams).get(0);
			if(!npDetail.getSyncTime().equals(oldNP.getSyncTime())){
				throw new RuntimeException(syncError);
			}
		}
		if(needPlanService.saveNeedPlan(npParams, false)==null){
			return null;
		}
		if(npDetailId!=null) {
			if(npDetail.getState()!=null&&npDetail.getState()==2) {
				if(NPDetailMapper.updateNPDetail(npDetail)>0&&needPlanService.updateState(npDetail.getNeedPlanId())) {
					return npDetailId;
				}else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return null;
				}
			}
			else {
				if(NPDetailMapper.updateNPDetail(npDetail)>0){
					return npDetailId;
				}else{
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return null;
				}
			}
		}else {
			if(NPDetailMapper.addNPDetail(npDetail)>0){
				return npDetail.getId();
			}else{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return null;
			}
		}
	}
	
	@Transactional
	public boolean deleteNPDetail(int id,int needPlanId,long syncTime) {
		Need_plan_detail npDetailParams=new Need_plan_detail();
		npDetailParams.setId(id);
		npDetailParams.setNeedPlanId(needPlanId);
		npDetailParams.setSyncTime(syncTime);
		npDetailParams.setDeleted(true);
		if(saveNPDetail(npDetailParams,true)!=null){
			return true;
		}else{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
	}
	
	@Transactional
	public boolean addNPDetailList(int needPlanId,int[] goodsIds,long syncTime) {
		Need_plan npParams=new Need_plan();
		npParams.setId(needPlanId);
		npParams.setSyncTime(syncTime);
		if(needPlanService.saveNeedPlan(npParams, true)==null){
			return false;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("needPlanId",needPlanId);
		params.put("goodsIds",goodsIds);
		if(NPDetailMapper.addNPDetailList(params)>0){
			return true;
		}else{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
	}
	
	public List<Need_plan_detail> getNPDetailList(int[] npDetailIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("npDetailIds",npDetailIds);
		return NPDetailMapper.getNPDetailList(params);
	}
	
	@Transactional
	public boolean finishNPDetail(List<Integer> npDetailIds) {
		if(npDetailIds.isEmpty()) return true;
		for(Need_plan_detail npDetail : getNPDetailList(npDetailIds.stream().mapToInt(Integer::valueOf).toArray())) {
			npDetail.setState(2);
			if(saveNPDetail(npDetail,false)==null) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return false;
			}
		}
		return true;
	}
}
