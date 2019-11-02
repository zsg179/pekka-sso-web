package com.pekka.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	@RequestMapping("/page/login")
	public String showLogin() {
		return "login";
	}

	@RequestMapping("/page/register")
	public String showRegister() {
		return "register";
	}
}
