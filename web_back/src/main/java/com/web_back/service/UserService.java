package com.web_back.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.User;
import com.web_back.entity.Need_plan;
import com.web_back.entity.Order;
import com.web_back.entity.Store_apply;
import com.web_back.mapper.IUserMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("UserService")
public class UserService {
	@Autowired
	private IUserMapper userMapper;
	@Autowired
	private NeedPlanService needPlanService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private StoreApplyService storeApplyService;
	
	public User getUserByPhone(User user) {
		return userMapper.getUserByPhone(user);
	}
	
	public List<User> getUser(User user,boolean noPsw){
		List<User> results=userMapper.getUser(user);
		if(noPsw){
			results.forEach(o->o.setPassword(null));
		}
		return results;
	}
	
	public PageResult getUserPage(PageModel<User> userPage,boolean noPsw) {
		Page<User> pageInfo=PageHelper.startPage(userPage.getStart(),userPage.getLength(),userPage.getSortColumn()+" "+userPage.getSortType());
		return new PageResult(getUser(userPage.getFilter(),noPsw),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	public Integer saveUser(User user) {
		Integer userId=user.getId();
		String phoneNum=user.getPhoneNum();
		List<User> searchUser=new ArrayList<User>();
		if(phoneNum!=null && phoneNum.trim()!="") {
			User filterUser=new User();
			filterUser.setPhoneNum(phoneNum);
			searchUser=getUser(filterUser,false);
		}
		if(userId!=null) {
			if(!searchUser.isEmpty() && !searchUser.get(0).getId().equals(userId)) {
				throw new RuntimeException("手机号已存在！请重新添加");
			}
			return userMapper.updateUser(user)>0?userId:null;
		}else {
			if(!searchUser.isEmpty()) {
				throw new RuntimeException("手机号已存在！请重新添加");
			}
			return userMapper.addUser(user)>0?user.getId():null;
		}
	}
	
	public boolean deleteUser(int id) {
		User userParams=new User();
		userParams.setId(id);
		userParams.setDeleted(true);
		return userMapper.updateUser(userParams)>0?true:false;
	}

	public Object getWorkInfo(Integer id,String name,String role){
		WorkInfo workInfo=new WorkInfo();
		Need_plan np;
		Order od;
		Store_apply sa;
		List<Integer> result;
		List<Integer> init=new ArrayList<Integer>();
		init.add(null);
		init.add(null);
		init.add(null);
		workInfo.id=id;
		workInfo.name=name;
		workInfo.role=role;
		//storeman
		if(role.equals("storeman")){
			sa=new Store_apply();
			result=new ArrayList<Integer>();
			//待确认
			sa.setState(0);
			result.add(storeApplyService.getStoreApplyCount(sa));
			//进行中
			sa.setState(1);
			result.add(storeApplyService.getStoreApplyCount(sa));
			workInfo.storeApplyCount=new ArrayList<Integer>(result);
			return workInfo;
		}

		//plan
		np=new Need_plan();
		result=new ArrayList<Integer>(init);
		np.setNeedPlanType(1);
		if(!role.equals("admin")&&!role.equals("manager")){
			np.setCreatorId(id);
			//plan列表
			PageHelper.startPage(1,6,"'startTime' DESC");
			workInfo.planList=needPlanService.getNeedPlan(np);
			//已完成
			np.setState(2);
			result.set(2, needPlanService.getNeedPlanCount(np));
		}
		//待确认
		np.setState(0);
		result.set(0,needPlanService.getNeedPlanCount(np));
		//进行中
		np.setState(1);
		result.set(1,needPlanService.getNeedPlanCount(np));
		workInfo.planCount=new ArrayList<Integer>(result);

		//isReturn false 为order ，true 为 return
		if(!role.equals("producer")){
			boolean[] isReturns={false,true};
			for(boolean isReturn : isReturns){
				od=new Order();
				result=new ArrayList<Integer>(init);
				od.setIsReturn(isReturn);
				if(!role.equals("admin")&&!role.equals("manager")){
					od.setUserId(id);
					//order/return列表
					PageHelper.startPage(1,6,"'createTime' DESC");
					if(isReturn){
						workInfo.returnList=orderService.getOrder(od);
					} else{
						workInfo.orderList=orderService.getOrder(od);
					}
					//已完成
					od.setState(2);
					result.set(2, orderService.getOrdeCount(od));
				}
				//待确认
				od.setState(0);
				result.set(0,orderService.getOrdeCount(od));
				//进行中
				od.setState(1);
				result.set(1,orderService.getOrdeCount(od));
				if(isReturn){
					workInfo.returnCount=new ArrayList<Integer>(result);
				} else{
					workInfo.orderCount=new ArrayList<Integer>(result);
				}
			}
		}

		//needCount
		if(role.equals("buyer")){
			np=new Need_plan();
			result=new ArrayList<Integer>();
			np.setBuySellType(0);
			np.setNeedPlanType(0);
			//待确认
			np.setState(0);
			result.add(needPlanService.getNeedPlanCount(np));
			//进行中
			np.setState(1);
			result.add(needPlanService.getNeedPlanCount(np));
			workInfo.needCount=new ArrayList<Integer>(result);
		}
		if(role.equals("producer")){
			np=new Need_plan();
			result=new ArrayList<Integer>();
			np.setBuySellType(1);
			np.setNeedPlanType(0);
			//待确认
			np.setState(0);
			result.add(needPlanService.getNeedPlanCount(np));
			//进行中
			np.setState(1);
			result.add(needPlanService.getNeedPlanCount(np));
			//已完成
			np.setSurerId(id);
			np.setState(2);
			result.add(needPlanService.getNeedPlanCount(np));
			workInfo.needCount=new ArrayList<Integer>(result);
			//needList
			np.setState(null);
			PageHelper.startPage(1,6,"'startTime' DESC");
			workInfo.productNeedList=needPlanService.getNeedPlan(np);
		}

		return workInfo;
	}
}

class WorkInfo {
	public Integer id;
	public String role;
	public String name;
	public List<Integer> needCount;
	public List<Integer> planCount;
	public List<Integer> orderCount;
	public List<Integer> returnCount ;
	public List<Integer> storeApplyCount;
	public List<Order> orderList;
	public List<Order> returnList;
	public List<Need_plan> planList;
	public List<Need_plan> productNeedList;
}