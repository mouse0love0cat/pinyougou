package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/12/6 0006
 * @Description: 订单支付
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private SeckillOrderService seckillOrderService;
    @Reference
    private PayService payService;


    //1 生成二维码
    @RequestMapping("createNative")
    public Map createNative(){
        //1 获取用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //2 根据用户名到redis中查询订单信息
        TbSeckillOrder seckillOrder = seckillOrderService.findOrderFromRedisById(userId);
        //3 判断当前订单是否存在
        if (seckillOrder != null){
            //3.1 若订单存在 则生成支付二维码
            return payService.createNative(seckillOrder.getId() + "", seckillOrder.getMoney().doubleValue() * 100 + "");
        }else {
            return new HashMap();
        }
    }

    //2 查询订单状态
    @RequestMapping("quertyOrder")
    public Result quertyOrder(Long out_trade_no){
        //1 查询用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int x = 0;
        while (true){
            //2 根据订单id查询支付状态
            Map map = payService.queryOrder(out_trade_no+"");
            //2.1 当前返还结果为空
            if (map == null){
                return new Result(false,"支付失败!");
            }
            //2.2 当返回的状态为SUCCESS时，表明支付成功
            if (map.get("trade_state").equals("SUCCESS")){
                //2.2.1 支付成功 则将订单数据保存到数据库中
                seckillOrderService.saveOrderFromRedisToDb(userId,out_trade_no,map.get("transaction_id")+"");
                //2.2.2 返回支付成功信息
                return new Result(true,"支付成功");
            }

            //每隔3秒去查询支付状态
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            //3 当查询次数超过二十次时，二维码超时
            if (x>=20){
                //3.1 当二维码超时时，进行关单操作
                Map resultMap = payService.closePay(out_trade_no);
                //3.2 判断当前的返回码为fail时
                if (resultMap.get("result_code").equals("FAIL")){
                    //3.2.1 当前的错误吗为ORDERPAID时  表示订单已支付，不能进行关单操作
                    if (resultMap.get("err_code").equals("ORDERPAID")){
                        //3.2.2 将订单信息存入数据库
                        seckillOrderService.saveOrderFromRedisToDb(userId,out_trade_no,map.get("transaction_id")+"");
                        return new Result(true,"支付成功!");
                    }
                }
                //4 从redis中删除商品订单信息，并恢复库存
                seckillOrderService.deleteFromRedis(userId,out_trade_no);
                return new Result(true,"二维码超时");
            }

        }
    }

}
