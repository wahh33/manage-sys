package com.web_back.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import com.web_back.utils.BaseApi;
import com.web_back.utils.ActionResult;
import com.web_back.utils.FileUtils;

@RestController
@RequestMapping("/api/upload")
public class UploadApi extends BaseApi {
    @Value("${web.uploadPath.img}")
	String path;

	@PostMapping("/uploadImg")
	public ActionResult uploadImg(@RequestParam(value = "file") MultipartFile file){
		List<String> typeArr=new ArrayList<String>();
		typeArr.add(".jpg");
		typeArr.add(".png");
		typeArr.add(".jpeg");
		String fileName=FileUtils.uploadFile(file, path, typeArr);
		if(fileName!=null){
			return success("",fileName);
		}else return error("上传出错！");
	}
}