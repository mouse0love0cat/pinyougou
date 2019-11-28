package com.pinyougou.page.service;


//页面静态化接口

public interface ItemPageService {

    //导入相关的页面静态化 生成商品详细页
    public boolean genItemHtml(Long goodsId);
    //删除页面静态化文件
    public boolean deleteItemHtml(Long[] goodsId);
}
