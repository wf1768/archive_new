package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_code;

public interface ICodeService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_code selectById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_code> list();
	/**
	 * 插入
	 * @param org
	 * @return
	 */
	public Sys_code insert(Sys_code code);
	
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_code code);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int delete(String id);
	
	public List<Sys_code> selectByWhere(Sys_code code);
	
	/**
	 * 直接传where、order查
	 * @param where
	 * @param order
	 * @return
	 */
	public List<Sys_code> list(String where,List<Object> values,String order);
}
