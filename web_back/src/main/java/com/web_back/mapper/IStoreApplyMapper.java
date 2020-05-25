package com.web_back.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Store_apply;

@Mapper
public interface IStoreApplyMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `vw_store_apply`"
		, "<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test='userId != null'>AND `userId` = #{userId}</if>"
	    ,"<if test='entityId != null'>AND `entityId` = #{entityId}</if>"
	    ,"<if test=\"creatorName != null and creatorName != '' \">"
	    ,"<bind name='creatorName' value=\"'%' + creatorName + '%'\" />"
	    ,"AND `creatorName` like #{creatorName}"
	    ,"</if>"
	    ,"<if test=\"createTime != null and createTime != '' \">AND `createTime` = #{createTime}</if>"
	    ,"<if test='needPlanOrderType != null'>AND `needPlanOrderType` = #{needPlanOrderType}</if>"
	    ,"<if test='inOutType != null'>AND `inOutType` = #{inOutType}</if>"
		,"<if test='state != null'>AND `state` = #{state}</if>"
		,"<if test='syncTime != null'>AND `syncTime` = #{syncTime}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Store_apply> getStoreApply(Store_apply storeApply);
	
	@Insert({
		"<script>"
		,"INSERT INTO `store_apply` (`userId`,`needPlanOrderType`,`inOutType`,`createTime`,`state`,`syncTime`"
	    ,"<if test=\"proofUrl != null and proofUrl != '' \">,`proofUrl`</if>"
	    ,"<if test='entityId != null'>,`entityId`</if>"
		,",`deleted`)"
		,"VALUES (#{userId},#{needPlanOrderType},#{inOutType},#{createTime},#{state},#{syncTime}"
		,"<if test=\"proofUrl != null and proofUrl != '' \">,#{proofUrl}</if>"
		,"<if test='entityId != null'>,#{entityId}</if>"
		,",false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addStoreApply(Store_apply storeApply);
	
	@Update({
		"<script>"
		,"UPDATE `store_apply`"
		,"<set>"
	    ,"<if test='userId != null'>`userId` = #{userId},</if>"
	    ,"<if test='entityId != null'>`entityId` = #{entityId},</if>"
	    ,"<if test='needPlanOrderType != null'>`needPlanOrderType` = #{needPlanOrderType},</if>"
	    ,"<if test='inOutType != null'>`inOutType` = #{inOutType},</if>"
	    ,"<if test=\"createTime != null and createTime != '' \">`createTime` = #{createTime},</if>"
	    ,"<if test=\"proofUrl != null and proofUrl != '' \">`proofUrl` = #{proofUrl},</if>"
	    ,"<if test='state != null'>`state` = #{state},</if>"
		,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"<if test='syncTime != null'>`syncTime` = #{syncTime},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateStoreApply(Store_apply storeApply); 

	@Select({
		"<script>"
		,"SELECT COUNT(*) FROM `vw_store_apply`"
		, "<where>"
	    ,"<if test='userId != null'>AND `userId` = #{userId}</if>"
	    ,"<if test='entityId != null'>AND `entityId` = #{entityId}</if>"
	    ,"<if test=\"creatorName != null and creatorName != '' \">"
	    ,"<bind name='creatorName' value=\"'%' + creatorName + '%'\" />"
	    ,"AND `creatorName` like #{creatorName}"
	    ,"</if>"
	    ,"<if test=\"createTime != null and createTime != '' \">AND `createTime` = #{createTime}</if>"
	    ,"<if test='needPlanOrderType != null'>AND `needPlanOrderType` = #{needPlanOrderType}</if>"
	    ,"<if test='inOutType != null'>AND `inOutType` = #{inOutType}</if>"
		,"<if test='state != null'>AND `state` = #{state}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public int getStoreApplyCount(Store_apply storeApply);
}
