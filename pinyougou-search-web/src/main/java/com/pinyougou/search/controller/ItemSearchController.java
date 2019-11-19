package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by WF on 2019/11/19 10:35
 */
@RestController
@RequestMapping("itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService searchService;
    //1.根据关键字进行查询
    @RequestMapping("search")
    public Map search(@RequestBody Map searchMap){
        return searchService.search(searchMap);
    }
}
