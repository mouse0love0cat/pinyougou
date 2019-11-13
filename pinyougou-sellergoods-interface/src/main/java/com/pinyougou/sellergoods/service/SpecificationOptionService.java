package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbSpecificationOption;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationOptionService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecificationOption> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSpecificationOption specificationOption);
	
	
	/**
	 * 修改
	 */
	public void update(TbSpecificationOption specificationOption);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSpecificationOption findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecificationOption specificationOption, int pageNum, int pageSize);

	/*/**
	* @Description 根据规格id查询相应的规格列表
	* @Author  wangyilong
	* @Date   2019/10/28 0028 上午 11:42
	* @Param  [specId]
	* @Return      java.util.List<com.pinyougou.pojo.TbSpecificationOption>
	* @Exception
	*
	*/
	public List<TbSpecificationOption> findSpecifcationOptionList(long specId);


	
}
