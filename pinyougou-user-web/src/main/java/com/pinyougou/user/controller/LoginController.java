package com.pinyougou.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	@RequestMapping("/loginName")
	public Map showName() {
		// 获取当前登录用户名
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(name);
		Map map = new HashMap<>();
		map.put("loginName", name);
		return map;
	}

}
