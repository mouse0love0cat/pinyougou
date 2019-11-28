package com.zelin.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.zelin.sms.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/11/27 0027
 * @Description: 消息的消费者（监听）
 */
@Component
public class Consumer {

    @Autowired
    private SmsUtils smsUtils;

    @JmsListener(destination = "itcast")
    public void readMessage(String text){
        System.out.printf("接收的消息:"+text);
    }

    @JmsListener(destination = "itcast_map")
    public void readMap(Map map){
        System.out.printf("接收的消息"+map);
    }

    //3 接收来自user-web的消息 并给阿里大于发送短信
    @JmsListener(destination = "user_quene")
    public void getCodeMessage(Map map){
        //1 获取内容 给阿里大于发短信
        String phone = map.get("phone").toString();
        String signName = map.get("signName").toString();
        String templateCode = map.get("templateCode").toString();
        String param = map.get("param").toString();
        //3.1.2)将消息发送给阿里大于
        try {
            smsUtils.sendSms(phone,signName,templateCode,param);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
