package net.ussoft.archive.service;

import java.util.HashMap;
import java.util.List;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_tree;

public interface IAccountService {
	
	/**
	 * 获取帐户
	 * @param id
	 * @return
	 */
	public Sys_account getById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_account> list();
	
	/**
	 * 帐户列表
	 * @return
	 */
	public PageBean<Sys_account> list(String orgid,Integer page);
	/**
	 * 帐户列表，不分页
	 * @param orgid
	 * @return
	 */
	public List<Sys_account> list(String orgid);
	
	/**
	 * 插入新帐户
	 * @param account
	 * @return
	 */
	public Sys_account insert(Sys_account account);
	
	/**
	 * 更新帐户
	 * @param account
	 * @return
	 */
	public int update(Sys_account account);
	/**
	 * 删除帐户
	 * @param id
	 * @return
	 */
	public int delete(String id);
	
	/**
	 * 移动帐户
	 * @param id
	 * @param targetid
	 * @return
	 */
	public Boolean move(String id,String targetid);
	
	/**
	 * 帐户登陆验证
	 * @param account
	 * @return
	 */
	public Sys_account login(Sys_account account);
	
	/**
	 * 获取帐户的档案树节点范围list
	 * @param accountid
	 * @return
	 */
	public List<Sys_tree> getAccountTree(String accountid);
	/**
	 * 根据条件获取帐户列表
	 * @param account
	 * @return
	 */
	public List<Sys_account> selectByWhere(PageBean<Sys_account> pageBean,Sys_account account);
	
	/**
	 * 获取组下的帐户，通过sql语句，直接获取帐户对应的角色
	 * @param orgid
	 * @return
	 */
	public List<HashMap<String, String>> getChildList(String orgid);
	
	/**
	 * 设置帐户的角色
	 * @param id
	 * @param roleid
	 * @return
	 */
	public Boolean saverole(String id,String roleid);
	/**
	 * 移除帐户的角色
	 * @param id
	 * @param roleid
	 * @return
	 */
	public Boolean removerole(String id,String roleid);
	
	/**
	 * 获取当前帐户的树节点关联权限
	 * @param id
	 * @param treeid
	 * @return
	 */
	public Sys_account_tree getTreeAuth(String id,String treeid);
	/**
	 * 为帐户赋权，树节点访问权
	 * @param id
	 * @param treeList
	 * @return
	 */
	public Boolean saveaccounttree(String id,List<String> treeList);
	/**
	 * 保存帐户与树关联的附属权限（电子全文）
	 * @param tmp
	 * @return
	 */
	public Boolean saveTreeAuth(Sys_account_tree tmp);
	
	/**
	 * 保存帐户与树的数据访问权限
	 * @param org_tree
	 * @param tabletype
	 * @param filter
	 * @return
	 */
	public Boolean saveDataAuth(Sys_account_tree account_tree,String tabletype,String filter);
	
	/**
	 * 删除帐户与树的数据访问权限
	 * @param accounttreeid
	 * @param id
	 * @return
	 */
	public Boolean removeDataAuth(String accounttreeid,String id);
	/**
	 * 保存帐户与树的电子文件浏览范围权限
	 * @param tmp
	 * @return
	 */
	public Boolean saveDocAuth(Sys_account_tree tmp);
	
	
}
