package com.pinyougou.page.listenner;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.Serializable;

/**
 * @author: wangyilong
 * @Date: 2019/11/26 0026
 * @Description: 静态模板文件删除的监听
 */
@Component
public class PageDeleteListenner implements MessageListener {
    
    @Autowired
    private ItemPageService itemPageService;
    
    @Override
    public void onMessage(Message message) {
        try {
            //1 创建message对象
            ObjectMessage objectMessage = (ObjectMessage) message;
            //2 得到文本对象
            Long[] ids = (Long[]) objectMessage.getObject();
            //3 根据id删除文件
            boolean b = itemPageService.deleteItemHtml(ids);
            System.out.printf("网页删除结果"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
