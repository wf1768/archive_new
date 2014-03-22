package net.ussoft.archive.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

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
	 * 直接传where、order查
	 * @param where
	 * @param order
	 * @return
	 */
	public List<Sys_tree> list(String where,List<Object> values,String order);
	
	/**
	 * 插入
	 * @param 
	 * @return
	 */
	public String insert(Sys_tree tree);
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
	 * @throws IOException 
	 * @throws SocketException 
	 */
	public String delete(String id) throws SocketException, IOException;
	
	/**
	 * 移动
	 * @param id
	 * @param targetid
	 * @return
	 */
	public String move(String id,String targetid);
	
	/**
	 * 修改排序
	 * @param templet
	 * @return
	 */
	public int sortsave(Sys_tree tree);
	
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
	 * 根据传入的json，增加根节点和图标
	 * @param jsonStr
	 * @param basePath
	 * @return
	 */
	public String createTreeJson(String jsonStr,String basePath);

	/**
	 * 根据账户ID，获取账户的权限  树节点范围
	 * 如果帐户设置了，就读取帐户的，如果没设置，就读取帐户所属组的。
	 * @param accountId		账户ID
	 * @return
	 * */
	public List<Sys_tree> getAuthTree(String accountid);
	
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
