package com.pinyougou.search.listenner;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.List;

/**
 * @author: wangyilong
 * @Date: 2019/11/26 0026
 * @Description: 监听容器
 */
@Component
public class ItemDeleteListenner implements MessageListener {

    @Autowired
    private ItemSearchService searchService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage textMessage = (ObjectMessage) message;
            //1 获取文本对象
            Long[]  ids = (Long[]) textMessage.getObject();
            //2 执行删除操作
            System.out.println("监听接收的消息。。。。"+ids);
            searchService.deleteFromIndex(ids);
            System.out.println("适应activemq删除索引库成功!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
