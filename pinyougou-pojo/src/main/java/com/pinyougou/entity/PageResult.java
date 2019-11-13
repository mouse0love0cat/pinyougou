package com.pinyougou.entity;

import java.io.Serializable;
import java.util.List;

/*/**
* @Description   分页的实体类
* @Author  wangyilong
* @Date   2019/10/15 0015 下午 2:41
* @Param
* @Return
* @Exception
*
*/
public class PageResult implements Serializable{
	private long total;              //总记录数
	private List data;                //当前页结果

	public PageResult(long total, List data) {
		this.total = total;
		this.data = data;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}
}
