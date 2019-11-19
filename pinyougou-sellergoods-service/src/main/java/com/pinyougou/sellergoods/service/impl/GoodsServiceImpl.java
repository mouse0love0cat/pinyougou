package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.GoodsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	//商品基本属性
	@Autowired
	private TbGoodsMapper goodsMapper;
	//商品扩展属性
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	//s商品的sku
	@Autowired
    private TbItemCatMapper itemCatMapper;
	//商品的品牌
	@Autowired
    private TbBrandMapper brandMapper;
	//商品所属商家
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(GoodsGroup goods) {
		//设置为未申请状态
		goods.getGoods().setAuditStatus("0");
		//1 添加商品
		goodsMapper.insert(goods.getGoods());
		//设置商品id
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		// 2 插入商品扩展数据
		goodsDescMapper.insert(goods.getGoodsDesc());
		//3 添加items 得到item集合
        List<TbItem> items = goods.getItems();
        //3.1 遍历集合，设置相关属性
        addItem(goods);
    }

    public void addItem(GoodsGroup goods) {
        List<TbItem> items = goods.getItems();
        for (TbItem item : items) {
            //3.2 设置商品名称
            item.setTitle(goods.getGoods().getGoodsName());
            //3.2 设置商品id
            item.setGoodsId(goods.getGoodsDesc().getGoodsId());
            //3.3 设置商品的分类id 为三级分类id
            item.setCategoryid(goods.getGoods().getCategory3Id());
            //3.4 设置分类名称
            String name = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id()).getName();
            item.setCategory(name);
            //3.5 设置时间
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            //3.6 设置品牌名称
            String brandName = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId()).getName();
            item.setBrand(brandName);
            //3.7 设置商家id
            String sellerName = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId()).getName();
            item.setSeller(sellerName);
            //3.8 添加图片
            //3.8.1 得到商品图片列表  字符串类型
            String images = goods.getGoodsDesc().getItemImages();
            //3.8.2 将其转换为map对象
            List<Map> maps = JSON.parseArray(images, Map.class);
            //3.8.3 遍历map集合  获取第一张图片的url
            if (maps !=null && maps.size()>0){
                String url = (String) maps.get(0).get("url");
                item.setImage(url);
            }
            //执行添加操作
            itemMapper.insert(item);
        }
    }


    /**
	 * 修改
	 */
	@Override
	public void update(GoodsGroup goods){
	    //先修改goods表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//在修改商品描述表
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //修改item表  先删除，再添加
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //再添加item属性
        addItem(goods);
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public GoodsGroup findOne(Long id){
        //1 创建实体类对象
        GoodsGroup goods = new GoodsGroup();
        // 2 根据id 获取实体
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //3 将tbgoods设置给goods
        goods.setGoods(tbGoods);
        //4 根据id查询商品描述对象
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        //5 设置商品的item属性
        //5.1 创建一个查询实例
		TbItemExample example = new TbItemExample();
		//5.2 创建查询条件
        TbItemExample.Criteria criteria = example.createCriteria();
        //5.3 添加查询条件
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItems(tbItems);
        //5 返回实体类对象
        return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}

		@Override
	public PageResult findPageByKey(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
		    if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
                //改为精确匹配
                criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

}
