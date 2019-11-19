package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/11/19 0019
 * @Description:
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class ImportDate {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    //将数据库中的商品导入索引库
    @Test
    public void TestImportDate(){
        //1 查询审核通过的商品
        //1.1创建查询对象
        TbItemExample example = new TbItemExample();
        //1.2 创建查询条件
        TbItemExample.Criteria criteria = example.createCriteria();
        //1.3 设置查询条件
        criteria.andStatusEqualTo("1");
        List<TbItem> items = itemMapper.selectByExample(example);
        //2 遍历集合  得到spec的属性，设置动态域
        for (TbItem item : items) {
            //2.1 得到规格参数列表
            String spec = item.getSpec();
            //2.2 将字符类型转换为map
            Map map = JSON.parseObject(spec, Map.class);
            //2.3 将该map域item进行绑定
            item.setSpecMap(map);
        }
        //3 保存
        solrTemplate.saveBeans(items);
        //4 提交
        solrTemplate.commit();
    }

}
