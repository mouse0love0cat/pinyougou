package com.pinyougou.entity;

import java.io.Serializable;

/*/**
		* @Description 对返回结果进行封装
		* @Author  wangyilong
		* @Date   2019/10/15 0015 下午 5:21
		* @Param
* @Return
* @Exception
*
		*/
public class Result implements Serializable{


	//是否成功
	private boolean success;
	//保存成功/失败信息
	private String message;
	
	
	
	public Result() {
		super();
	}
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
