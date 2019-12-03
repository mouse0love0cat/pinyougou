package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    //1 添加商品到购物车
    List<Cart> addGoodsToCartList(List<Cart> cartList, long itemId, int num);
    //2 从redis中获取购物车列表
    public List<Cart> findCartListFromRedis(String userName);
    //3 将购物车列表 保存到redis中
    public void saveCartListToRedis(String username,List<Cart> cartList);
    //4 合并cookie和redis购物车列表到redis中
    public List<Cart> mergeCartList(List<Cart> cookieCartList,List<Cart> redisCartList);
}
