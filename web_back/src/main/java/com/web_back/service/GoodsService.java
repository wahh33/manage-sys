package com.web_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.Goods;
import com.web_back.mapper.IGoodsMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("GoodsService")
public class GoodsService {
	@Autowired
	private IGoodsMapper goodsMapper;
	
	public List<Goods> getGoods(Goods goods){
		return goodsMapper.getGoods(goods);
	}
	
	public PageResult getGoodsPage(PageModel<Goods> goodsPage) {
		Page<Goods> pageInfo=PageHelper.startPage(goodsPage.getStart(),goodsPage.getLength(),goodsPage.getSortColumn()+" "+goodsPage.getSortType());
		return new PageResult(getGoods(goodsPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	}
	
	public Integer saveGoods(Goods goods) {
		Integer goodsId=goods.getId();
		if(goodsId!=null) {
			if(goods.getIsWarn()!=null || goods.getCount()!=null || goods.getLow()!=null || goods.getHigh()!=null){
				Goods goodsParams=new Goods();
				goodsParams.setId(goodsId);
				Goods goodsInfo=getGoods(goodsParams).get(0);
				Boolean isWaran=goods.getIsWarn()!=null?goods.getIsWarn():goodsInfo.getIsWarn();
				if(isWaran.booleanValue()){
					Integer count=goods.getCount()!=null?goods.getCount():goodsInfo.getCount();
					Integer low=goods.getLow()!=null?goods.getLow():goodsInfo.getLow();
					Integer high=goods.getHigh()!=null?goods.getHigh():goodsInfo.getHigh();
					if(low!=null && low.intValue()>count.intValue()){
						goods.setState(1);
					} else if(high!=null && high.intValue()<count.intValue()){
						goods.setState(2);
					} else{
						goods.setState(0);
					}
				} else{
					goods.setState(0);
				}
			}
			return goodsMapper.updateGoods(goods)>0?goodsId:null;
		}else {
			return goodsMapper.addGoods(goods)>0?goods.getId():null;
		}
	}
	
	public boolean deleteGoods(int id) {
		Goods goodsParams=new Goods();
		goodsParams.setId(id);
		goodsParams.setDeleted(true);
		return goodsMapper.updateGoods(goodsParams)>0?true:false;
	}
}
