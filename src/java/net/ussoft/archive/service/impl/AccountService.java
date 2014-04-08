package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.CodeDao;
import net.ussoft.archive.dao.ConfigDao;
import net.ussoft.archive.dao.OrgDao;
import net.ussoft.archive.dao.OrgownerDao;
import net.ussoft.archive.dao.RoleDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_role;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.MD5;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

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
	@Resource
	private RoleDao roleDao;
	@Resource
	private TableDao tableDao;
	@Resource
	private ConfigDao configDao;
	@Resource
	private TempletfieldDao templetfieldDao;
	@Resource
	private CodeDao codeDao;
	

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
//		select a.id,a.accountcode,r.rolename from sys_account as a left join sys_role as r on a.roleid = r.id
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
		
		//删除帐户私有配置
		sql = "delete from sys_config where accountid=?";
		configDao.del(sql, values);
		
		//删除帐户私有字段
		//首先获取字段list，删除字段代码
		sql = "select * from sys_templetfield where iscode=1 and accountid=?";
		List<Sys_templetfield> fields = templetfieldDao.search(sql, values);
		
		if (null != fields && fields.size() > 0) {
			for (Sys_templetfield field : fields) {
				sql = "delete from sys_code where templetfieldid=?";
				values.clear();
				values.add(field.getId());
				codeDao.del(sql, values);
			}
		}
		
		sql = "delete from sys_templetfield where accountid=?";
		values.clear();
		values.add(id);
		templetfieldDao.del(sql, values);
		
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
		
		List<Sys_tree> treeList = treeDao.search("select * from sys_tree where id in ("+sb.toString()+")", values);
		
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
	@Override
	public List<HashMap<String, String>> getChildList(String orgid) {
		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		PageBean<Sys_account> pageBean = new PageBean<Sys_account>();
		Sys_account account = new Sys_account();
		if (orgid != null) {
			account.setOrgid(orgid);
		}
		
		pageBean.setIsPage(false);
		pageBean.setOrderBy("accountcode asc");
		pageBean = accountDao.search(account, pageBean);
		
		
		//将child的组，填充所有者
		if (pageBean.getList().size() > 0) {
			List<Sys_account> list = pageBean.getList();
			for (Sys_account sys_account : list) {
				HashMap<String, String> childMap = new HashMap<String, String>();
				childMap.put("id", sys_account.getId());
				childMap.put("accountcode", sys_account.getAccountcode());
				childMap.put("accountstate", sys_account.getAccountstate().toString());
				childMap.put("accountmemo", sys_account.getAccountmemo());
				//获取当前组的角色
				String roleid = sys_account.getRoleid();
				
				if (roleid==null || roleid.equals("")) {
					childMap.put("rolename", "");
				}
				else {
					Sys_role role = roleDao.get(roleid);
					childMap.put("rolename", role.getRolename());
					
				}
				resultList.add(childMap);
			}
		}
		
		return resultList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#saverole(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean saverole(String id, String roleid) {
		//获取帐户对象
		Sys_account account = accountDao.get(id);
		
		if (null == account) {
			return false;
		}
		account.setRoleid(roleid);
		accountDao.update(account);
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IAccountService#removerole(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean removerole(String id, String roleid) {
		//获取帐户对象
		Sys_account account = accountDao.get(id);
		
		if (null == account) {
			return false;
		}
		account.setRoleid("");
		accountDao.update(account);
		
		return true;
	}
	
	@Override
	public Sys_account_tree getTreeAuth(String id, String treeid) {
		Sys_account_tree account_tree = new Sys_account_tree();
		account_tree.setAccountid(id);
		account_tree.setTreeid(treeid);
		return accounttreeDao.searchOne(account_tree);
	}
	
	@Transactional("txManager")
	@Override
	public Boolean saveaccounttree(String id, List<String> treeList) {
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		//如果传入的treeList为空，那就清空帐户对应的树节点.
		if (null == treeList || treeList.size() == 0) {
			sql = "delete from sys_account_tree where accountid=?";
			values.add(id);
			accounttreeDao.del(sql, values);
			return true;
		}
		
		//获取帐户已经存在的树节点访问权
		sql = "select * from sys_account_tree where accountid=?";
		values.clear();
		values.add(id);
		List<Sys_account_tree> account_trees = accounttreeDao.search(sql, values);
		
		//获取新加入的。
		List<String> newList = new ArrayList<String>();
		for (int i=0;i < treeList.size();i++) {
			Boolean b = false;
			for (int j = 0;j<account_trees.size();j++) {
				if (treeList.get(i).toString().equals(account_trees.get(j).getTreeid())) {
					b = true;
					break;
				}
			}
			if (!b) {
				newList.add(treeList.get(i).toString());
			}
		}
		//将新加入的，保存到数据库
		List<List<Object>> values2 = new ArrayList<List<Object>>();
		for (int i = 0; i < newList.size(); i++) {
			List<Object> tmp = new ArrayList<Object>();
			tmp.add(UUID.randomUUID().toString());
			tmp.add(id);
			tmp.add(newList.get(i));
			tmp.add(0);
			tmp.add(0);
			tmp.add(0);
			tmp.add("");
			tmp.add("1");
			values2.add(tmp);
		}
		accounttreeDao.batchAdd("insert into sys_account_tree (id,accountid,treeid,filescan,filedown,fileprint,filter,docauth) values (?,?,?,?,?,?,?,?)", values2);
		
		//处理原来有，本次被移除的关联
		List<String> delList = new ArrayList<String>();
		for (int i=0;i < account_trees.size();i++) {
			Boolean b = false;
			for (int j = 0;j<treeList.size();j++) {
				if (account_trees.get(i).getTreeid().equals(treeList.get(j))) {
					b = true;
					break;
				}
			}
			if (!b) {
				delList.add(account_trees.get(i).getId());
			}
		}
		
		//删除被移除的关联
		if (null != delList && delList.size() >0) {
			accounttreeDao.delByIds(delList);
		}
		
		return true;
	}
	
	@Transactional("txManager")
	@Override
	public Boolean saveTreeAuth(Sys_account_tree tmp) {
		//获取对象
		Sys_account_tree aTree = new Sys_account_tree();
		aTree.setAccountid(tmp.getAccountid());
		aTree.setTreeid(tmp.getTreeid());
		Sys_account_tree account_tree = accounttreeDao.searchOne(aTree);
		
		if (null == account_tree) {
			return false;
		}
		
		//获取tree对象，判断是否是夹（F），如果是夹，赋予夹下面的所有与当前帐户关联的树节点相同的权限
		Sys_tree tree = treeDao.get(account_tree.getTreeid());
		
		if (null == tree) {
			return false;
		}
		
		if (tree.getTreetype().equals("F")) {
			String sql = "select * from sys_account_tree where accountid=? and treeid in (select id from sys_tree where parentid =?)";
			List<Object> values = new ArrayList<Object>();
			values.add(tmp.getAccountid());
			values.add(tree.getId());
			
			List<Sys_account_tree> childList = accounttreeDao.search(sql, values);
			
			for (Sys_account_tree sys_account_tree : childList) {
				sys_account_tree.setFilescan(tmp.getFilescan());
				sys_account_tree.setFiledown(tmp.getFiledown());
				sys_account_tree.setFileprint(tmp.getFileprint());
				
				accounttreeDao.update(sys_account_tree);
			}
		}
		else {
			account_tree.setFilescan(tmp.getFilescan());
			account_tree.setFiledown(tmp.getFiledown());
			account_tree.setFileprint(tmp.getFileprint());
			
			//保存
			accounttreeDao.update(account_tree);
		}
		return true;
	}
	
	@Transactional("txManager")
	private Boolean setDataAuth(Sys_account_tree accountTree,String tabletype,String filter) {
		if (null == accountTree) {
			return false;
		}
		
		String f = accountTree.getFilter();
		HashMap<String, String> tmpMap = new HashMap<String, String>();
		//转化参数
		if (filter == null || filter.equals("")) {
			return false;
		}
		else {
			tmpMap = (HashMap<String, String>) JSON.parseObject(filter,new HashMap<String,String>().getClass());  
		}
		List list = new ArrayList();
		
		
		//如果已经设置过
		try {
			if (f != null && f.length() > 0) {
				list = (List) JSON.parseObject(f, new ArrayList().getClass());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		tmpMap.put("id", UUID.randomUUID().toString());
		tmpMap.put("tableType", tabletype);
		
		list.add(tmpMap);
		
		Sys_account_tree record = new Sys_account_tree();
		record.setFilter(JSON.toJSONString(list));
		record.setId(accountTree.getId());
		accounttreeDao.update(record);
		
		return true;
	}
	
	@Transactional("txManager")
	@Override
	public Boolean saveDataAuth(Sys_account_tree account_tree,
			String tabletype, String filter) {
		if (null == account_tree.getTreeid() || account_tree.getTreeid().equals("")) {
			return false;
		}
		
		//判断Treeid是夹还是节点，如果是夹，则获取当前帐户在该夹下所有有权限的节点赋权
		Sys_tree tree = treeDao.get(account_tree.getTreeid());
		
		if (null == tree) {
			return false;
		}
		
		if (tree.getTreetype().equals("F")) {
			//如果是夹，则获取夹下面的子节点
			String sql = "select * from sys_account_tree where accountid=? and treeid in (select id from sys_tree where parentid =?)";
			List<Object> values = new ArrayList<Object>();
			values.add(account_tree.getAccountid());
			values.add(tree.getId());
			
			List<Sys_account_tree> childList = accounttreeDao.search(sql, values);
			
			for (Sys_account_tree sys_account_tree : childList) {
				setDataAuth(sys_account_tree,tabletype,filter);
			}
		}
		else {
			//获取orgtree关联对象
			Sys_account_tree accountTree = accounttreeDao.searchOne(account_tree);
			return setDataAuth(accountTree,tabletype,filter);
		}
		
		return true;
	}
	
	@Transactional("txManager")
	@Override
	public Boolean removeDataAuth(String accounttreeid, String id) {
		if (null == accounttreeid || accounttreeid.equals("") || null == id || id.equals("")) {
			return false;
		}
		//获取orgtree对象
		Sys_account_tree account_tree = accounttreeDao.get(accounttreeid);
		
		String f = account_tree.getFilter();
		
		if (null == f || f.equals("")) {
			return false;
		}
		
		List list = new ArrayList();
		//如果已经设置过
		try {
			list = (List) JSON.parseObject(f, new ArrayList().getClass());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		for (int i=0;i<list.size();i++) {
			HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(list.get(i).toString(),new HashMap<String,String>().getClass());
			
			if (map.get("id").equals(id)) {
				list.remove(i);
				break;
			}
		}
		
		Sys_account_tree record = new Sys_account_tree();
		record.setFilter(JSON.toJSONString(list));
		record.setId(account_tree.getId());
		accounttreeDao.update(record);
		
		return true;
	}
	
	@Transactional("txManager")
	@Override
	public Boolean saveDocAuth(Sys_account_tree tmp) {
		//获取对象
		Sys_account_tree aTree = new Sys_account_tree();
		aTree.setAccountid(tmp.getAccountid());
		aTree.setTreeid(tmp.getTreeid());
		Sys_account_tree account_tree = accounttreeDao.searchOne(aTree);
		
		if (null == account_tree) {
			return false;
		}
		
		//获取tree对象，判断是否是夹（F），如果是夹，赋予夹下面的所有与当前组关联的树节点相同的权限
		Sys_tree tree = treeDao.get(account_tree.getTreeid());
		
		if (null == tree) {
			return false;
		}
		
		if (tree.getTreetype().equals("F")) {
			String sql = "select * from sys_account_tree where accountid=? and treeid in (select id from sys_tree where parentid =?)";
			List<Object> values = new ArrayList<Object>();
			values.add(tmp.getAccountid());
			values.add(tree.getId());
			
			List<Sys_account_tree> childList = accounttreeDao.search(sql, values);
			
			for (Sys_account_tree sys_account_tree : childList) {
				sys_account_tree.setDocauth(tmp.getDocauth());
				accounttreeDao.update(sys_account_tree);
			}
		}
		else {
			account_tree.setDocauth(tmp.getDocauth());
			//保存
			accounttreeDao.update(account_tree);
		}
		return true;
	}
	
}
