package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author: wangyilong
 * @Date: 2019/12/2 0002
 * @Description:
 */
@RestController
@RequestMapping("cart")
public class CartController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    @RequestMapping("findCartList")
    public List<Cart> findCartList(){

        //0 获取用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //1 从cook中获取数据
        String cookieCartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //2 判断当前的cartlist是否为空
        if (cookieCartList == null || cookieCartList.equals("")){
            //2.1 若为空，则将cartlist初始化为一个空的数组
            cookieCartList = "[]";
        }
        //3 若不为空 则将其转换为一个list集合
        List<Cart> cartList = JSON.parseArray(cookieCartList, Cart.class);
        //4 进行判断是否是匿名用户
        if (userName.equals("anonymousUser")){                 //用户未登录
            return cartList;
        }else {                                                                         //用户已登录
            //第一步  从redis中购物车列表数据
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(userName);
            //第二步 判断本地是否存在购物车信息（即cookie中是否有购物车列表数据）
            if (cartList.size() >0){                    //表示有数据
                //第三步  合并购物车列表
                cartListFromRedis = cartService.mergeCartList(cartList,cartListFromRedis);
                //第四步 情况本地的购物车列表
                CookieUtil.deleteCookie(request,response,"cartList");
                //第五步  将合并后的数据保存到redis中
                cartService.saveCartListToRedis(userName,cartListFromRedis);
            }
            //返回集合
            return cartListFromRedis;
        }
    }


    //2 添加商品到购物车列表
    //参数1：sku商品id  参数二：要购买的商品数量
    @RequestMapping("addGoodsToCartList")
    public Result addGoodsToCartList(long itemId,  int num){
        try {
            //设置跨域请求的请求头（使用CORS解决跨域请求）
            response.setHeader("Access-Control-Allow-Origin","http://localhost:9104");
            response.setHeader("Access-Control-Allow-Credentials","true");
            //0 获取当前登录的用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(userName);
            //1 查询购物车列表
            List<Cart> cartList = findCartList();
            //2 更新该列表
            cartList = cartService.addGoodsToCartList(cartList,itemId,num);
            //3 对当前用户进行判断  若为匿名用户
            // 将更新后的购物车列表放到cookie中
            if (userName.equals("anonymousUser")){                      //匿名用户
                //将购物车列表放入本地的cookie中
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
                System.out.println("存入cookie中！");
            }else {                                                                              //已登录的用户
                //将购物车列表放入redis中
                cartService.saveCartListToRedis(userName,cartList);
                System.out.println("存入redis缓存中");
            }
            return new Result(true,"添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败!");
        }
    }


}
