package com.web_back.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.web_back.annotation.PassToken;
import com.web_back.entity.User;
import com.web_back.service.UserService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;
import com.web_back.utils.TokenUtils;

@RestController
@RequestMapping("/api/user")
public class UserApi extends BaseApi {
	@Autowired
	UserService userService;

	@PassToken
	@PostMapping("/loginByPhone")
	public ActionResult login(@RequestBody User user) {
		User userDB = userService.getUserByPhone(user);
		if (userDB == null) {
			return error("登录失败！账号不存在", null);
		} else if (!userDB.getPassword().equals(user.getPassword())) {
			return error("登录失败！密码不正确", null);
		} else {
			JSONObject result = new JSONObject();
			result.put("token", TokenUtils.getToken(userDB));
			result.put("user", userDB);
			return success("登录成功！", result);
		}
	}

	@GetMapping("/getUserInfo")
	public ActionResult login(HttpServletRequest httpServletRequest) {
		return success("", httpServletRequest.getAttribute("currentUser"));
	}

	@PostMapping("/getUserPage")
	public ActionResult getUserPage(@RequestBody PageModel<User> pageModel, HttpServletRequest httpServletRequest) {
		User userInfo = (User) httpServletRequest.getAttribute("currentUser");
		return success("", userService.getUserPage(pageModel,(userInfo.getRole()==null || !userInfo.getRole().equals("admin"))));
	}

	@PostMapping("/saveUser")
	public ActionResult saveUser(@RequestBody User user) {
		return success("", userService.saveUser(user));
	}

	@PostMapping("/deleteUser")
	public ActionResult deleteUser(@RequestBody User user) {
		return success("", userService.deleteUser(user.getId()));
	}

	@GetMapping("/getAllUser")
	public ActionResult getAllUser(HttpServletRequest httpServletRequest) {
		User userInfo = (User) httpServletRequest.getAttribute("currentUser");
		return success("", userService.getUser(null,(userInfo.getRole()==null || !userInfo.getRole().equals("admin"))));
	}

	@PostMapping("/changePassword")
	public ActionResult changePassword(@RequestBody User user, HttpServletRequest httpServletRequest) {
		User userInfo = (User) httpServletRequest.getAttribute("currentUser");
		user.setId(userInfo.getId());
		userService.saveUser(user);
		userInfo.setPassword(user.getPassword());
		JSONObject result = new JSONObject();
		result.put("token", TokenUtils.getToken(userInfo));
		result.put("user", userInfo);
		return success("", result);
	}

	@GetMapping("/getWorkInfo")
	public ActionResult getWorkInfo(HttpServletRequest httpServletRequest){
		User userInfo=(User)httpServletRequest.getAttribute("currentUser");
		return success("",userService.getWorkInfo(userInfo.getId(), userInfo.getName(), userInfo.getRole()));
	}
}
