package com.web_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.Need_plan;
import com.web_back.entity.Need_plan_detail;
import com.web_back.mapper.INPDetailMapper;
import com.web_back.mapper.INeedPlanMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("NeedPlanService")
public class NeedPlanService {
	@Autowired
	private INeedPlanMapper needPlanMapper;
	@Autowired
	private INPDetailMapper npDetailMapper;
	@Value("${web.sync.error}")
	private String syncError;

	public List<Need_plan> getNeedPlan(Need_plan needPlan){
		return needPlanMapper.getNeedPlan(needPlan);
	}
	
	public PageResult getNeedPlanPage(PageModel<Need_plan> needPlanPage) {
		Page<Need_plan> pageInfo=PageHelper.startPage(needPlanPage.getStart(),needPlanPage.getLength(),needPlanPage.getSortColumn()+" "+needPlanPage.getSortType());
		return new PageResult(getNeedPlan(needPlanPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	public Integer saveNeedPlan(Need_plan needPlan,boolean useSync){
		Integer needPlanId=needPlan.getId();
		Long syncTime=needPlan.getSyncTime();
		needPlan.setSyncTime(System.currentTimeMillis());
		if(needPlanId!=null) {
			if(useSync){
				Need_plan npParams=new Need_plan();
				npParams.setId(needPlanId);
				Need_plan oldNP=getNeedPlan(npParams).get(0);
				if(!syncTime.equals(oldNP.getSyncTime())){
					throw new RuntimeException(syncError);
				}
			}
			return needPlanMapper.updateNeedPlan(needPlan)>0?needPlanId:null;
		}else {
			return needPlanMapper.addNeedPlan(needPlan)>0?needPlan.getId():null;
		}
	}
	
	public boolean deleteNeedPlan(int id, Long syncTime) {
		Need_plan needPlanParams=new Need_plan();
		needPlanParams.setId(id);
		needPlanParams.setSyncTime(syncTime);
		needPlanParams.setDeleted(true);
		return saveNeedPlan(needPlanParams,true)!=null?true:false;
	}
	
	public boolean updateState(int needPlanId) {
		Need_plan_detail npDetailParams=new Need_plan_detail();
		npDetailParams.setNeedPlanId(needPlanId);
		npDetailParams.setState(0);
		Need_plan needPlanParams=new Need_plan();
		needPlanParams.setId(needPlanId);
		if(npDetailMapper.getNPDetail(npDetailParams).isEmpty()) {
			npDetailParams.setState(1);
			if(npDetailMapper.getNPDetail(npDetailParams).isEmpty()) {
				needPlanParams.setState(2);
			}else {
				needPlanParams.setState(1);
			}
		}else {
			needPlanParams.setState(1);
		}
		return saveNeedPlan(needPlanParams,false)!=null?true:false;
	}

	public int getNeedPlanCount(Need_plan needPlan){
		return needPlanMapper.getNeedPlanCount(needPlan);
	}
}
