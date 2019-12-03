package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: wangyilong
 * @Date: 2019/12/3 0003
 * @Description:
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderMapper orderMapper;

    //添加订单
    //思路：从redis中获取购物车信息，构造订单信息，添加订单明细信息，并设置总金额，最后清空redis中的数据
    @Override
    public void add(TbOrder tbOrder) {
        //1 从redis中获取购物车列表 key为用户名
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(tbOrder.getUserId());
        //1.1 遍历购物车列表  构造订单项
        for (Cart cart : cartList) {
            //2 构造一个TbOrder对象
            TbOrder order = new TbOrder();
            //3 设置订单的一系列属性
            long orderId = idWorker.nextId();
            order.setOrderId(orderId);
            order.setUserId(tbOrder.getUserId());
            order.setPaymentType(tbOrder.getPaymentType());
            order.setStatus(tbOrder.getStatus());
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setReceiverAreaName(tbOrder.getReceiverAreaName());
            order.setReceiver(tbOrder.getReceiver());
            order.setReceiverMobile(tbOrder.getReceiverMobile());
            order.setSourceType(tbOrder.getSourceType());
            order.setSellerId(tbOrder.getSellerId());
            //4 设置订单详情列表（添加数据 到orderitem列表）
            //定义订单明细中的总金额
            double sum = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //设置id
                orderItem.setId(idWorker.nextId());
                orderItem.setSellerId(tbOrder.getSellerId());
                orderItem.setOrderId(orderId);
                //设置小计金额
                sum += orderItem.getTotalFee().doubleValue();
                //添加订单选项
                orderItemMapper.insert(orderItem);
            }
            //计算总金额
            order.setPayment(new BigDecimal(sum));
            //将当前订单信息添加到数据库
            orderMapper.insert(order);
        }
    }
}
