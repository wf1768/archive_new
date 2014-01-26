package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.OrgDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.MD5;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements IAccountService {
	
	@Resource
	private AccountDao accountDao;
	@Resource
	private TreeDao treeDao;
	@Resource
	private AccountTreeDao accounttreeDao;
	@Resource
	private OrgDao orgDao;

	@Override
	public Sys_account getById(String id) {
		return accountDao.get(id);
	}
	
	@Override
	public List<Sys_account> list() {
		return accountDao.getAll();
	}

	@Override
	public PageBean<Sys_account> list(String orgid, Integer page) {
		PageBean<Sys_account> p = new PageBean<Sys_account>();
//		p.setPageSize(8);
		p.setPageNo(page);
		
		Sys_account account = new Sys_account();
		account.setOrgid(orgid);
		p = accountDao.search(account, p);
		// p = Info_userDao.searchForMap("select * from Info_user", new ArrayList(),p);
		return p;
	}

	@Transactional("txManager")
	@Override
	public Sys_account insertOne(Sys_account account) {
		accountDao.save(account);
		return account;
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_account account) {
		Sys_account tmp = accountDao.update(account);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#delete(java.lang.String)
	 */
	@Override
	public int delete(String id) {
		return accountDao.del(id);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#login(net.ussoft.archive.model.Sys_account)
	 */
	@Override
	public Sys_account login(Sys_account account) {
		Sys_account tmp = new Sys_account();
		tmp.setAccountcode(account.getAccountcode());
		Sys_account result = accountDao.searchOne(tmp);
		if (result == null ) {
			return null;
		}
		// 将输入的密码与Pojo里的密码MD5后对比，如果不匹配，说明密码不对
		if (!MD5.encode(account.getPassword()).equals(
				result.getPassword())) {
			return null;
		}
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#getAccountTree(java.lang.String)
	 */
	@Override
	public List<Sys_tree> getAccountTree(String accountid) {
		List<Object> values=new ArrayList<Object>();
		values.add(accountid);
		List<Sys_account_tree> accountTreeList = accounttreeDao.search("select * from sys_account_tree where accountid=?", values);
		
		if (accountTreeList.size()  == 0) {
			return null;
		}
		
		values.clear();
		StringBuilder sb=new StringBuilder();
		for (Sys_account_tree sys_account_tree : accountTreeList) {
			values.add(sys_account_tree.getTreeid());
			sb.append("?,");
		}
		CommonUtils.deleteLastStr(sb, ",");
		
		List<Sys_tree> treeList = treeDao.search("select * from sys_tree where treeid in ("+sb.toString()+")", values);
		
		return treeList;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#selectByWhere(net.ussoft.archive.model.Sys_account)
	 */
	@Override
	public List<Sys_account> selectByWhere(PageBean<Sys_account> pageBean,Sys_account account) {
		if (null == account) {
			return null;
		}
		
		pageBean = accountDao.search(account, pageBean);
		List<Sys_account> accountList = pageBean.getList();
		
		return accountList;
	}

}
