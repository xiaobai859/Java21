package com.pinyougou.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

  @RequestMapping("/name")
  public Map showName() {
    // 使用spring-security获取上下文对象，从而获取认证的用户名
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    Map map = new HashMap<>();
    map.put("loginName", name);
    return map;
  }

}
