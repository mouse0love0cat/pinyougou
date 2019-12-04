package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangyilong
 * @Date: 2019/12/2 0002
 * @Description:
 */
@Service
public class CartServiceImpl  implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //1 添加商品到购物车
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, long itemId, int num) {
        //1 根据itemId查询一个item对象
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //2 根据查询sellerId 当前购物车中是否包含此商品
        Cart cart = findCartBySellerId(cartList,tbItem.getSellerId());
        //3 判断当前的购物车是否为空
        if (cart !=null){
            //3.1 当购物车不为空时，判断订单列表中是否包含此商品 查询订单列表
            TbOrderItem orderItem = findOrderItemBy(cartList,tbItem.getId());
            //3.2 判断当前的订单列表是否为空
            if (orderItem == null){    //3.2.1 若订单为空 则创建订单
                //第一步  创建订单
                orderItem =  createOrderItem(tbItem, num);
                //第二步  添加订单都订单列表
                cart.getOrderItemList().add(orderItem);
            }else {                             //3.2.2 不为空，则修改订单的数量和金额
                //① 修改订单的数量
                 orderItem.setNum(orderItem.getNum()+num);
                 // ② 修改订单的金额
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //3.2.3 当商品的数量减为0 时 将该商品移出购物车
                if (orderItem.getNum() == 0){
                    //将当前商品从购物车的订单列表中移出
                    cart.getOrderItemList().remove(orderItem);
                }
                //当前的订单列表为空时，则从购物车列表中移出该购物车
                if (cart.getOrderItemList().size() == 0){
                    //将当前的购物车从购物车列表中移出
                    cartList.remove(cart);
                }
            }
        }else {          //3.2 购物车为空  则创建购物车，将商品添加到购物车列表中
            //① 创建购物车
            Cart  newCart = createCart(tbItem,num);
            //② 将购物车加入购物车列表
            cartList.add(newCart);
        }
        return cartList;
    }

    //从redis中获取购物车列表
    @Override
    public List<Cart> findCartListFromRedis(String userName) {
        List<Cart> redisCartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userName);
        return redisCartList;
    }

    //将购物车列表保存到redis中
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    //将cookie中的购物车列表 合并到redis中的购物车列表中
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        //第一步  遍历cookie中的购物车列表
        for (Cart cart : cookieCartList) {
            //遍历购物车中的订单列表
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //将cookie中每一项数据都添加到redis中
                redisCartList = addGoodsToCartList(cookieCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }


    //2 根据sellid判断当前商品是否存在购物车列表中
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        //1 遍历当前的购物车列表
        for (Cart cart : cartList) {
            //2 判断购物的sellerid与当前的商品的sellerid是否相同
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    //3 创建购物车
    private Cart createCart(TbItem tbItem, int num) {
        //3.1 创建一个购物车对象
        Cart cart = new Cart();
        //3.2 给购物车设置相应的属性
        cart.setSellerId(tbItem.getSellerId());
        cart.setSellerName(tbItem.getSeller());
        TbOrderItem orderItem = createOrderItem(tbItem,num);
        //3.3 创建一个集合 用于存放订单
        List<TbOrderItem> orderItemList = new ArrayList<>();
        //3.4 将订单放入集合
        orderItemList.add(orderItem);
        //3.5 将订单列表放入购物车中
        cart.setOrderItemList(orderItemList);
        //3.3 返回购物车对象
        return cart;
    }

    //4 创建订单列表（作用：将tbitem对象包装转换为orderItem对象）
    private TbOrderItem createOrderItem(TbItem tbItem, int num) {

        //1 构造一个TbOrderItem 对象
        TbOrderItem orderItem = new TbOrderItem();
        //2 设置orderitem的一系列属性
        orderItem.setItemId(tbItem.getId());
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setSellerId(tbItem.getSellerId());
        orderItem.setTitle(orderItem.getTitle());
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue() * num));
        //3 返回订单信息
        return orderItem;
    }
    //5 根据itemid查询订单信息
    private TbOrderItem findOrderItemBy(List<Cart> cartList, Long itemId) {
        //5.1 遍历购物车列表
        for (Cart cart : cartList) {
            //5.2 遍历购物车中的订单列表
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //orderItem.getItemId()得到的值和itemId都是包装类型，都是对象，两个对象比价，先将其转换为基本类型，再进行比较
                //5.3 判断当前的订单列表中是否存在要添加的商品
                if (orderItem.getItemId().longValue() == itemId.longValue()){
                    // 若存在，则返回该订单信息
                    return orderItem;
                }
            }
        }
        //不存在 返回null
        return  null;
    }



}
