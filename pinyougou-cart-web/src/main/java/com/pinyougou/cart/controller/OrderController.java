package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wangyilong
 * @Date: 2019/12/3 0003
 * @Description: 订单操作
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Reference
    private OrderService orderService;

    //添加订单列表到数据库
    @RequestMapping("add")
    public Result add(@RequestBody  TbOrder order){
        try {
            //1 获取用户名
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(userId);
            //2 根据用户名添加
            order.setUserId(userId);
            order.setSourceType("2");
            //3 添加订单列表
            orderService.add(order);
            return new Result(true,"添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败!");
        }
    }
}
