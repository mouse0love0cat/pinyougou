package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import com.pinyougou.entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	/*规格选项*/
	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {

		/*插入规格名称*/
		specificationMapper.insert(specification.getSpecification());

		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		/*循环插入规格选项*/
		for (TbSpecificationOption specificationOption:specificationOptionList){
			/*设置规格id*/
			specificationOption.setSpecId(specification.getSpecification().getId());

			tbSpecificationOptionMapper.insert(specificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		/*修改规格名称*/
		TbSpecification spec = specification.getSpecification();
		specificationMapper.updateByPrimaryKey(spec);
		/*修改规格参数*/
		//1 删除现有的规格参数----根据规格id删除
		//创建一个查询条件
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		//获取规格id
		Long id = specification.getSpecification().getId();
		//将规格id放入查询条件中
		criteria.andSpecIdEqualTo(id);
		//根据id删除原有规格
		tbSpecificationOptionMapper.deleteByExample(example);
		//循环遍历现有的规格参数，插入数据
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption option : specificationOptionList) {
			tbSpecificationOptionMapper.insert(option);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		/*获取规格*/
		TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
		/*获取规格参数列表-------条件查询*/
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();

		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		/*根据SpecId进行查询*/
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specificationOptionsList = tbSpecificationOptionMapper.selectByExample(example);
		/*组建返回的specification对象*/
		Specification spec = new Specification();
		spec.setSpecification(specification);
		spec.setSpecificationOptionList(specificationOptionsList);

		return  spec;
	}

	/**
	 * 批量删除
	 * 删除规格的同时  删除关联的规格选项
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除规格
			specificationMapper.deleteByPrimaryKey(id);

			//删除规格选项
			//创建查询条件
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			tbSpecificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	/*/**
	 * @Description 下拉显示所有规格列表
	 * @Author  wangyilong
	 * @Date   2019/10/28 0028 上午 11:55
	 * @Param  []
	 * @Return      java.util.List<java.util.Map>
	 * @Exception
	 *
	 */
	@Override
	public List<Map> selectSpecOptionList() {
		return specificationMapper.selectAllSpecification();
	}
	
}
