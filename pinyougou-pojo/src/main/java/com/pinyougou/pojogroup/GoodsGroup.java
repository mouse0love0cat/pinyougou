package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;

import java.io.Serializable;
import java.util.List;

/**
 * 商品组合实体类
 * @author 晓电脑
 */
public class GoodsGroup implements Serializable{

    /**
     * 未审核
     */
    public static final String GOODS_UNAUDITED="0";

    /**
     * spu 商品实体
     */
    private TbGoods goods;


    /**
     * spu 商品拓展实体
     */
    private TbGoodsDesc goodsDesc;

    /**
     * sku 商品属性
     */
    private List<TbItem> items;

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItems() {
        return items;
    }

    public void setItems(List<TbItem> items) {
        this.items = items;
    }
}
