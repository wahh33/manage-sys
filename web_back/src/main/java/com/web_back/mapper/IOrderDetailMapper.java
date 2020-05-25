package com.web_back.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Order_detail;

@Mapper
public interface IOrderDetailMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `vw_order_detail`"
		, "<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test='goodsId != null'>AND `goodsId` = #{goodsId}</if>"
	    ,"<if test=\"goodsName != null and goodsName != '' \">"
	    ,"<bind name='goodsName' value=\"'%' + goodsName + '%'\" />"
	    ,"AND `goodsName` like #{goodsName}"
	    ,"</if>"
	    ,"<if test='orderId != null'>AND `orderId` = #{orderId}</if>"
	    ,"<if test='count != null'>AND `count` = #{count}</if>"
	    ,"<if test='finishCount != null'>AND `finishCount` = #{finishCount}</if>"
	    ,"<if test='state != null'>AND `state` = #{state}</if>"
	    ,"<if test='price != null'>AND `price` = #{price}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Order_detail> getOrderDetail(Order_detail orderDetail);
	
	@Insert({
		"<script>"
		,"INSERT INTO `order_detail` (`orderId`,`goodsId`,`count`,`npDetailIds`"
	    ,"<if test='price != null'>,`price`</if>"
		,",`finishCount`,`state`,`deleted`)"
		,"VALUES (#{orderId},#{goodsId},#{count},#{npDetailIds}"
		,"<if test='price != null'>,#{price}</if>"
	    ,",0,0,false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addOrderDetail(Order_detail orderDetail);
	
	@Update({
		"<script>"
		,"UPDATE `order_detail`"
		,"<set>"
		,"<if test='orderId != null'>`orderId` = #{orderId},</if>"
	    ,"<if test='goodsId != null'>`goodsId` = #{goodsId},</if>"
	    ,"<if test='count != null'>`count` = #{count},</if>"
	    ,"<if test='npDetailIds != null'>`npDetailIds` = #{npDetailIds},</if>"
	    ,"<if test='price != null'>`price` = #{price},</if>"
	    ,"<if test='finishCount != null'>`finishCount` = #{finishCount},</if>"
	    ,"<if test='state != null'>`state` = #{state},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateOrderDetail(Order_detail orderDetail); 

	@Select({
		"<script>"
		,"SELECT SUM(IF(`isReturn`,0-`finishCount`,`finishCount`)) `finishCount`,`goodsName`"
		,"FROM (SELECT * FROM `vw_order_detail` WHERE `deleted`=0 AND `orderType`=#{orderType}) `f_vw_order_detail`"
		,"GROUP BY `goodsId`"
		,"ORDER BY `finishCount` DESC"
		,"</script>"
	})
	public List<Order_detail> getOrderDetailSum(@Param("orderType") Integer orderType);
}
