package com.pinyougou.page.listenner;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author: wangyilong
 * @Date: 2019/11/26 0026
 * @Description: 页面配置
 */
@Component
public class PageListenner implements MessageListener {

    @Autowired
    private ItemPageService pageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            //1 获取文件内容
            String ids = textMessage.getText();
            System.out.println("接收到的消息"+ids);
            boolean b = pageService.genItemHtml(new Long(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
