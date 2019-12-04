package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/12/4 0004
 * @Description:
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;
    @Autowired
    private IdWorker idWorker;

    @RequestMapping("createNative")
    public Map createNative(){
        long id = idWorker.nextId();
        System.out.println(id);
        return payService.createNative(id+"","1");
    }

    //查询订单 根据订单号查询订单  判断是否支付，若已支付  跳转到支付成功页面  若未支付  定时刷新二维码 生成新的订单
    @RequestMapping("quertyOrder")
    public Result quertyOrder(String out_trade_no){
        //1 定时的去查询订单是否支付
        int x = 0;
        while (true){
            //1 调用查询接口查询订单
            Map resultMap = payService.queryOrder(out_trade_no);
            if (resultMap == null){
                return  new Result(true,"支付出错!");
            }
            if (resultMap.get("trade_state").equals("SUCCESS")){
                return new Result(true,"支付成功!");
            }
            try {
                //每查询一次订单  休息3秒
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //定时5分钟 也就是去查询100次订单，若此订单还未支付 则重新生成二维码
            x++;
            if (x>=100){
               return new Result(false,"二维码超时");
            }
        }
    }


}
