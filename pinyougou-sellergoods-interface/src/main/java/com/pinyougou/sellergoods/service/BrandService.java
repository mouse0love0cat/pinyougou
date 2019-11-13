package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/10/10 0010
 * @Description: 品牌管理的接口
 */
public interface BrandService {

    /*查询全部品牌*/
    List<TbBrand> findAll();

    /*分页查询*/
    PageResult findPages(int pagNum, int pageSize);


    /*条件综合查询---分页显示*/
    PageResult findPagesByKey(TbBrand tbBrand, int pageNum, int pageSize);

    /*根据id查询品牌*/
    TbBrand findByBrandId(long brnadId);

    /*添加*/
    void addBrand(TbBrand tbBrand);

    /*修改*/
    void updateBrand(TbBrand tbBrand);

    /*删除*/
    void deleteBrand(long[] brnadId);

    /*品牌下拉框数据*/
    List<Map> selectOptionList();

}
