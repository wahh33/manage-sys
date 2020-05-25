package com.web_back.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Pay_record;

@Mapper
public interface IPayRecordMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `vw_pay_record`"
		,"<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test='userId != null'>AND `userId` = #{userId}</if>"
	    ,"<if test='orderId != null'>AND `orderId` = #{orderId}</if>"
	    ,"<if test=\"creatorName != null and creatorName != '' \">"
	    ,"<bind name='creatorName' value=\"'%' + creatorName + '%'\" />"
	    ,"AND `creatorName` like #{creatorName}"
	    ,"</if>"
		,"<if test='totalPrice != null'>AND `totalPrice` = #{totalPrice}</if>"
		,"<if test='isReturn != null'>AND `isReturn` = #{isReturn}</if>"
		,"<if test='createTime != null'>AND `createTime` = #{createTime}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Pay_record> getPayRecord(Pay_record payRecord);
	
	@Insert({
		"<script>"
		,"INSERT INTO `pay_record` (`userId`,`orderId`,`totalPrice`,`createTime`"
		,"<if test=\"proofUrl != null and proofUrl != '' \">,`proofUrl`</if>"
		,",`deleted`)"
		,"VALUES (#{userId},#{orderId},#{totalPrice},#{createTime}"
		,"<if test=\"proofUrl != null and proofUrl != '' \">,#{proofUrl}</if>"
		,",false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addPayRecord(Pay_record payRecord);
	
	@Update({
		"<script>"
		,"UPDATE `pay_record`"
		,"<set>"
	    ,"<if test='userId != null'>`userId` = #{userId},</if>"
	    ,"<if test='orderId != null'>`orderId` = #{orderId},</if>"
	    ,"<if test=\"proofUrl != null and proofUrl != '' \">`proofUrl` = #{proofUrl},</if>"
		,"<if test='totalPrice != null'>`totalPrice` = #{totalPrice},</if>"
		,"<if test='createTime != null'>`createTime` = #{createTime},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updatePayRecord(Pay_record payRecord); 

	@Select({
		"<script>"
		,"SELECT SUM(IF(((`orderType`+IF(`isReturn`,1,0))%2) = 0,0-`totalPrice`,`totalPrice`)) `totalPrice`,DATE_FORMAT(`createTime`,#{dateFormat}) `createTime`"
		,"FROM (SELECT * FROM `vw_pay_record`"
		,"<where>"
		,"<if test=\"clientId != null and clientId !='' \"> AND `clientId` = #{clientId} </if>"
		,"<if test=\"startTime != null and startTime !='' \"> AND `createTime` &gt;= #{startTime} </if>"
		,"<if test=\"endTime != null and endTime !='' \"> AND `createTime` &lt;= #{endTime} </if>"
		,"AND `deleted` = false"
		,"</where>"
		,") `f_vw_pay_record` GROUP BY DATE_FORMAT(`createTime`,#{dateFormat})"
		,"</script>"
	})
	public List<Pay_record> getPayRecordSum(Map<String, Object> params);
}
