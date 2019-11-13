package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangyilong
 * @Date: 2019/11/7 0007
 * @Description: 自定义的认证类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    //使用setter的方法将sellerService服务注入到自定义的认证方法中
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
    /**
    * @Description 自定义的认证方法  调用dubbo服务
    * @Author  wangyilong
    * @Date   2019/11/7 0007 下午 4:46
    * @Param username
    * @Return
    * @Exception
    *
    */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("经过认证。。。。。。");
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        //对角色进行授权
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        //根据用户id 得到用户对象
        TbSeller seller = sellerService.findOne(username);
        System.out.println(seller.getStatus());
        //做非空判断
        if (seller !=null){
            //获取商家状态信息
            //如果审核通过，则放行 否则其他情况都不放行
            if (seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),grantedAuths);
            }else{
                return null;
            }
        }else {
            return null;
        }

    }
}
