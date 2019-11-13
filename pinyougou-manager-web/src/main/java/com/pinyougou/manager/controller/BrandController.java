package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/10/14 0014
 * @Description:
 */

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /*查询全部商品*/
    @RequestMapping("/findall")
    public List<TbBrand> findAllBrand(){

        return brandService.findAll();
    }

    /*分页查询*/
    @RequestMapping("/findpage")
    public PageResult findPage(int page,int rows){

        return brandService.findPages(page,rows);
    }

    /*模糊查询-------搜索*/
    @RequestMapping("/search")
    public PageResult findPageByKey(@RequestBody  TbBrand brand,int page,int rows){

        return brandService.findPagesByKey(brand,page,rows);
    }

    /*添加操作*/
    @RequestMapping("/add")
    public Result addBrand(@RequestBody  TbBrand brand){

        try {
            brandService.addBrand(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,"添加失败");
        }
    }

    /*修改操作*/
    @RequestMapping("/update")
    public Result updateBrand(@RequestBody  TbBrand tbBrand){
        try {
            brandService.updateBrand(tbBrand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /*根据ibrandid进行查找*/
    @RequestMapping("/findById")
    public TbBrand findByBrandId(long brandId){
        return brandService.findByBrandId(brandId);
    }

    /*批量删除*/
    @RequestMapping("/delete")
    public Result deleteBrand(long[] ids){
        try {
            brandService.deleteBrand(ids);
            return new Result(true,"删除成功!");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }


}
