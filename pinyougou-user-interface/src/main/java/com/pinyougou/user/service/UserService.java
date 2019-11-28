package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

public interface UserService {

        //1.获取手机验证码
        void getValidCode(String phone);
        //2.比较验证是否正确
        boolean checkValidCode(String validCode,String phone);
        //3.添加用户
        void add(TbUser user);

}
