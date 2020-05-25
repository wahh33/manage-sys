package com.web_back.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Client;

@Mapper
public interface IClientMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `client`"
		,"<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test=\"name != null and name != '' \">"
	    ,"<bind name='name' value=\"'%' + name + '%'\" />"
	    ,"AND `name` like #{name}"
	    ,"</if>"
	    ,"<if test=\"phoneNum != null and phoneNum != ''\">"
	    ,"<bind name='phoneNum' value=\"'%' + phoneNum + '%'\" />"
	    ,"AND `phoneNum` like #{phoneNum}"
	    ,"</if>"
	    ,"<if test=\"eMail != null and eMail != '' \">"
	    ,"<bind name='eMail' value=\"'%' + eMail + '%'\" />"
	    ,"AND `eMail` like #{eMail}"
	    ,"</if>"
	    ,"<if test=\"address != null and address != '' \">"
	    ,"<bind name='address' value=\"'%' + address + '%'\" />"
	    ,"AND `address` like #{address}"
	    ,"</if>"
	    ,"<if test=\"description != null and description != '' \">"
	    ,"<bind name='description' value=\"'%' + description + '%'\" />"
	    ,"AND `description` like #{description}"
	    ,"</if>"
	    ,"<if test='discount != null'>AND `discount` = #{discount}</if>"
	    ,"<if test='type != null'>AND `type` = #{type}</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Client> getClient(Client client);
	
	@Insert({
		"<script>"
		,"INSERT INTO `client` (`name`,`phoneNum`,`eMail`,`type`"
		,"<if test=\"address != null and address != '' \">,`address`</if>"
		,"<if test=\"imgUrl != null and imgUrl != '' \">,`imgUrl`</if>"
		,"<if test=\"description != null and description != '' \">,`description`</if>"
	    ,"<if test='discount != null'>,`discount`</if>"
		,",`deleted`)"
		,"VALUES (#{name},#{phoneNum},#{eMail},#{type}"
		,"<if test=\"address != null and address != '' \">,#{address}</if>"
		,"<if test=\"imgUrl != null and imgUrl != '' \">,#{imgUrl}</if>"
		,"<if test=\"description != null and description != '' \">,#{description}</if>"
		,"<if test='discount != null'>,#{discount}</if>"
		,",false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addClient(Client client);
	
	@Update({
		"<script>"
		,"UPDATE `client`"
		,"<set>"
		,"<if test=\"name != null and name != '' \">`name` = #{name},</if>"
	    ,"<if test=\"phoneNum != null and phoneNum != '' \">`phoneNum` = #{phoneNum},</if>"
	    ,"<if test=\"eMail != null and eMail != '' \">`eMail` = #{eMail},</if>"
	    ,"<if test=\"address != null and address != '' \">`address` = #{address},</if>"
	    ,"<if test=\"imgUrl != null and imgUrl != '' \">`imgUrl` = #{imgUrl},</if>"
	    ,"<if test=\"description != null and description != '' \">`description` = #{description},</if>"
	    ,"<if test='discount != null'>`discount` = #{discount},</if>"
	    ,"<if test='type != null'>`type` = #{type},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateClient(Client client); 

	@Select({
		"<script>"
		,"SELECT * FROM `vw_client_order_count`"
		,"<where>"
		,"<if test='clientId != null'>AND `clientId` = #{clientId}</if>"
		,"<if test='clientType != null'>AND `clientType` = #{clientType}</if>"
		,"</where>"
		,"</script>"
	})
	public List<Client> getClientInfo(Client client);
}
