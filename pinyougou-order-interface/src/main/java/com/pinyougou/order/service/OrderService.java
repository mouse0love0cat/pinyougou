package com.pinyougou.order.service;


import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

public interface OrderService {
    //添加订单
    public void add(TbOrder tbOrder);
    //根据用户id从redis中查询支付日志信息
    public TbPayLog findIdFromRedis(String userId);
    //修改订单的支付状态
    public void updateOrderStatus(String out_trade_no, String transaction_id);
}
