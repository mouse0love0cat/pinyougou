package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wangyilong
 * @Date: 2019/11/28 0028
 * @Description: 登录用户的控制类
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Reference
    private UserService userService;


    //根据电话号码获取验证码
    @RequestMapping("getValidCode")
    public Result getValidCode(String phone) {
        try {
            //1 j检验手机号是否合法
            if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone)) {
                //2 生成验证码
                userService.getValidCode(phone);
                //3 发送验证码
                return new Result(true, "雅验证码发送成功!");

            }
            return new Result(false, "手机号不合法！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "验证码发送失败");
        }
    }


    //用户注册
    @RequestMapping("add")
    public Result addUser(@RequestBody TbUser user, String validCode) {
        try {
            //1 验证验证码是否相等
            boolean b = userService.checkValidCode(user.getPhone(), validCode);
            System.out.println(b);
            if (b == false) {
                return new Result(false, "验证码输入有误");
            }
            userService.add(user);
            return new Result(true, "注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败!");
        }
    }

}
