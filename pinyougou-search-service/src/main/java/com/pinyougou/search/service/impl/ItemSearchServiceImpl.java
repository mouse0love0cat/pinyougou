package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
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
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //0 创建一个Map对象 用于接收返回值
        Map map = new HashMap();
        //1.进行高亮查询
        searchMap.put("keywords",searchMap.get("keywords").toString().replace(" ",""));
        //2 设置高亮查询
        Map highLightMap = highLightList(searchMap);
        map.putAll(highLightMap);
        //3设置分组信息
        List<String> categoryList = getCategoryGroupList(searchMap);
        map.put("categoryList", categoryList);
            //4 根据分类显示品牌和规格参数
        //5.根据分类名称查询品牌列表及规格列表
        //5.1)得到分类的值
        String category = (String) searchMap.get("category");
        Map brandAndSpecMap = null;
        if (StringUtils.isNotBlank(category)) {
            brandAndSpecMap = saveToList(category);
        } else if (categoryList != null && categoryList.size() > 0) {
            brandAndSpecMap = saveToList(categoryList.get(0));
        }
        map.putAll(brandAndSpecMap);

        return map;
    }
    //商品添加或是审核  同步索引库
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
        System.out.println("同步索引库成功!");
    }

    //从数据库删除数据时  同步删除索引库
    @Override
    public void deleteFromIndex(Long[] ids) {
        System.out.println("goodsid"+ids);
        //1 创建查询对象
        SimpleQuery query = new SimpleQuery();
        //2 创建查询条件
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        //3 添加查询条件
        query.addCriteria(criteria);
        //4 执行删除
        solrTemplate.delete(query);
        //5 提交操作
        solrTemplate.commit();
    }


    /**
     * @Description 设置高亮查询
     * @Author wangyilong
     * @Date 2019/11/20 0020 下午 12:18
     * @Param
     * @Return
     * @Exception
     */
    private Map highLightList(Map searchMap) {
        //0创建一个map对象，用于结果返回
        Map highLightMap = new HashMap();
        //1 创建一个查询对象
        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        /*------------------------------------------------------------第一部分  关键字查询--------------------------------------------*/
        //1.1  创建查询条件
        Criteria criteria = new Criteria("item_keywords");
        //1.2 当searchMap不为空时执行查询操作
        if (searchMap !=null){
            //1.2  判断查询条件是否为空
            if (StringUtils.isNotBlank(searchMap.get("keywords") + " ")) ;
            {
                //1.3 不为空则将查询条件放入查询对象中
                criteria.is(searchMap.get("keywords"));
            }
            highlightQuery.addCriteria(criteria);

            /*------------------------------------------------------------第二部分  过滤查询--------------------------------------------*/

            //2.1 设置查询的分类过滤
            if(StringUtils.isNotBlank(searchMap.get("category")+"")){
                //2.1.2 创建过滤查询条件
                SimpleFilterQuery  filterQuery = new SimpleFilterQuery();
                //2.1.3设置分类的查询过滤
                filterQuery.addCriteria(new Criteria("item_category").is(searchMap.get("category")));
                //2.1.4 将过滤于高亮查询进行绑定
                highlightQuery.addFilterQuery(filterQuery);
            }

            //2.2 设置品牌的分类过滤
            if (StringUtils.isNotBlank(searchMap.get("brand")+"")){
                //2.2.1 将品牌过滤条件与高亮查询进行绑定
                highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_brand").is(searchMap.get("brand"))));
            }

            //2.3 设置规格参数过滤
            if(StringUtils.isNotBlank(searchMap.get("spec")+"")){
                //2.3.1 将规格参数列表转换为map对象
                Map  specMap = JSON.parseObject(searchMap.get("spec")+"", Map.class);
                //2.3.2 遍历规格选项，为了得到动态域
                for (Object key : specMap.keySet()) {
                    //2.3.3 创建查询对象
                    SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                    //2.3.4 得到动态域字段
                    Criteria criteria1 = new Criteria("item_spec_"+key).is(specMap.get(key));
                    //2.3.5 添加过滤查询
                    filterQuery.addCriteria(criteria1);
                    //2.3.6 与高亮查新进行绑定
                    highlightQuery.addFilterQuery(filterQuery);
                }
            }

            //2.4 设置价格区间过滤查询
            if(StringUtils.isNotBlank(searchMap.get("price")+"")){
                //2.4.1 将价格区间进行分割为字符串
                String[] prices =  searchMap.get("price").toString().split("-");
                //2.4.2 判断初始值是否为0 ，若为0 就是大于等于
                //创建过滤查询条件
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                if (!prices[0].equals("0")){
                    filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(prices[0]));
                }
                //2.4.3 判断最后一个结束值是否是*
                //不为* 则小于等于
                if (!prices[1].equals("*")){
                    filterQuery.addCriteria(new Criteria("item_price").lessThanEqual(prices[1]));
                }
                //j2.4.4 将过滤条件与高亮查询进行绑定
                highlightQuery.addFilterQuery(filterQuery);
            }



            /*------------------------------------------------------------第三部分  排序查询--------------------------------------------*/
            //3.1 得到排序字段和排序关键字
            String sort = (String) searchMap.get("sort");                         //排序关键字
            System.out.println(sort);
            String sortField = (String) searchMap.get("sortField");         //排序字段
            System.out.println(sortField);

            //3.2 判断是否存在排序的关键字和排序字段
            if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(sortField)){
                // 3.2根据排序的关键进行判断是升序还是降序
                if (sort.equals("ASC")){
                    highlightQuery.addSort(new Sort(Sort.Direction.ASC,"item_"+sortField));
                }else if (sort.equals("DESC")){
                    highlightQuery.addSort(new Sort(Sort.Direction.DESC,"item_"+sortField));
                }
            }

            /*------------------------------------------------------------第四部分  分页查询--------------------------------------------*/
            //4 得到分页参数
            //4.1 得到当前页
             int page  = new Integer(searchMap.get("page")+"");
             //4.2 得到每页大小
            int pageSize = new Integer(searchMap.get("pageSize")+"");
            //4.3 设置分页
            highlightQuery.setOffset((page-1)*pageSize);          //设置偏移量
            highlightQuery.setRows(pageSize);                          //设置每页大小

            /*------------------------------------------------------------第五部分  高亮查询--------------------------------------------*/
            //5 创建高亮对象，设置高亮选项
            HighlightOptions highlightOptions = new HighlightOptions();
            //5.1 设置高亮字段
            highlightOptions.addField("item_title");
            //5.2 设置高亮字段的额前后缀
            highlightOptions.setSimplePrefix("<span style='color:red'>");
            highlightOptions.setSimplePostfix("</span>");
            //5.3 将高亮选项放入查询中
            highlightQuery.setHighlightOptions(highlightOptions);
            //6 获取高亮分页对象
            HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
            //6.1 得到高亮的字段列表
            List<HighlightEntry<TbItem>> highlightList = highlightPage.getHighlighted();
            //6.2 判断列表是否为空
            if (highlightList != null && highlightList.size() > 0) {
                //6.3 遍历列表
                for (HighlightEntry<TbItem> entity : highlightList) {
                    //6.4 获取原始的对象
                    TbItem item = entity.getEntity();
                    //6.5 获取高亮的字段集合
                    List<HighlightEntry.Highlight> highlights = entity.getHighlights();
                    //6.6 判断集合是否为空
                    if (highlights != null && highlights.size() > 0) {
                        //6.7 遍历集合
                        for (HighlightEntry.Highlight highlight : highlights) {
                            //6.8 得到高亮字段
                            List<String> snipplets = highlight.getSnipplets();
                            //6.9 将高亮字段与原始的对象进行绑定
                            item.setTitle(snipplets.get(0));
                        }
                    }
                }
            }
            //7 将内容放入map集合中
            highLightMap.put("rows", highlightPage.getContent());
            //8 设置分页参数
            highLightMap.put("totalPages",highlightPage.getTotalPages());   //得到总页数
            highLightMap.put("total",highlightPage.getTotalElements());        //得到总记录数
        }
        //8 返回map集合
        return highLightMap;
    }


    /**
     * @Description 分组对象
     * @Author wangyilong
     * @Date 2019/11/20 0020 下午 2:59
     * @Param
     * @Return
     * @Exception
     */
    private List<String> getCategoryGroupList(Map searchMap) {
        //3.0 定义一个list'集合 ，用于存储返回值
        List categoryList = new ArrayList();
        //3.1  创建查询对象
        Query query = new SimpleQuery();
        //3.2 创建查询条件对象
        Criteria criteria = new Criteria("item_keywords");
        //3.3 判断item_keywords是否为空
        if (StringUtils.isNotBlank(searchMap.get("keywords") + " ")) ;
        {
            //3.4 将值设置到查询条件中
            criteria.is(searchMap.get("keywords"));
            query.addCriteria(criteria);
        }

        //3.5 创建分组对象
        GroupOptions groupOptions = new GroupOptions();
        //设置分组选项
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //3.5 获取分组查询对象
        GroupPage<TbItem> itemGroupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //3.5.1 获取分类级别的分组对象
        GroupResult<TbItem> groupResult = itemGroupPage.getGroupResult("item_category");
        //3.5.2 获取分组对象入口
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //3.5.3 获取分组的内容
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //3.5.4 遍历内容集合
        for (GroupEntry<TbItem> entry : content) {
            //3.5.5 获取value值
            String groupValue = entry.getGroupValue();
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    //4 根据分类从redis中获取品牌规格列表
    public Map saveToList(String category) {
        //4.0 创建一个map  用于存放集合
        Map map = new HashMap();
        //4.1 根据分类获取模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //4.2 根据模板id获取相应的品牌列表
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        //4.3 根据模板id获取相应的分类列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

        //4.4 将集合放入map
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }

}
