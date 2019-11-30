package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/11/27 0027
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue userQueue;
    @Autowired
    private TbUserMapper userMapper;

    //1 生成随机六位验证码  并发送消息到队列
    @Override
    public void getValidCode(String phone) {
        //1 随机生成六位数的验证码
        String code = (long)((Math.random()) * 1000000) + "";
        System.out.printf("code:"+code);
        //2 将数据放入redis缓存中  用于用户登录时的验证码的比对
        redisTemplate.boundHashOps("smscode").put(phone,code);
        //3 使用消息队列发送消息
        jmsTemplate.send(userQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //3.1 根据对应的接口api  组装数据
                //3.1.1 使用一个map集合用于存放组装的数据
                MapMessage mapMessage = session.createMapMessage();
                //3.2 组装数据
                mapMessage.setString("phone",phone);
                mapMessage.setString("signName","品优购");
                mapMessage.setString("templateCode","SMS_178756327");
                //3.3 根据格式  组装code数据
                Map map = new HashMap();
                map.put("code",code);
                String param = JSON.toJSONString(map);
                mapMessage.setString("param",param);
                //3.4 返回结果消息
                return mapMessage;
            }
        });
    }

    //2 判断验证码是否一致
    @Override
    public boolean checkValidCode(String phone, String validCode) {
        //1 从redis中获取生成的验证码
        String smscode = (String) redisTemplate.boundHashOps("smscode").get(phone);

        //2 对验证码做非空判断
        if (smscode == null){
            return  false;
        }
        if (!smscode.equals(validCode)){
            return false;
        }
        return true;
    }

    //3 添加用户操作
    @Override
    public void add(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        userMapper.insert(user);
    }
}
