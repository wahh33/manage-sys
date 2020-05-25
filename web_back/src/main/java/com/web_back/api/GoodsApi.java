package com.web_back.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_back.entity.Goods;
import com.web_back.service.GoodsService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

@RestController
@RequestMapping("/api/goods")
public class GoodsApi extends BaseApi {
	@Autowired
	GoodsService goodsService;

	@PostMapping("/getGoodsPage")
	public ActionResult getGoodsPage(@RequestBody PageModel<Goods> pageModel){
		return success("",goodsService.getGoodsPage(pageModel));
	}

	@PostMapping("/saveGoods")
	public ActionResult saveGoods(@RequestBody Goods goods){
		return success("",goodsService.saveGoods(goods));
	}

	@PostMapping("/deleteGoods")
	public ActionResult deleteGoods(@RequestBody Goods goods){
		return success("",goodsService.deleteGoods(goods.getId()));
	}
	
	@GetMapping("/getAllGoods")
	public ActionResult getAllGoods(){
		return success("",goodsService.getGoods(null));
	}
}
