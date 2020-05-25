package com.web_back.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.User;

@Mapper
public interface IUserMapper {
	@Select("SELECT * FROM `user` WHERE `phoneNum`=#{phoneNum} AND `deleted`=false ORDER BY `id` LIMIT 1")
	public User getUserByPhone(User user);
	
	@Select({
		"<script>"
		,"SELECT * FROM `user`"
		, "<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test=\"name != null and name != '' \">"
	    ,"<bind name='name' value=\"'%' + name + '%'\" />"
	    ,"AND `name` like #{name}"
	    ,"</if>"
	    ,"<if test=\"phoneNum != null and phoneNum != ''\">"
	    ,"AND `phoneNum` = #{phoneNum}"
	    ,"</if>"
	    ,"<if test=\"eMail != null and eMail != '' \">"
	    ,"<bind name='eMail' value=\"'%' + eMail + '%'\" />"
	    ,"AND `eMail` like #{eMail}"
	    ,"</if>"
	    ,"<if test=\"role != null and role != ''\">"
	    ,"AND `role` = #{role}"
	    ,"</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<User> getUser(User user);
	
	@Insert({
		"<script>"
		,"INSERT INTO `user` (`name`,`password`,`phoneNum`,`eMail`,`role`"
	    ,"<if test=\"imgUrl != null and imgUrl != ''  \">,'imgUrl'</if>"
		,",`deleted`)"
		,"VALUES (#{name},#{password},#{phoneNum},#{eMail},#{role}"
		,"<if test=\"imgUrl != null and imgUrl != ''  \">,#{imgUrl}</if>"
		,",false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addUser(User user);
	
	@Update({
		"<script>"
		,"UPDATE `user`"
		,"<set>"
	    ,"<if test=\"name != null and name != '' \">`name` = #{name},</if>"
	    ,"<if test=\"password != null and password != ''  \">`password` = #{password},</if>"
	    ,"<if test=\"role != null and role != ''  \">`role` = #{role},</if>"
	    ,"<if test=\"phoneNum != null and phoneNum != ''  \">`phoneNum` = #{phoneNum},</if>"
	    ,"<if test=\"eMail != null and eMail != ''  \">`eMail` = #{eMail},</if>"
	    ,"<if test=\"imgUrl != null and imgUrl != ''  \">`imgUrl` = #{imgUrl},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"</set>"
		,"<where>`id`=#{id} OR `phoneNum`=#{phoneNum}</where>"
		,"</script>"
	})
	public int updateUser(User user); 
}
