package com.pekka.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pekka.common.pojo.PekkaResult;
import com.pekka.common.util.CookieUtils;
import com.pekka.common.util.JsonUtils;
import com.pekka.pojo.TbUser;
import com.pekka.sso.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	@Value("${TOKEN_EXPIRE}")
	private Integer TOKEN_EXPIRE;

	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public PekkaResult checkData(@PathVariable String param, @PathVariable Integer type) {
		PekkaResult result = userService.checkData(param, type);
		return result;
	}

	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	@ResponseBody
	public PekkaResult register(TbUser user) {
		PekkaResult result = userService.register(user);
		return result;
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	@ResponseBody
	public PekkaResult login(String username, String password, HttpServletRequest request,
			HttpServletResponse response) {
		PekkaResult result = userService.login(username, password);
		// 登陆成功把token写入cookie
		if (result.getStatus() == 200) {
			CookieUtils.setCookie(request, response, TOKEN_KEY, result.getData().toString(), TOKEN_EXPIRE);
		}
		return result;
	}

	@RequestMapping(value = "/user/token/{token}", method = RequestMethod.GET,
			// 指定返回响应数据的content-type
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token, String callback) {
		PekkaResult result = userService.getUserByToken(token);
		// 判断是否为jsonp请求
		if (StringUtils.isNotBlank(callback)) {
			return callback + "(" + JsonUtils.objectToJson(result) + ");";
		}
		return JsonUtils.objectToJson(result);
	}

	@RequestMapping(value = "/user/logout/{token}")
	public String logOut(@PathVariable String token) {
		userService.logOut(token);
		return "redirect:http://localhost:8088/page/login";
	}

}
