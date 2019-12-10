package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wangyilong
 * @Date: 2019/12/9 0009
 * @Description:
 */
@Component
public class Task {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    //每隔一分钟同步一次缓存库
    @Scheduled(cron = "0 * * * * *")
    public void refreshRedis(){
        System.out.println("从redis中同步数据");
        //1 查询秒杀商品
        // 1.1得到秒杀商品的id集合
        List ids = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        //1.2 条件查询
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");                                                           //审核成功的商品
        criteria.andStockCountGreaterThan(0);                                               //库存大于0的商品
        criteria.andStartTimeLessThanOrEqualTo(new Date());                                   //开始时间小于等于当前时间
        criteria.andEndTimeGreaterThan(new Date());                                                //结束时间大于当前时间
        criteria.andIdNotIn(ids);                                                                                 //排除已存在的商品
        //2 得到秒杀商品列表
        List<TbSeckillGoods> tbSeckillGoods = seckillGoodsMapper.selectByExample(example);
        //3 循环列表 将商品加入缓存中
        for (TbSeckillGoods tbSeckillGood : tbSeckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGood.getId(),tbSeckillGood);
        }
        System.out.println("缓存同步完成！");
    }


    //移除过期的商品信息
    @Scheduled(cron = "* * * * * *")
    public void removeFromRedis(){
        //1 查询当前秒杀商品列表
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        //2 遍历商品列表  查询商品信息
        for (TbSeckillGoods seckillGood : seckillGoods) {
            //2.1 秒杀商品的结束时间小于当前时间，则该商品失效
            if (seckillGood.getEndTime().getTime() < new Date().getTime()){
                //2.1.1 更新数据库
                seckillGoodsMapper.updateByPrimaryKey(seckillGood);
                //2.1.2 从缓存中移出该过期商品信息
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
                System.out.println("移出的商品id为:"+seckillGood.getId());
            }
        }
        System.out.println("从缓存中移出过期商品。。。。");
    }


}
