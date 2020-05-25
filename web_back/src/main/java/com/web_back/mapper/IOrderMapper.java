package com.web_back.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Order;

@Mapper
public interface IOrderMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `vw_order`"
		, "<where>"
		,"<if test='id != null'>AND `id` = #{id}</if>"
		,"<if test='userId != null'>AND `userId` = #{userId}</if>"
	    ,"<if test=\"creatorName != null and creatorName != '' \">"
	    ,"<bind name='creatorName' value=\"'%' + creatorName + '%'\" />"
	    ,"AND `creatorName` like #{creatorName}"
	    ,"</if>"
	    ,"<if test=\"clientName != null and clientName != '' \">"
	    ,"<bind name='clientName' value=\"'%' + clientName + '%'\" />"
	    ,"AND `clientName` like #{clientName}"
	    ,"</if>"
	    ,"<if test=\"createTime != null and createTime != '' \">AND `createTime` = #{createTime}</if>"
	    ,"<if test='type != null'>AND `type` = #{type}</if>"
		,"<if test='state != null'>AND `state` = #{state}</if>"
		,"<if test='discount != null'>AND `discount` = #{discount}</if>"
		,"<if test='isReturn != null'>AND `isReturn` = #{isReturn}</if>"
		,"<if test='syncTime != null'>AND `syncTime` = #{syncTime}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Order> getOrder(Order order);
	
	@Insert({
		"<script>"
		,"INSERT INTO `order` (`userId`,`clientId`,`type`,`state`,`createTime`,`syncTime`,`deleted`,`finishPrice`,`totalPrice`,`isReturn`)"
		,"VALUES (#{userId},#{clientId},#{type},#{state},#{createTime},#{syncTime},false,0,0,#{isReturn})"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addOrder(Order order);
	
	@Update({
		"<script>"
		,"UPDATE `order`"
		,"<set>"
	    ,"<if test='userId != null'>`userId` = #{userId},</if>"
	    ,"<if test='clientId != null'>`clientId` = #{clientId},</if>"
	    ,"<if test=\"createTime != null and createTime != '' \">`createTime` = #{createTime},</if>"
	    ,"<if test='state != null'>`state` = #{state},</if>"
	    ,"<if test='type != null'>`type` = #{type},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
	    ,"<if test='totalPrice != null'>`totalPrice` = #{totalPrice},</if>"
		,"<if test='finishPrice != null'>`finishPrice` = #{finishPrice},</if>"
		,"<if test='isReturn != null'>`isReturn` = #{isReturn},</if>"
		,"<if test='syncTime != null'>`syncTime` = #{syncTime},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateOrder(Order order); 

	@Select({
		"<script>"
		,"SELECT COUNT(*) FROM `vw_order`"
		, "<where>"
		,"<if test='userId != null'>AND `userId` = #{userId}</if>"
	    ,"<if test=\"creatorName != null and creatorName != '' \">"
	    ,"<bind name='creatorName' value=\"'%' + creatorName + '%'\" />"
	    ,"AND `creatorName` like #{creatorName}"
	    ,"</if>"
	    ,"<if test=\"clientName != null and clientName != '' \">"
	    ,"<bind name='clientName' value=\"'%' + clientName + '%'\" />"
	    ,"AND `clientName` like #{clientName}"
	    ,"</if>"
	    ,"<if test=\"createTime != null and createTime != '' \">AND `createTime` = #{createTime}</if>"
	    ,"<if test='type != null'>AND `type` = #{type}</if>"
		,"<if test='state != null'>AND `state` = #{state}</if>"
		,"<if test='discount != null'>AND `discount` = #{discount}</if>"
		,"<if test='isReturn != null'>AND `isReturn` = #{isReturn}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public int getOrderCount(Order order);
}
