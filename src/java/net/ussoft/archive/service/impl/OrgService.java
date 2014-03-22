package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.dao.OrgDao;
import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.dao.OrgownerDao;
import net.ussoft.archive.dao.RoleDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_orgowner;
import net.ussoft.archive.model.Sys_role;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

@Service
public class OrgService implements IOrgService {
	
	@Resource
	private OrgDao orgDao;
	@Resource
	private TreeDao treeDao;
	@Resource
	private OrgTreeDao orgtreeDao;
	@Resource
	private OrgownerDao orgownerDao;
	@Resource
	private AccountDao accountDao ;
	@Resource
	private RoleDao roleDao ;
	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#getById(java.lang.String)
	 */
	@Override
	public Sys_org getById(String id) {
		return orgDao.get(id);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#list()
	 */
	@Override
	public List<Sys_org> list() {
		return orgDao.getAll("CONVERT(orgname USING gbk)");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#list(net.ussoft.archive.model.Sys_org)
	 */
	@Override
	public PageBean<Sys_org> list(Sys_org org,PageBean<Sys_org> pageBean) {
		return orgDao.search(org, pageBean);
	}
	
	@Override
	public PageBean<Sys_org> list(String sql,List<Object> values,PageBean<Sys_org> pageBean) {
		return orgDao.search(sql, values, pageBean);
	}
	
	@Transactional("txManager")
	@Override
	public Sys_org insert(Sys_org org) {
		orgDao.save(org);
		return org;
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_org org) {
		Sys_org tmp = orgDao.update(org);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#delete(java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public int delete(String id) {
		if (id == null || id.equals("")) {
			return 0;
		}
		Sys_org org = orgDao.get(id);
		if (org == null || org.getTreenode().equals("")) {
			return 0;
		}
		
		//删除组下的帐户及子组的全部帐户
		String sql = "";
		sql = "delete from sys_account where orgid in (select id from sys_org where treenode = '"+org.getTreenode()+"' or treenode like '"+org.getTreenode()+"#%')";
		List<Object> values = new ArrayList<Object>();
		accountDao.del(sql, values);
		
		//TODO 因集团版删除组涉及到很多方面，这个等做完帐户管理、角色管理、档案类型管理等再做
		//TODO 要删除组关联的其他信息。（sys_org_tree  sys_orgowner sys_role sys_templet sys_templetfield sys_table）
		
		//删除组及子组的管理者
		sql = "delete from sys_orgowner where orgid in (select id from sys_org where treenode = '"+org.getTreenode()+"' or treenode like '"+org.getTreenode()+"#%')";
		orgownerDao.del(sql, values);
		
		//删除组、子组与档案树的关联
		sql = "delete from sys_org_tree where orgid in (select id from sys_org where treenode = '"+org.getTreenode()+"' or treenode like '"+org.getTreenode()+"#%')";
		orgtreeDao.del(sql, values);
		
		//删除组、以及子组
		sql = "delete from sys_org where treenode = '"+org.getTreenode()+"' or treenode like '"+org.getTreenode()+"#%'";
		int num = orgDao.del(sql, values);
		return num;
	}
	
	public List<Sys_tree> getOrgTree(String orgid) {
		List<Object> values=new ArrayList<Object>();
		values.add(orgid);
		List<Sys_org_tree> orgTreeList = orgtreeDao.search("select * from sys_org_tree where orgid=?", values);
		
		if (orgTreeList.size()  == 0) {
			return null;
		}
		
		values.clear();
		StringBuilder sb=new StringBuilder();
		for (Sys_org_tree sys_org_tree : orgTreeList) {
			values.add(sys_org_tree.getTreeid());
			sb.append("?,");
		}
		CommonUtils.deleteLastStr(sb, ",");
		
		List<Sys_tree> treeList = treeDao.search("select * from sys_tree where id in ("+sb.toString()+")", values);
		
		return treeList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#getorgowner(java.lang.String)
	 */
	@Override
	public List<Sys_org> getorgowner(String accountid) {
		List<Sys_org> orgs = new ArrayList<Sys_org>();
		
		//获取帐户id在sys_orgowner表关联的组id
		Sys_orgowner orgowner = new Sys_orgowner();
		orgowner.setAccountid(accountid);
		List<Sys_orgowner> orgowners = orgownerDao.search(orgowner);
		
		if (orgowner == null || orgowners.size() == 0) {
			return orgs;
		}
		//获取id集合
		List<String> idList = new ArrayList<String>();
		for (Sys_orgowner owner : orgowners) {
			idList.add(owner.getOrgid());
		}
		
		//
		List<Object> values = null;
		
		String strResult = "";
		for(int i=0;i<idList.size();i++){
		   strResult += "'"+idList.get(i) +"',";
		}
		//去掉最后多出来的逗号。
		strResult = strResult.substring(0,strResult.length()-1);
		
		orgs = orgDao.search("select * from sys_org where id in ("+strResult+") order by CONVERT(orgname USING gbk)", values);
		
		return orgs;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#orgownerList(java.lang.String)
	 */
	@Override
	public List<Sys_org> orgownerList(String accountid) {
		List<Object> values = null;
		List<Sys_org> orgs = getorgowner(accountid);
		//获取真正的数据
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append("select * from sys_org where ");
		for (Sys_org org : orgs) {
			//TODO 屏蔽的2句，是保留的，可以全部显示组节点，
//			sbBuffer.append("instr('").append(org.getTreenode()).append("',treenode)>0 or treenode like ").append("'").append(org.getTreenode()).append("#%'");
//			sbBuffer.append(" or ");
			//以下2句，是只显示owner下节点，不包括owner上级节点
			sbBuffer.append("treenode = '").append(org.getTreenode()).append("' or treenode like ").append("'").append(org.getTreenode()).append("#%'");
			sbBuffer.append(" or ");
		}
		sbBuffer.delete(sbBuffer.length()-4, sbBuffer.length());
		sbBuffer.append(" order by CONVERT(orgname USING gbk)");
		
//		select * from sys_org where instr('1#2#3',treenode)>0 or treenode like '1#2#3#%' or instr('1#2#5',treenode)>0 or treenode like '1#2#5#%' 

		orgs.clear();
		orgs = orgDao.search(sbBuffer.toString(), values);
		
		return orgs;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#getChildList(java.lang.String)
	 */
	@Override
	public List<HashMap<String, String>> getChildList(String orgid) {
		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		PageBean<Sys_org> pageBean = new PageBean<Sys_org>();
		Sys_org org = new Sys_org();
		if (orgid != null) {
			org.setParentid(orgid);
		}
		
		pageBean.setIsPage(false);
		pageBean.setOrderBy("CONVERT(orgname USING gbk)");
		pageBean = list(org,pageBean);
		
		
		//将child的组，填充所有者
		if (pageBean.getList().size() > 0) {
			List<Sys_org> list = pageBean.getList();
			for (Sys_org sys_org : list) {
				HashMap<String, String> childMap = new HashMap<String, String>();
				childMap.put("id", sys_org.getId());
				childMap.put("orgname", sys_org.getOrgname());
				//获取当前组的所有者
				List<Object> values = new ArrayList<Object>();
				values.add(sys_org.getId());
				String sql = "select * from sys_account where id in (select accountid from sys_orgowner where orgid=?)";
				List<Sys_account> ownerList = accountDao.search(sql, values);
				String ownerString = "";
				for (Sys_account sys_account : ownerList) {
					ownerString += sys_account.getAccountcode() + " ";
				}
				childMap.put("ownerString", ownerString);
				//获取当前组的角色
				String roleid = sys_org.getRoleid();
				
				if (roleid==null || roleid.equals("")) {
					childMap.put("roleString", "");
				}
				else {
					Sys_role role = roleDao.get(roleid);
					childMap.put("roleString", role.getRolename());
					
				}
				resultList.add(childMap);
			}
		}
		
		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#move(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean move(String id, String targetid) {
		//取出目标父节点对象
		Sys_org targetOrg = orgDao.get(targetid);
		//取出要移动的组对象
		Sys_org org = orgDao.get(id);
		if (targetOrg == null || org == null) {
			return false;
		}
		//获取旧的treenode
		String oldTreenodeString = org.getTreenode();
		//计算新的treenode
		String newTreenodeString = targetOrg.getTreenode() + "#" + org.getOrgindex();

		//更新组的父节点id和treenode
		org.setParentid(targetOrg.getId());
		org.setTreenode(newTreenodeString);
		org = orgDao.update(org);
		
		//更新子节点的treenode

		List<Object> values = new ArrayList<Object>();
		values.add(newTreenodeString + "#");
		values.add(oldTreenodeString + "#%");
		String sql = "update sys_org set treenode = CONCAT(?,orgindex) where treenode like ?";
		
		orgDao.update(sql, values);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#getowner(java.lang.String)
	 */
	@Override
	public List<Sys_account> getowner(String orgid) {
		
		String sql = "select * from sys_account where id in (select accountid from sys_orgowner where orgid=?)";
		List<Object> values = new ArrayList<Object>();
		values.add(orgid);
		List<Sys_account> accounts = accountDao.search(sql, values);
		return accounts;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#getAccounts(java.lang.String)
	 */
	@Override
	public List<Sys_account> getAccounts(String orgid) {
		Sys_account account = new Sys_account();
		account.setOrgid(orgid);
		return accountDao.search(account);
	}

	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#removeowner(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean removeowner(String orgid, String accountid) {
		
		//获取组与管理者对应表里的数据对象
		Sys_orgowner owner = new Sys_orgowner();
		owner.setAccountid(accountid);
		owner.setOrgid(orgid);
		List<Sys_orgowner> orgowners = orgownerDao.search(owner);
		if (orgowners.size() == 1) {
			int num = orgownerDao.del(orgowners.get(0).getId().toString());
			if (num <= 0 ) {
				return false;
			}
		}
		else {
			return false;
		}
		
		return true;
	}

	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#setowner(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean saveowner(String orgid, String accountid) {
		//获取组与管理者对应表里的数据对象
		Sys_orgowner owner = new Sys_orgowner();
		owner.setAccountid(accountid);
		owner.setOrgid(orgid);
		List<Sys_orgowner> orgowners = orgownerDao.search(owner);
		//如果不存在记录,就添加
		if (orgowners != null && orgowners.size() == 0) {
			owner.setId(UUID.randomUUID().toString());
			owner.setAccountid(accountid);
			owner.setOrgid(orgid);
			owner = orgownerDao.save(owner);
			if (owner == null ) {
				return false;
			}
		}
		else {
			return true;
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#setrole(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean saverole(String orgid, String roleid) {
		//获取组对象
		Sys_org org = orgDao.get(orgid);
		
		if (null == org) {
			return false;
		}
		org.setRoleid(roleid);
		org = orgDao.update(org);
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IOrgService#removerole(java.lang.String, java.lang.String)
	 */
	@Transactional("txManager")
	@Override
	public Boolean removerole(String orgid, String roleid) {
		//获取组对象
		Sys_org org = orgDao.get(orgid);
		
		if (null == org) {
			return false;
		}
		org.setRoleid("");
		org = orgDao.update(org);
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public Boolean saveorgtree(String orgid, List<String> treeList) {
		
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		//如果传入的treeList为空，那就清空组对应的树节点.
		if (null == treeList || treeList.size() == 0) {
			sql = "delete from sys_org_tree where orgid=?";
			values.add(orgid);
			orgtreeDao.del(sql, values);
			return true;
		}
		
		//获取组已经存在的树节点访问权
		sql = "select * from sys_org_tree where orgid=?";
		values.clear();
		values.add(orgid);
		List<Sys_org_tree> org_trees = orgtreeDao.search(sql, values);
		
		//获取新加入的。
		List<String> newList = new ArrayList<String>();
		for (int i=0;i < treeList.size();i++) {
			Boolean b = false;
			for (int j = 0;j<org_trees.size();j++) {
				if (treeList.get(i).toString().equals(org_trees.get(j).getTreeid())) {
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
			tmp.add(orgid);
			tmp.add(newList.get(i));
			tmp.add(0);
			tmp.add(0);
			tmp.add(0);
			tmp.add("");
			tmp.add("1");
			values2.add(tmp);
		}
		orgtreeDao.batchAdd("insert into sys_org_tree (id,orgid,treeid,filescan,filedown,fileprint,filter,docauth) values (?,?,?,?,?,?,?,?)", values2);
		
		//处理原来有，本次被移除的关联
		List<String> delList = new ArrayList<String>();
		for (int i=0;i < org_trees.size();i++) {
			Boolean b = false;
			for (int j = 0;j<treeList.size();j++) {
				if (org_trees.get(i).getTreeid().equals(treeList.get(j))) {
					b = true;
					break;
				}
			}
			if (!b) {
				delList.add(org_trees.get(i).getId());
			}
		}
		
		//删除被移除的关联
		if (null != delList && delList.size() >0) {
			orgtreeDao.delByIds(delList);
		}
		
		return true;
	}

	@Override
	public Sys_org_tree getTreeAuth(String orgid, String treeid) {
		Sys_org_tree org_tree = new Sys_org_tree();
		org_tree.setOrgid(orgid);
		org_tree.setTreeid(treeid);
		return orgtreeDao.searchOne(org_tree);
	}

	@Transactional("txManager")
	@Override
	public Boolean saveTreeAuth(Sys_org_tree tmp) {
		//获取对象
		Sys_org_tree oTree = new Sys_org_tree();
		oTree.setOrgid(tmp.getOrgid());
		oTree.setTreeid(tmp.getTreeid());
		Sys_org_tree org_tree = orgtreeDao.searchOne(oTree);
		
		if (null == org_tree) {
			return false;
		}
		
		//获取tree对象，判断是否是夹（F），如果是夹，赋予夹下面的所有与当前组关联的树节点相同的权限
		Sys_tree tree = treeDao.get(org_tree.getTreeid());
		
		if (null == tree) {
			return false;
		}
		
		if (tree.getTreetype().equals("F")) {
			String sql = "select * from sys_org_tree where orgid=? and treeid in (select id from sys_tree where parentid =?)";
			List<Object> values = new ArrayList<Object>();
			values.add(tmp.getOrgid());
			values.add(tree.getId());
			
			List<Sys_org_tree> childList = orgtreeDao.search(sql, values);
			
			for (Sys_org_tree sys_org_tree : childList) {
				sys_org_tree.setFilescan(tmp.getFilescan());
				sys_org_tree.setFiledown(tmp.getFiledown());
				sys_org_tree.setFileprint(tmp.getFileprint());
				
				orgtreeDao.update(sys_org_tree);
			}
		}
		else {
			org_tree.setFilescan(tmp.getFilescan());
			org_tree.setFiledown(tmp.getFiledown());
			org_tree.setFileprint(tmp.getFileprint());
			
			//保存
			orgtreeDao.update(org_tree);
		}
		return true;
	}
	
	@Transactional("txManager")
	private Boolean setDataAuth(Sys_org_tree orgTree,String tabletype,String filter) {
		if (null == orgTree) {
			return false;
		}
		
		String f = orgTree.getFilter();
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
		
		Sys_org_tree record = new Sys_org_tree();
		record.setFilter(JSON.toJSONString(list));
		record.setId(orgTree.getId());
		orgtreeDao.update(record);
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional("txManager")
	@Override
	public Boolean saveDataAuth(Sys_org_tree org_tree, String tabletype,
			String filter) {
		
		if (null == org_tree.getTreeid() || org_tree.getTreeid().equals("")) {
			return false;
		}
		
		//判断Treeid是夹还是节点，如果是夹，则获取当前帐户在该夹下所有有权限的节点赋权
		Sys_tree tree = treeDao.get(org_tree.getTreeid());
		
		if (null == tree) {
			return false;
		}
		
		if (tree.getTreetype().equals("F")) {
			//如果是夹，则获取夹下面的子节点
			String sql = "select * from sys_org_tree where orgid=? and treeid in (select id from sys_tree where parentid =?)";
			List<Object> values = new ArrayList<Object>();
			values.add(org_tree.getOrgid());
			values.add(tree.getId());
			
			List<Sys_org_tree> childList = orgtreeDao.search(sql, values);
			
			for (Sys_org_tree sys_org_tree : childList) {
				setDataAuth(sys_org_tree,tabletype,filter);
			}
		}
		else {
			//获取orgtree关联对象
			Sys_org_tree orgTree = orgtreeDao.searchOne(org_tree);
			return setDataAuth(orgTree,tabletype,filter);
		}
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public Boolean removeDataAuth(String orgtreeid, String id) {
		if (null == orgtreeid || orgtreeid.equals("") || null == id || id.equals("")) {
			return false;
		}
		//获取orgtree对象
		Sys_org_tree org_tree = orgtreeDao.get(orgtreeid);
		
		String f = org_tree.getFilter();
		
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
//			HashMap<String, String> map = (HashMap<String, String>) list.get(i);
			
			if (map.get("id").equals(id)) {
				list.remove(i);
				break;
			}
		}
		
		Sys_org_tree record = new Sys_org_tree();
		record.setFilter(JSON.toJSONString(list));
		record.setId(org_tree.getId());
		orgtreeDao.update(record);
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public Boolean saveDocAuth(Sys_org_tree tmp) {
		//获取对象
		Sys_org_tree oTree = new Sys_org_tree();
		oTree.setOrgid(tmp.getOrgid());
		oTree.setTreeid(tmp.getTreeid());
		Sys_org_tree org_tree = orgtreeDao.searchOne(oTree);
		
		if (null == org_tree) {
			return false;
		}
		
		//获取tree对象，判断是否是夹（F），如果是夹，赋予夹下面的所有与当前组关联的树节点相同的权限
		Sys_tree tree = treeDao.get(org_tree.getTreeid());
		
		if (null == tree) {
			return false;
		}
		
		if (tree.getTreetype().equals("F")) {
			String sql = "select * from sys_org_tree where orgid=? and treeid in (select id from sys_tree where parentid =?)";
			List<Object> values = new ArrayList<Object>();
			values.add(tmp.getOrgid());
			values.add(tree.getId());
			
			List<Sys_org_tree> childList = orgtreeDao.search(sql, values);
			
			for (Sys_org_tree sys_org_tree : childList) {
				sys_org_tree.setDocauth(tmp.getDocauth());
				orgtreeDao.update(sys_org_tree);
			}
		}
		else {
			org_tree.setDocauth(tmp.getDocauth());
			//保存
			orgtreeDao.update(org_tree);
		}
		return true;
	}

}
