package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.util.resule.ResultInfo;

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

	/**
	 * 根据账户ID，获取账户的权限的树节点范围
	 * @param accountId		账户ID
	 * @return
	 * */
	public List<Sys_tree> getAuthTree(String accountId);
	
	/**
	 * 获取帐户的档案树节点范围list
	 * @param accountid
	 * @return
	 */
	public List<Sys_tree> getAccountTree(String accountid);
	
	/**
	 * 获取帐户组的树管理权限范围
	 * @param 
	 * @return
	 */
	public List<Sys_tree> getOrgTree(String orgid);
	
}
