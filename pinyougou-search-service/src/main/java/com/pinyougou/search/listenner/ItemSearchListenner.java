package com.pinyougou.search.listenner;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @author: wangyilong
 * @Date: 2019/11/26 0026
 * @Description: 监听容器
 */
@Component
public class ItemSearchListenner implements MessageListener {

    @Autowired
    private ItemSearchService searchService;

    @Override
    public void onMessage(Message message) {

        System.out.println("监听接收的消息。。。。");
        try {
            //1 获取文本对象
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            //2 文本对象转换为list
            List<TbItem> items = JSON.parseArray(text, TbItem.class);
            //3 同步索引库
            searchService.importList(items);
            System.out.println("使用activemq同步索引库成功。。。。。。");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
