package com.web_back.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Need_plan_detail;

@Mapper
public interface INPDetailMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `vw_need_plan_detail`"
		, "<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test='goodsId != null'>AND `goodsId` = #{goodsId}</if>"
	    ,"<if test=\"goodsName != null and goodsName != '' \">"
	    ,"<bind name='goodsName' value=\"'%' + goodsName + '%'\" />"
	    ,"AND `goodsName` like #{goodsName}"
	    ,"</if>"
	    ,"<if test='needPlanId != null'>AND `needPlanId` = #{needPlanId}</if>"
	    ,"<if test='count != null'>AND `count` = #{count}</if>"
	    ,"<if test='finishCount != null'>AND `finishCount` = #{finishCount}</if>"
	    ,"<if test='state != null'>AND `state` = #{state}</if>"
	    ,"<if test='buySellType != null'>AND `buySellType` = #{buySellType}</if>"
	    ,"<if test='needPlanType != null'>AND `needPlanType` = #{needPlanType}</if>"
		,"<if test='npState != null'>AND `npState` = #{npState}</if>"
		,"<if test='goodsType != null'>AND `goodsType` = #{goodsType}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Need_plan_detail> getNPDetail(Need_plan_detail npDetail);
	
	@Insert({
		"<script>"
		,"INSERT INTO `need_plan_detail` (`goodsId`"
	    ,"<if test='needPlanId != null'>,`needPlanId`</if>"
	    ,"<if test='count != null'>,`count`</if>"
	    ,"<if test='finishCount != null'>,`finishCount`</if>"
		,",`state`,`deleted`)"
		,"VALUES (#{goodsId}"
		,"<if test='needPlanId != null'>,#{needPlanId}</if>"
	    ,"<if test='count != null'>,#{count}</if>"
	    ,"<if test='finishCount != null'>,#{finishCount}</if>"
	    ,",0,false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addNPDetail(Need_plan_detail npDetail);
	
	@Update({
		"<script>"
		,"UPDATE `need_plan_detail`"
		,"<set>"
	    ,"<if test='goodsId != null'>`goodsId` = #{goodsId},</if>"
	    ,"<if test='needPlanId != null'>`needPlanId` = #{needPlanId},</if>"
	    ,"<if test='count != null'>`count` = #{count},</if>"
	    ,"<if test='finishCount != null'>`finishCount` = #{finishCount},</if>"
	    ,"<if test='state != null'>`state` = #{state},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateNPDetail(Need_plan_detail npDetail); 
	
	@Insert({
		"<script>"
		,"INSERT INTO `need_plan_detail` (`goodsId`,`needPlanId`,`count`,`finishCount`,`state`,`deleted`)"
		,"VALUES"
		,"<foreach item='item' index='index' collection='goodsIds' open='' close='' separator=','>"
		,"(#{item},#{needPlanId},0,0,0,false)"
		,"</foreach>"
		,"</script>"
	})
	public int addNPDetailList(Map<String, Object> params);
	
	@Select({
		"<script>"
		,"SELECT * FROM `vw_need_plan_detail`"
		, "<where>"
		,"AND id IN"
		,"<foreach item='item' index='index' collection='npDetailIds' open='(' close=')' separator=','>"
		,"#{item}"
		,"</foreach>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Need_plan_detail> getNPDetailList(Map<String, Object> params);
}
