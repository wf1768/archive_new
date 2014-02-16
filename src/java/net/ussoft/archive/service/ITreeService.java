package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;

public interface ITreeService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_tree getById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_tree> list();
	
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
	
	/**
	 * 根据treeid，获取tree节点对应的档案模版实体对象
	 * @param treeid
	 * @return
	 */
	public Sys_templet getTemplet(String treeid);
	
	/**
	 * 根据treeid，获取tree节点对应的档案模版的字段list
	 * @param treeid		树id
	 * @param tabletype		表类型（01 or 02）
	 * @return
	 */
	public List<Sys_templetfield> geTempletfields(String treeid,String tabletype);

}
