package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/11/19 0019
 * @Description:
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //1 创建一个Map对象 用于接收返回值
        Map map = new HashMap();
        //2 创建一个查询对象 查询全部
        Query query = new SimpleQuery("*:*");
        //3 创建查询条件对象
        Criteria criteria = new Criteria("item_keywords");
        //3.1 判断当前查询是否为空
        if (StringUtils.isNotBlank(searchMap.get("keywords")+" "));{
            //3.2 不为空，则设置查询条件
            criteria = criteria.is(searchMap.get("keywords"));
        }
        //4 添加查询条件
        query.addCriteria(criteria);
        //5 分页
        query.setOffset(0);
        query.setRows(10);
        //6 开始分页查询
        GroupPage<TbItem> itemPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //7 得到查询的结果
        List<TbItem> rows = itemPage.getContent();
        //8 将结果返回给前天
        map.put("rows",rows);
        //9 返回map集合
        return map;
    }
}
