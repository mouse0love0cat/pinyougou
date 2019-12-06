package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	RedisTemplate redisTemplate;
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private IdWorker idWorker;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}
	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
		}
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//用户下单请求
	@Override
	public void submitOrder(Long id, String userId) {
		//1 根据用商品id  从缓存中查询秒杀商品
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
		//2 判断商品是否存在
		if (seckillGoods == null){
			throw new RuntimeException("商品不在秒杀活动!");
		}
		//3 若商品存在  判断该商品的库存是否为0
		if (seckillGoods.getStockCount() == 0){
			throw new RuntimeException("该商品已被售罄!");
		}
		//4 商品存在且库存不为0，则进行下单操作
		//4.1 进行减库存操作
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		//4.2 将商品数据放回缓存
		redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(),seckillGoods);
		//4.2 判断当减完库存后  剩余库存的数量是否为0
		if (seckillGoods.getStockCount() == 0){
			//4.2.1 则将该数据同步到数据库
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			//4.2.2 同时清空redis中该商品的数据
			redisTemplate.boundHashOps("seckillGoodsList").delete(seckillGoods.getId());
		}

		//5 进行生成秒杀订单操作
		//5.1 创建秒杀订单对象
		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		//5.2 生成订单编号
		long seckillId = idWorker.nextId();
		seckillOrder.setId(seckillId);
		seckillOrder.setSeckillId(seckillId);
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setStatus("0");                                               //设置状态
		seckillOrder.setMoney(seckillGoods.getCostPrice());		//设置秒杀价格
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setUserId(userId);											//设置用户id
		//5.3 将订单信息存入redis中
		redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);
	}

	//根据用户id从redis中获取商品数据
	@Override
	public TbSeckillOrder findOrderFromRedisById(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	//将秒杀订单数据保存到数据中
	@Override
	public void saveOrderFromRedisToDb(String userId, Long out_trade_no,String transactionId) {
		//1 从redis中查询订单信息
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		//2 判断订单是否为空
		if (seckillOrder == null){
			throw new RuntimeException("订单不存在");
		}
		if (seckillOrder.getId().longValue() != out_trade_no.longValue()){
			throw new RuntimeException("订单不相符");
		}
		seckillOrder.setTransactionId(transactionId);                           //设置流水号
		seckillOrder.setStatus("1");                                                     //状态
		seckillOrder.setPayTime(new Date());									  //设置支付时间
		//3 插入数据库
		seckillOrderMapper.insert(seckillOrder);
		//4 清空redis中的订单缓存
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
	}

	//将订单信息从redis中删除，并修改库存信息
	@Override
	public void deleteFromRedis(String userId, Long out_trade_no) {
		//1 根据用户id查询订单列表
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		//2 判断当前订单是否为空并且订单id是否一致
		if (seckillOrder != null && seckillOrder.getId().longValue() ==out_trade_no.longValue() ){
			//2.1 将订单从缓存库中删除
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
			//2.2 恢复数据 修改库存
			//2.2.1 从redis中查询秒杀商品信息
			TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(seckillOrder.getSeckillId());
			//2.2.2 修改缓存中的库存信息
			seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
			//2.2.3 将修改的秒杀商品数据放回redis中
			redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(),seckillGoods);
		}
	}


}
