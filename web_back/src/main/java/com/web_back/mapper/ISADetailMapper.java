package com.web_back.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Store_apply_detail;

@Mapper
public interface ISADetailMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `vw_store_apply_detail`"
		, "<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test='goodsId != null'>AND `goodsId` = #{goodsId}</if>"
	    ,"<if test=\"goodsName != null and goodsName != '' \">"
	    ,"<bind name='goodsName' value=\"'%' + goodsName + '%'\" />"
	    ,"AND `goodsName` like #{goodsName}"
	    ,"</if>"
	    ,"<if test='storeApplyId != null'>AND `storeApplyId` = #{storeApplyId}</if>"
	    ,"<if test='preInOutCount != null'>AND `preInOutCount` = #{preInOutCount}</if>"
	    ,"<if test='reaInOutCount != null'>AND `reaInOutCount` = #{reaInOutCount}</if>"
	    ,"<if test='state != null'>AND `state` = #{state}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Store_apply_detail> getSADetail(Store_apply_detail saDetail);
	
	@Insert({
		"<script>"
		,"INSERT INTO `store_apply_detail` (`goodsId`,`storeApplyId`,`preInOutCount`"
	    ,"<if test='reaInOutCount != null'>,`reaInOutCount`</if>"
		,",`state`,`deleted`)"
		,"VALUES (#{goodsId},#{storeApplyId},#{preInOutCount}"
		,"<if test='reaInOutCount != null'>,#{reaInOutCount}</if>"
	    ,",0,false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addSADetail(Store_apply_detail saDetail);
	
	@Update({
		"<script>"
		,"UPDATE `store_apply_detail`"
		,"<set>"
	    ,"<if test='goodsId != null'>`goodsId` = #{goodsId},</if>"
	    ,"<if test='storeApplyId != null'>`storeApplyId` = #{storeApplyId},</if>"
	    ,"<if test='preInOutCount != null'>`preInOutCount` = #{preInOutCount},</if>"
	    ,"<if test='reaInOutCount != null'>`reaInOutCount` = #{reaInOutCount},</if>"
	    ,"<if test='state != null'>`state` = #{state},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateSADetail(Store_apply_detail saDetail); 
	
	@Insert({
		"<script>"
		,"INSERT INTO `store_apply_detail` (`goodsId`,`storeApplyId`,`preInOutCount`,`reaInOutCount`,`state`,`deleted`)"
		,"VALUES"
		,"<foreach item='item' index='index' collection='saDetailList' open='' close='' separator=','>"
		,"(#{item.goodsId},#{storeApplyId},#{item.preInOutCount},0,0,false)"
		,"</foreach>"
		,"</script>"
	})
	public int addSADetailList(Map<String, Object> params);
	
	@Select({
		"<script>"
		,"SELECT * FROM `vw_store_apply_detail`"
		, "<where>"
		,"AND id IN"
		,"<foreach item='item' index='index' collection='saDetailIds' open='(' close=')' separator=','>"
		,"#{item}"
		,"</foreach>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Store_apply_detail> getSADetailList(Map<String, Object> params);

	@Select({
		"<script>"
		,"SELECT SUM(IF(`inOutType`=0,`reaInOutCount`,-`reaInOutCount`)) `totalCount`,SUM(IF(`inOutType`=0,`reaInOutCount`,0)) `inCount`,SUM(IF(`inOutType`=1,-`reaInOutCount`,0)) `outCount`,DATE_FORMAT(`createTime`,#{dateFormat}) `createTime`"
		,"FROM (SELECT * FROM `vw_store_apply_detail`"
		,"<where>"
		,"<if test=\"goodsId != null and goodsId !='' \"> AND `goodsId` = #{goodsId} </if>"
		,"<if test=\"startTime != null and startTime !='' \"> AND `createTime` &gt;= #{startTime} </if>"
		,"<if test=\"endTime != null and endTime !='' \"> AND `createTime` &lt;= #{endTime} </if>"
		,"AND `deleted` = false"
		,"AND `state` = 1"
		,"</where>"
		,") `f_vw_store_apply_detail`"
		,"GROUP BY DATE_FORMAT(`createTime`,#{dateFormat})"
		,"</script>"
	})
	public List<Store_apply_detail> getSADetailSum(Map<String,Object> params);
}
