package com.zelin.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/11/27 0027
 * @Description: 用于消息的发送  使用内置的activemq发送服务
 */

@RestController
public class QueueProducer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    //1 使用内置服务发送文本数据
    @RequestMapping("/send")
    public void sendMessage(String text){
        //发送消息
        jmsMessagingTemplate.convertAndSend("itcast",text);
    }

    //2 使用外置的服务进行服务
    @RequestMapping("sendMap")
    public void sendMap(){
        Map map = new HashMap();
        map.put("name","zhangsan");
        map.put("sex","男");
        map.put("age","20");
        jmsMessagingTemplate.convertAndSend("itcast_map",map);
    }





}
