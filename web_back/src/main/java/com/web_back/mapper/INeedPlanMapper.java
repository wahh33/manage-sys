package com.web_back.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Need_plan;

@Mapper
public interface INeedPlanMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `vw_need_plan`"
		, "<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test='creatorId != null'>AND `creatorId` = #{creatorId}</if>"
	    ,"<if test='surerId != null'>AND `surerId` = #{surerId}</if>"
	    ,"<if test='level != null'>AND `level` = #{level}</if>"
	    ,"<if test=\"startTime != null and startTime != '' \">AND `startTime` = #{startTime}</if>"
	    ,"<if test=\"endTime != null and endTime != '' \">AND `endTime` = #{endTime}</if>"
	    ,"<if test=\"description != null and description != '' \">"
	    ,"<bind name='description' value=\"'%' + description + '%'\" />"
	    ,"AND `description` like #{description}"
	    ,"</if>"
	    ,"<if test=\"creatorName != null and creatorName != '' \">"
	    ,"<bind name='creatorName' value=\"'%' + creatorName + '%'\" />"
	    ,"AND `creatorName` like #{creatorName}"
	    ,"</if>"
	    ,"<if test=\"surerName != null and surerName != '' \">"
	    ,"<bind name='surerName' value=\"'%' + surerName + '%'\" />"
	    ,"AND `surerName` like #{surerName}"
	    ,"</if>"
	    ,"<if test='needPlanType != null'>AND `needPlanType` = #{needPlanType}</if>"
	    ,"<if test='buySellType != null'>AND `buySellType` = #{buySellType}</if>"
		,"<if test='state != null'>AND `state` = #{state}</if>"
		,"<if test='syncTime != null'>AND `syncTime` = #{syncTime}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Need_plan> getNeedPlan(Need_plan needPlan);
	
	@Insert({
		"<script>"
		,"INSERT INTO `need_plan` (`creatorId`,`surerId`,`level`,`startTime`,`endTime`,`needPlanType`,`buySellType`,`state`,`syncTime`"
	    ,"<if test=\"description != null and description != '' \">,`description`</if>"
		,",`deleted`)"
		,"VALUES (#{creatorId},#{surerId},#{level},#{startTime},#{endTime},#{needPlanType},#{buySellType},#{state},#{syncTime}"
		,"<if test=\"description != null and description != '' \">,#{description}</if>"
		,",false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addNeedPlan(Need_plan needPlan);
	
	@Update({
		"<script>"
		,"UPDATE `need_plan`"
		,"<set>"
	    ,"<if test='creatorId != null'>`creatorId` = #{creatorId},</if>"
	    ,"<if test='surerId != null'>`surerId` = #{surerId},</if>"
	    ,"<if test='level != null'>`level` = #{level},</if>"
	    ,"<if test=\"startTime != null and startTime != '' \">`startTime` = #{startTime},</if>"
	    ,"<if test=\"endTime != null and endTime != '' \">`endTime` = #{endTime},</if>"
	    ,"<if test=\"description != null and description != '' \">`description` = #{description},</if>"
	    ,"<if test='needPlanType != null'>`needPlanType` = #{needPlanType},</if>"
	    ,"<if test='buySellType != null'>`buySellType` = #{buySellType},</if>"
	    ,"<if test='state != null'>`state` = #{state},</if>"
		,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"<if test='syncTime != null'>`syncTime` = #{syncTime},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateNeedPlan(Need_plan needPlan); 

	@Select({
		"<script>"
		,"SELECT COUNT(*) FROM `vw_need_plan`"
		, "<where>"
	    ,"<if test='creatorId != null'>AND `creatorId` = #{creatorId}</if>"
	    ,"<if test='surerId != null'>AND `surerId` = #{surerId}</if>"
	    ,"<if test='level != null'>AND `level` = #{level}</if>"
	    ,"<if test=\"startTime != null and startTime != '' \">AND `startTime` = #{startTime}</if>"
	    ,"<if test=\"endTime != null and endTime != '' \">AND `endTime` = #{endTime}</if>"
	    ,"<if test=\"description != null and description != '' \">"
	    ,"<bind name='description' value=\"'%' + description + '%'\" />"
	    ,"AND `description` like #{description}"
	    ,"</if>"
	    ,"<if test=\"creatorName != null and creatorName != '' \">"
	    ,"<bind name='creatorName' value=\"'%' + creatorName + '%'\" />"
	    ,"AND `creatorName` like #{creatorName}"
	    ,"</if>"
	    ,"<if test=\"surerName != null and surerName != '' \">"
	    ,"<bind name='surerName' value=\"'%' + surerName + '%'\" />"
	    ,"AND `surerName` like #{surerName}"
	    ,"</if>"
	    ,"<if test='needPlanType != null'>AND `needPlanType` = #{needPlanType}</if>"
	    ,"<if test='buySellType != null'>AND `buySellType` = #{buySellType}</if>"
	    ,"<if test='state != null'>AND `state` = #{state}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public int getNeedPlanCount(Need_plan needPlan);
}
