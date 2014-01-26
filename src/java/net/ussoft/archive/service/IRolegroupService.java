package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_rolegroup;

public interface IRolegroupService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_rolegroup selectById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_rolegroup> list();
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_rolegroup rolegroup);
	
	/**
	 * 插入新记录
	 * @param docserver
	 * @return 
	 */
	public Sys_rolegroup insert(Sys_rolegroup rolegroup);
	
	/**
	 * 根据条件获取数据
	 * @param role
	 * @return
	 */
	public Sys_rolegroup selectByWhere(Sys_rolegroup rolegroup);
	
	/**
	 * 删除一条记录
	 * @param id
	 * @return
	 */
	public int delete(String id);
}
