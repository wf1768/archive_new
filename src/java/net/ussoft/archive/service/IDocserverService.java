package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_docserver;

public interface IDocserverService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_docserver selectById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_docserver> list();
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_docserver docserver);
	/**
	 * 更新所有服务器状态为0
	 * @return
	 */
	public int updateState();
	/**
	 * 插入新记录
	 * @param docserver
	 * @return 
	 */
	public Sys_docserver insert(Sys_docserver docserver);
	
	public Sys_docserver selectByWhere(Sys_docserver docserver);
	
	public int delete(String id);
}
