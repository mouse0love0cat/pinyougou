package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/10/10 0010
 * @Description:
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPages(int pagNum, int pageSize) {

        //使用分页插件
        PageHelper.startPage(pagNum, pageSize);
        /*返回一个page对象*/
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public PageResult findPagesByKey(TbBrand tbBrand, int pageNum, int pageSize) {

        /*开始分页查询*/
        PageHelper.startPage(pageNum, pageSize);
        /*创建一个查询条件*/
        TbBrandExample example = new TbBrandExample();
        /*拼接查询条件*/
        TbBrandExample.Criteria criteria = example.createCriteria();
        //判断对象是否存在
        if (tbBrand != null) {
            /*判断品牌名称是否为空*/
            if (tbBrand.getName() != null && tbBrand.getName().length() > 0) {
                /*根据品牌名称进行查询*/
                criteria.andNameLike("%" + tbBrand.getName() + "%");
            }
            /*判断首字母是否为空*/
            if (tbBrand.getFirstChar() != null && tbBrand.getFirstChar().length() > 0) {
                /*根据首字母查询*/
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);

        /*返回一个pageresult对象*/
        return new PageResult(page.getTotal(), page.getResult());
    }

    /*根据id查询*/
    @Override
    public TbBrand findByBrandId(long brnadId) {
        return tbBrandMapper.selectByPrimaryKey(brnadId);
    }

    /*添加品牌*/
    @Override
    public void addBrand(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }


    /*修改一条记录*/
    @Override
    public void updateBrand(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKeySelective(tbBrand);
    }

    /*删除一条记录*/
    @Override
    public void deleteBrand(long[] brandId) {
        for (long id : brandId) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }


    /*下拉框显示品牌数据*/
    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectAllBrand();
    }

}
