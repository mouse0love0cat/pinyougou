package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    //搜索
    public Map<String,Object> search(Map searchMap);

    //导入更新的商品索引库
    void importList(List list);

    //从数据库删除时  同步索引库
    void deleteFromIndex(Long[] ids);
}
