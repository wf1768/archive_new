package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.dao.OrgDao;
import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.dao.OrgownerDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_orgowner;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public Sys_org insertOne(Sys_org org) {
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

	@Override
	public int delete(String id) {
		return orgDao.del(id);
	}
	
	

	@Override
	public ResultInfo getOrgTree(String orgid) {
		List<Object> values=new ArrayList<Object>();
		values.add(orgid);
		List<Sys_org_tree> orgTreeList = orgtreeDao.search("select * from sys_org_tree where orgid=?", values);
		
		ResultInfo info = new ResultInfo(Boolean.TRUE);
		
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
		
		List<Sys_tree> treeList = treeDao.search("select * from sys_tree where treeid in ("+sb.toString()+")", values);
		
		info.setMsg("获取帐户组管理的树节点成功。");
		info.put("list", treeList);
		
		return info;
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
		   strResult += idList.get(i) +",";
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
	
	@Override
	public List<HashMap<String, String>> getChildList(String orgid) {
		List<HashMap<String, String>> resultList = new ArrayList();
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
				resultList.add(childMap);
			}
		}
		
		return resultList;
	}

}
