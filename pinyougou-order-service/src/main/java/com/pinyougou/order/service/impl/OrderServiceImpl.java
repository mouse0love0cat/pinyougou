package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    @Autowired
    private TbPayLogMapper payLogMapper;

    //添加订单
    //思路：从redis中获取购物车信息，构造订单信息，添加订单明细信息，并设置总金额，最后清空redis中的数据
    @Override
    public void add(TbOrder tbOrder) {
        //1 从redis中获取购物车列表 key为用户名
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(tbOrder.getUserId());
        List<String> orderIds = new ArrayList<>();
        //定义总金额
        double total_fee = 0;
        //1.1 遍历购物车列表  构造订单项
        for (Cart cart : cartList) {
            //2 构造一个TbOrder对象
            TbOrder order = new TbOrder();
            //3 设置订单的一系列属性
            long orderId = idWorker.nextId();
            //3.1 将订单id放入一个集合中
            orderIds.add(orderId+"");
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
            //4.1 定义订单明细中的总金额
            double sum = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //4.2 设置id
                orderItem.setId(idWorker.nextId());
                orderItem.setSellerId(tbOrder.getSellerId());
                orderItem.setOrderId(orderId);
                //4.2 设置小计金额
                sum += orderItem.getTotalFee().doubleValue();
                //4.4 添加订单选项
                orderItemMapper.insert(orderItem);
            }
            //5 计算总金额
            total_fee+=sum;
            order.setPayment(new BigDecimal(sum));
            //6 将当前订单信息添加到数据库
            orderMapper.insert(order);
        }
        //7 当订单生成后，生成支付日志信息，将支付日志信息存入数据库，同时同步到缓存redis中
        //7.1 证明是微信支付
        if (tbOrder.getPaymentType().equals("1")){
            //7.2 创建一个payLog对象
            TbPayLog payLog = new TbPayLog();
            //7.3 生成支付订单号
            String outTradeNo = idWorker.nextId() + "";
            //7.4 设置一些列的属性
            payLog.setOutTradeNo(outTradeNo);
            payLog.setCreateTime(new Date());
            payLog.setOrderList(orderIds.toString().replace("[","").replace("]","").replace(" ",""));
            payLog.setPayType("1");
            payLog.setTradeState("0");   //0:表示未支付  1 ： 表示已支付
            payLog.setTotalFee((long) total_fee * 100);   //以 分作为单位
            payLog.setUserId(tbOrder.getUserId());
            //7.5 将支付日志保存到数据库
            payLogMapper.insert(payLog);
            //7.6 将支付日志存入redis缓存中
            redisTemplate.boundHashOps("payLog").put(tbOrder.getUserId(),payLog);
        }
        //8 清空购物车缓存
        redisTemplate.boundHashOps("cartList").delete(tbOrder.getUserId());
    }

    //从人redis中查询支付日志信息
    @Override
    public TbPayLog findIdFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    //修改订单的支付状态
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1 根据订单id查询支付日志
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        //1.2 设置相关的属性
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLog.setTradeState("1");                      //设置为已支付状态
        payLogMapper.updateByPrimaryKey(payLog);

        //2  修改订单状态  获取订单列表
        String orderList = payLog.getOrderList();
        String[] orderIds = orderList.split(",");

        //3 遍历orderIds,修改每个订单的支付状态
        for (String orderId : orderIds) {
            //3.1 根据订单id查询订单对象
            TbOrder order = orderMapper.selectByPrimaryKey(new Long(orderId));
            // 3.2 修改订单对象中的支付状态
            order.setStatus("2"); //设置为已支付
            //3.3 修改订单
            orderMapper.updateByPrimaryKey(order);
        }
    }
}
