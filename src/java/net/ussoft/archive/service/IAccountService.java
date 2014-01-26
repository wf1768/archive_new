package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
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
	 * 插入新帐户
	 * @param account
	 * @return
	 */
	public Sys_account insertOne(Sys_account account);
	
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

}
