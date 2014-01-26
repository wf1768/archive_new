package net.ussoft.archive.service;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_tree;

public interface ITreeService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_tree selectById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public PageBean<Sys_tree> list(Sys_tree tree,PageBean<Sys_tree> p);
	
	/**
	 * 插入
	 * @param 
	 * @return
	 */
	public Sys_tree insertOne(Sys_tree tree);
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_tree tree);
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int delete(String id);

}
