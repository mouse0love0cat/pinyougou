package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.GoodsGroup;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;

import javax.jms.*;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	/*@Reference
	private ItemSearchService    searchService;*/
	/*@Reference
	private ItemPageService itemPageService;*/
	//引入消息中间件 activemq
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private ActiveMQQueue goodsQueryQueue;
	@Autowired
	private ActiveMQQueue goodsDeleteQueue;
	@Autowired
    private ActiveMQTopic pageQueue;
	@Autowired
	private ActiveMQTopic pageDelete;


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody GoodsGroup goods){
		try {
            goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsGroup goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public GoodsGroup findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			//1 从数据库中删除数据
			goodsService.delete(ids);
			//2 从索引库中删除数据
			//searchService.deleteFromIndex(ids);
			//从索引库中删除
			//发送消息
			jmsTemplate.send(goodsDeleteQueue, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {

				    //将id发送到消息队列中
                    ObjectMessage message = session.createObjectMessage(ids);

                    return message;
				}
			});

			jmsTemplate.send(pageDelete, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});


			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPageByKey(goods, page, rows);
	}

	//同步更新索引库
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] goodsIds,String status){
		try {
		    //1 修改商品状态
			goodsService.updateStatus(goodsIds,status);
			//2 状态修改成功后页面静态化
           if (status.equals("1") ){
               for (final Long goodsId : goodsIds){
                   jmsTemplate.send(pageQueue, new MessageCreator() {
                       @Override
                       public Message createMessage(Session session) throws JMSException {
                           return session.createTextMessage(goodsId+"");
                       }
                   });
               }
           }


			List<TbItem> itemList = goodsService.findItemByGoodIds(goodsIds,status);
			if (itemList != null && itemList.size() > 0){
				//更新索引库
				//searchService.importList(itemList);
				/*使用mq发送需要更新的数据消息给搜索服务*/
			//1 将集合转换为字符串类型
				final  String items = JSON.toJSONString(itemList);
				//2 发送消息
				jmsTemplate.send(goodsQueryQueue,new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						//发送信息
						Message message = session.createTextMessage(items);
						return message;
					}
				});

			}else {
				System.out.println("无数据！");
			}
			return new Result(true,"商品审核成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"商品审核失败!");
		}

	}

	/*
	@RequestMapping("genHtml")
	public void genItemHtml(Long goodsId){
		itemPageService.genItemHtml(goodsId);
	}

	*/

	
}
