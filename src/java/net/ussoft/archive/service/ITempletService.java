package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_templet;


public interface ITempletService {
	
	/**
	 * 获取templet全部信息
	 * @return
	 */
	public List<Sys_templet> list();
	/**
	 * 直接传where、order查
	 * @param where
	 * @param order
	 * @return
	 */
	public List<Sys_templet> list(String where,List<Object> values,String order);
	


}
