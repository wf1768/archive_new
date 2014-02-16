package net.ussoft.archive.service;

import java.util.HashMap;
import java.util.List;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.model.Sys_org_tree;
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
	 * 集团版本，获取当前帐户为owner的组list.仅仅是帐户id与组id对应的。不包含上级和下级。
	 * @param accountid
	 * @return
	 */
	public List<Sys_org> getorgowner(String accountid);
	
	/**
	 * 集团版本获取当前帐户为owner的所有组list，包括下级单位，用这个就能画出树结构，不包含不能owner的父节点
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
	 * 集团版，或者组的所有者
	 * @param orgid
	 * @return
	 */
	public List<Sys_account> getowner(String orgid);
	
	/**
	 * 获取组下的帐户。
	 * @param orgid
	 * @return
	 */
	public List<Sys_account> getAccounts(String orgid);
	
	/**
	 * 插入新组
	 * @param org
	 * @return
	 */
	public Sys_org insert(Sys_org org);
	
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
	
	/**
	 * 移动组到新的父节点下
	 * @param id
	 * @param targetid
	 * @return
	 */
	public Boolean move(String id,String targetid);
	
	/**
	 * 移除组的管理者。删除组id和帐户id的对应关系
	 * @param orgid
	 * @param accountid
	 * @return
	 */
	public Boolean removeowner(String orgid,String accountid);
	/**
	 * 设置组的管理者。将组id与帐户id对应表保存
	 * @param orgid
	 * @param accountid
	 * @return
	 */
	public Boolean setowner(String orgid,String accountid);
	
	/**
	 * 设置组的角色
	 * @param orgid
	 * @param roleid
	 * @return
	 */
	public Boolean setrole(String orgid,String roleid);
	/**
	 * 移除组的角色
	 * @param orgid
	 * @param roleid
	 * @return
	 */
	public Boolean removerole(String orgid,String roleid);
	/**
	 * 为组赋权，树节点访问权
	 * @param orgid
	 * @param treeList
	 * @return
	 */
	public Boolean setorgtree(String orgid,List<String> treeList);
	
	/**
	 * 获取当前组的树节点关联权限
	 * @param orgid
	 * @param treeid
	 * @return
	 */
	public Sys_org_tree getTreeAuth(String orgid,String treeid);
	/**
	 * 保存组与树关联的附属权限（电子全文）
	 * @param org_tree
	 * @return
	 */
	public Boolean setTreeAuth(Sys_org_tree tmp);
	
	/**
	 * 保存组与树的数据访问权限
	 * @param org_tree
	 * @param tabletype
	 * @param filter
	 * @return
	 */
	public Boolean saveDataAuth(Sys_org_tree org_tree,String tabletype,String filter);
	
}
