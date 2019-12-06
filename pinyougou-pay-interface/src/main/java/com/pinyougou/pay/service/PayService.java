package com.pinyougou.pay.service;


import java.util.Map;

/**
* @Description 支付接口
* @Author  wangyilong
* @Date   2019/12/4 0004 下午 12:04
* @Param
* @Return
* @Exception
*
*/

public interface PayService {

    //生成二维码
    public Map createNative(String out_trade_no, String total_fee);
    //查询订单状态
    public Map queryOrder(String out_trade_no);
    //关闭订单
    public Map closePay(Long out_trade_no);
}
