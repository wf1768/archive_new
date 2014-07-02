package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_init;

public interface IInitService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_init selectById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_init> list();
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_init init);
	
	public Sys_init selectByWhere(Sys_init init);

	Sys_init insert(Sys_init init);

}
