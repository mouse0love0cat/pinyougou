package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/11/5 0005
 * @Description: 登录验证
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public Map login(){

        //通过输入的用户名获取凭证信息，交由安全框架认证和授权
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //创建一个map对象
        Map map = new HashMap();
        //将信息返回给前台
        map.put("name",name);
        //System.out.println(name);

        return map;
    }


}
