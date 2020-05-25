package com.web_back.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.web_back.entity.Goods;

@Mapper
public interface IGoodsMapper {
	@Select({
		"<script>"
		,"SELECT * FROM `goods`"
		, "<where>"
	    ,"<if test='id != null'>AND `id` = #{id}</if>"
	    ,"<if test=\"name != null and name != '' \">"
	    ,"<bind name='name' value=\"'%' + name + '%'\" />"
	    ,"AND `name` like #{name}"
	    ,"</if>"
		,"<if test='type != null'>AND `type` = #{type}</if>"
		,"<if test='_type != null'>AND `type` != #{_type}</if>"
	    ,"<if test='defaultPrice != null'>AND `defaultPrice` = #{defaultPrice}</if>"
	    ,"<if test='count != null'>AND `count` = #{count}</if>"
	    ,"<if test='high != null'>AND `high` = #{high}</if>"
	    ,"<if test='low != null'>AND `low` = #{low}</if>"
		,"<if test='isWarn != null'>AND `isWarn` = #{isWarn}</if>"
		,"<if test='state != null'>AND `state` = #{state}</if>"
	    ,"<if test=\"description != null and description != '' \">"
	    ,"<bind name='description' value=\"'%' + description + '%'\" />"
	    ,"AND `description` like #{description}"
	    ,"</if>"
	    ,"AND `deleted` = false"
	    ,"</where>"
		,"</script>"
		})
	public List<Goods> getGoods(Goods goods);
	
	@Insert({
		"<script>"
		,"INSERT INTO `goods` (`name`,`type`,`defaultPrice`"
	    ,"<if test='high != null'>,'high'</if>"
	    ,"<if test='low != null'>,'low'</if>"
	    ,"<if test=\"description != null and description != '' \">,`description`</if>"
		,",`count`,`isWarn`,`state`,`deleted`)"
		,"VALUES (#{name},#{type},#{defaultPrice}"
		,"<if test='high != null'>,#{high}</if>"
		,"<if test='low != null'>,#{low}</if>"
		,"<if test=\"description != null and description != '' \">,#{description}</if>"
		,",0,false,0,false)"
		,"</script>"
	})
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public int addGoods(Goods goods);
	
	@Update({
		"<script>"
		,"UPDATE `goods`"
		,"<set>"
		 ,"<if test=\"name != null and name != '' \">`name` = #{name},</if>"
	    ,"<if test='type != null'>`type` = #{type},</if>"
	    ,"<if test='defaultPrice != null'>`defaultPrice` = #{defaultPrice},</if>"
	    ,"<if test='count != null'>`count` = #{count},</if>"
	    ,"<if test='high != null'>`high` = #{high},</if>"
	    ,"<if test='low != null'>`low` = #{low},</if>"
		,"<if test='isWarn != null'>`isWarn` = #{isWarn},</if>"
		,"<if test='state != null'>`state` = #{state},</if>"
	    ,"<if test=\"description != null and description != '' \">`description` = #{description},</if>"
	    ,"<if test='deleted != null'>`deleted` = #{deleted},</if>"
		,"</set>"
		,"<where>`id`=#{id}</where>"
		,"</script>"
	})
	public int updateGoods(Goods goods); 
}
