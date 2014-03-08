package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;

import org.springframework.stereotype.Service;


@Service
public class TreeService implements ITreeService {
	
	@Resource
	private TreeDao treeDao;
	@Resource
	private TempletDao templetDao;
	@Resource
	private TableDao tableDao;
	@Resource
	private TempletfieldDao templetfieldDao;
	@Resource
	private AccountTreeDao accounttreeDao;
	
	

	@Resource
	private OrgTreeDao orgtreeDao;
	
	@Override
	public Sys_tree getById(String id) {
		return treeDao.get(id);
	}

	@Override
	public List<Sys_tree> list() {
		return treeDao.getAll("CONVERT(treename USING gbk)");
	}

	@Override
	public Sys_tree insertOne(Sys_tree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Sys_tree tree) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Sys_templet getTemplet(String treeid) {
		//获取tree对象，以获取tree对应的templetid
		Sys_tree tree = treeDao.get(treeid);
		
		if (null == tree || tree.getTempletid().equals("")) {
			return null;
		}
		
		//获取templet实体对象并返回
		return templetDao.get(tree.getTempletid());
	}

	@Override
	public List<Sys_templetfield> geTempletfields(String treeid,
			String tabletype) {
		
		//获取tree对象，来获取templetid
		Sys_tree tree = treeDao.get(treeid);
		String templetid = tree.getTempletid();
		
		//根据templetid 和 tabletype 来或者tableid
		Sys_table table = new Sys_table();
		table.setTempletid(templetid);
		table.setTabletype(tabletype);
		table = tableDao.searchOne(table);
		
		if (null == table) {
			return null;
		}
		
		//根据tableid，获取字段 templetfield的list
		String sql = "select * from sys_templetfield where tableid=? order by sort";
		List<Object> values = new ArrayList<Object>();
		values.add(table.getId());
		List<Sys_templetfield> templetfields = templetfieldDao.search(sql, values);
		return templetfields;
	}

	@Override
	public List<Sys_tree> getAuthTree(String accountId) {
		return null;
	}

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

	@Override
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

}
