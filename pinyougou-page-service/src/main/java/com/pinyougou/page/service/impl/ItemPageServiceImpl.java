package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/11/25 0025
 * @Description:   页面静态化服务
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Value("${pathDir}")
    private String pathDir;

    @Override
    public boolean genItemHtml(Long goodsId) {

        try {
            //1 获取FreemakerConfigurer对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //2 根据对象获取模板
            Template template = configuration.getTemplate("item.ftl");
            //3 创建一个map   用于存放数据的集合
            Map data = new HashMap();

            //3.0 根据商品id查询isku列表

            //3.0.1 创建查询对象
            TbItemExample example = new TbItemExample();
            //3.0.2 创建查询条件
            TbItemExample.Criteria criteria = example.createCriteria();
            //3.0.3 设置查询条件
            //查询已审核的sku商品
            criteria.andStatusEqualTo("1");
            //根据商品id进行查询
            System.out.println(goodsId);
            criteria.andGoodsIdEqualTo(goodsId);
            //按照状态降序排序  ，保证第一个为默认
            example.setOrderByClause("is_default desc");
            List<TbItem> items = itemMapper.selectByExample(example);
            //将数据放入map集合中
            data.put("items",items);
            //3,1 根据商品id查询商品表
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            //3.2 根据商品id 查询商品描述信息
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //3.3 生成面包屑导航的分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            //3.4 将数据放入map集合
            data.put("goods",tbGoods);
            data.put("goodsDesc",tbGoodsDesc);
            data.put("itemCat1",itemCat1);
            data.put("itemCat2",itemCat2);
            data.put("itemCat3",itemCat3);
            //3.5 写出数据
            Writer out = new FileWriter(pathDir+goodsId+".html");
            //3.6 进行模板对象的生成
            template.process(data,out);
            //3.7 关闭写入流
            out.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //删除文件
    @Override
    public boolean deleteItemHtml(Long[] goodsId) {
        try {
            for (Long id : goodsId) {
                new File(pathDir+id+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
