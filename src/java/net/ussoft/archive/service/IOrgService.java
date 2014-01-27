package net.ussoft.archive.service;

import java.util.HashMap;
import java.util.List;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.util.resule.ResultInfo;

public interface IOrgService {
	
	/**
	 * 获取组
	 * @param id
	 * @return
	 */
	public Sys_org getById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_org> list();
	
	/**
	 * 根据条件，获取全部信息
	 * @return
	 */
	public PageBean<Sys_org> list(Sys_org org,PageBean<Sys_org> pageBean);
	/**
	 * 根据条件，获取信息
	 * @return
	 */
	public PageBean<Sys_org> list(String sql,List<Object> values,PageBean<Sys_org> pageBean);
	
	/**
	 * 集团版本，获取当前帐户为owner的组list
	 * @param accountid
	 * @return
	 */
	public List<Sys_org> getorgowner(String accountid);
	
	/**
	 * 集团版本获取当前帐户为owner的所有组list，用这个就能画出树结构，包含不能owner的父节点
	 * @param accountid
	 * @return
	 */
	public List<Sys_org> orgownerList(String accountid);
	
	/**
	 * 集团版，获取页面点击的组的子组,特别增加组的所有者，共前台显示
	 * @param orgid
	 * @return
	 */
	public List<HashMap<String, String>> getChildList(String orgid);
	
	/**
	 * 插入新组
	 * @param org
	 * @return
	 */
	public Sys_org insertOne(Sys_org org);
	
	/**
	 * 更新组
	 * @param org
	 * @return
	 */
	public int update(Sys_org org);
	/**
	 * 删除组
	 * @param id
	 * @return
	 */
	public int delete(String id);
	
	/**
	 * 获取帐户组的树管理权限范围
	 * @param 
	 * @return
	 */
	public ResultInfo getOrgTree(String orgid);

}
