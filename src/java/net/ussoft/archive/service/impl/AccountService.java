package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.OrgDao;
import net.ussoft.archive.dao.OrgownerDao;
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
	@Resource
	private OrgownerDao orgownerDao;

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#getById(java.lang.String)
	 */
	@Override
	public Sys_account getById(String id) {
		return accountDao.get(id);
	}
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#list()
	 */
	@Override
	public List<Sys_account> list() {
		return accountDao.getAll();
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#list(java.lang.String, java.lang.Integer)
	 */
	@Override
	public PageBean<Sys_account> list(String orgid, Integer page) {
		PageBean<Sys_account> p = new PageBean<Sys_account>();
//		p.setPageSize(8);
		p.setPageNo(page);
		
		Sys_account account = new Sys_account();
		account.setOrgid(orgid);
		p = accountDao.search(account, p);
		return p;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#list(java.lang.String)
	 */
	@Override
	public List<Sys_account> list(String orgid) {
//		return accountDao.getAll("accountcode asc");
		String sql = "select * from sys_account where orgid=? order by accountcode asc";
		List<Object> values = new ArrayList<Object>();
		values.add(orgid);
		return accountDao.search(sql, values);
	}

	@Transactional("txManager")
	@Override
	public Sys_account insert(Sys_account account) {
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
	@Transactional("txManager")
	@Override
	public int delete(String id) {
		//TODO 删除帐户可能后期还要删除其他帐户关联的信息。
		//删除帐户与档案树节点的关联
		String sql = "delete from sys_account_tree where accountid=?";
		List<Object> values = new ArrayList<Object>();
		values.add(id);
		accounttreeDao.del(sql, values);
		//删除帐户作为管理者的，与组织机构的关联
		sql = "delete from sys_orgowner where accountid=?";
		orgownerDao.del(sql, values);
		
		return accountDao.del(id);
	}
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#move(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean move(String id, String targetid) {
		if (id == null || id.equals("") || targetid == null || targetid.equals("")) {
			return false;
		}
		//处理id
		String[] idsStrings = id.split(",");
		
		String strResult = "";
		for(int i=0;i<idsStrings.length;i++){
		   strResult += "'"+idsStrings[i] +"',";
		}
		//去掉最后多出来的逗号。
		strResult = strResult.substring(0,strResult.length()-1);
		
		String sql = "update sys_account set orgid=? where id in ("+strResult+")";
		List<Object> values = new ArrayList<Object>();
		values.add(targetid);
		
		int num = accountDao.update(sql, values);
		
		if (num > 0) {
			return true;
		}
		else {
			return false;
		}
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
