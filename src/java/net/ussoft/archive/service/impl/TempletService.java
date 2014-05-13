package net.ussoft.archive.service.impl;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.DocDao;
import net.ussoft.archive.dao.DocserverDao;
import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ITempletService;
import net.ussoft.archive.util.FileOperate;
import net.ussoft.archive.util.FtpUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TempletService implements ITempletService {
	
	@Resource
	private TempletDao templetDao;
	@Resource
	private TreeDao treeDao;
	@Resource
	private TableDao tableDao;
	@Resource
	private TempletfieldDao templetfieldDao;
	@Resource
	private AccountTreeDao accountTreeDao;
	@Resource
	private OrgTreeDao orgTreeDao;
	@Resource
	private DocDao docDao;
	@Resource
	private DocserverDao docserverDao;
	
	@Override
	public List<Sys_templet> list() {
//		return templetDao.getAll("CONVERT(templetname USING gbk)");
		return templetDao.getAll(" sort asc");
	}

	@Override
	public List<Sys_templet> list(String where,List<Object> values,String order) {
		String sql = "select * from sys_templet";
		if (null != where && !where.equals("")) {
			sql += where;
		}
		
		if (null != order && !order.equals("")) {
			sql += order;
		}
		
		return templetDao.search(sql, values);
	}

	@Transactional("txManager")
	@Override
	public String insert(Sys_templet templet,String copyid) {
		String result = "success";
		
		//查sort最大值＋1赋予新字段sort
		List<Object> values = new ArrayList<Object>();
		String sql = "select max(sort) from sys_templet where parentid=?";
		values.clear();
		values.add(templet.getParentid());
		Long lo = templetDao.getLong(sql, values);
		
		templet.setSort(lo.intValue()+1);
		
		Boolean b = createTemplet(templet,copyid);
		
		if (!b) {
			return "failure";
		}
		
		return result;
	}
	
	/**
	 * 创建档案类型
	 * @param templet			要保存的templet实体对象
	 * @param copyTempletid		如果是创建档案类型（templettype 不为“T”），会有这个参数。新创建档案类型参照模版
	 * @return
	 */
	private Boolean createTemplet(Sys_templet templet,String copyTempletid) {
		if (null == templet) {
			return false;
		}
		
		List<Object> values = new ArrayList<Object>();
		
		//插入tree表
		Sys_tree tree = new Sys_tree();
		String tmpid = UUID.randomUUID().toString();
		tree.setId(tmpid);
		
		tree.setTempletid(templet.getId());
		tree.setTreename(templet.getTempletname());
		String treenode = tmpid.substring(tmpid.length()-8, tmpid.length());
		//获取tree的parentid
		values.clear();
		values.add(templet.getParentid());
		List<Sys_tree> parentTrees = treeDao.search("select * from sys_tree where templetid=?", values);
		
		if (null != parentTrees && parentTrees.size() > 0) {
			tree.setParentid(parentTrees.get(0).getId());
		}
		else {
			tree.setParentid("0");
		}
		
		if (templet.getTemplettype().equals("T")) {
			tree.setTreetype("T");
		}
		else {
			tree.setTreetype("F");
		}
		
//		//查sort最大值＋1赋予新字段sort
//		
//		String sql = "select max(sort) from sys_tree where parentid=?";
//		values.clear();
//		values.add(tree.getParentid());
//		Long lo = treeDao.getLong(sql, values);
		
//		tree.setSort(lo.intValue()+1);
		tree.setSort(templet.getSort());
		
		if (templet.getParentid().equals("0")) {
			tree.setTreenode("0#"+treenode);
		}
		else {
			//获取父节点的对象
			values.clear();
			values.add(templet.getParentid());
			List<Sys_tree> tmpTrees = treeDao.search("select * from sys_tree where templetid=?", values);
			if (null == tmpTrees || tmpTrees.size() != 1) {
				return false;
			}
			tree.setTreenode(tmpTrees.get(0).getTreenode()+"#"+treenode);
		}
		
		//如果不是夹（是档案类型），则创建真实表（sys_table、真实表、）
		if (!templet.getTemplettype().equals("T")) {
			if (null == copyTempletid || copyTempletid.equals("")) {
				return false;
			}
			//获取参照模版的实体表名
			Sys_templet copyTemplet = templetDao.get(copyTempletid);
			if (null == copyTemplet) {
				return false;
			}
			
			Sys_table copyTable = new Sys_table();
			copyTable.setTempletid(copyTemplet.getId());
			List<Sys_table> tables = tableDao.search(copyTable);
			if (tables.size() == 0) {
				return false;
			}
			for (Sys_table sys_table : tables) {
				//复制表结构
				//CREATE TABLE  sys_table2 LIKE sys_table
				StringBuffer sb = new StringBuffer();
				sb.append("create table ");
				//生成新的表名
				String newTablename = "";
				//生成sys_table表插入时的label内容
				String label = "";
				//标准档案
				if (templet.getTemplettype().equals("CA") || templet.getTemplettype().equals("A")) {
					newTablename = "A_" + treenode + "_" + sys_table.getTabletype();
					if (sys_table.getTabletype().equals("01")) {
						label = templet.getTempletname() + "_案卷级";
					}
					else {
						label = templet.getTempletname() + "_文件级";
					}
				}
				else if (templet.getTemplettype().equals("CF") || templet.getTemplettype().equals("F")) {
					newTablename = "F_" + treenode + "_" + sys_table.getTabletype();
					label = templet.getTempletname() + "_文件级";
				}
				else if (templet.getTemplettype().equals("CP") || templet.getTemplettype().equals("P")) {
					newTablename = "P_" + treenode + "_" + sys_table.getTabletype();
					if (sys_table.getTabletype().equals("01")) {
						label = templet.getTempletname() + "_案卷级";
					}
					else {
						label = templet.getTempletname() + "_文件级";
					}
				}
				else {
					return false;
				}
				sb.append(newTablename);
				sb.append(" like ");
				sb.append(sys_table.getTablename());
				
				tableDao.add(sb.toString(), null);
				
				//复制完表，插入sys_table表
				Sys_table tmp = new Sys_table();
				tmp.setId(UUID.randomUUID().toString());
				tmp.setTablelabel(label);
				tmp.setTablename(newTablename);
				tmp.setTabletype(sys_table.getTabletype());
				tmp.setTempletid(templet.getId());
				tableDao.save(tmp);
				
				//copy字段（sys_templetfield表数据）
				Sys_templetfield sqlField = new Sys_templetfield();
				sqlField.setTableid(sys_table.getId());
				List<Sys_templetfield> templetfields = templetfieldDao.search(sqlField);
				
				for (Sys_templetfield sys_templetfield : templetfields) {
					sys_templetfield.setId(UUID.randomUUID().toString());
					sys_templetfield.setTableid(tmp.getId());
					templetfieldDao.save(sys_templetfield);
				}
				
			}
		}
		treeDao.save(tree);
		templetDao.save(templet);
		return true;
	}

	@Override
	public Sys_templet getByid(String id) {
		return templetDao.get(id);
	}

	/**
	 * 更新档案类型
	 */
	@Transactional("txManager")
	@Override
	public int update(Sys_templet templet) {
		
		//传入的templet只有id和templetname，先获取实体类
		Sys_templet templet_tmp = templetDao.get(templet.getId());
		
		//更新tree里的相关
		Sys_tree tree = new Sys_tree();
		tree.setTempletid(templet.getId());
		tree = treeDao.searchOne(tree);
		if (null == tree) {
			return 0;
		}
		Sys_tree tmp = new Sys_tree();
		tmp.setId(tree.getId());
		tmp.setTreename(templet.getTempletname());
		treeDao.update(tmp);
		
		//更新table里的数据.如果档案类型为T的是夹，不需要修改table里的相关数据
		if (!templet_tmp.getTemplettype().equals("T")) {
			Sys_table table = new Sys_table();
			table.setTempletid(templet.getId());
			List<Sys_table> tables = tableDao.search(table);
			if (tables.size() == 0) {
				return 0;
			}
			
			for (Sys_table sys_table : tables) {
				String str = "";
				if (sys_table.getTabletype().equals("01")) {
					if (templet_tmp.getTemplettype().equals("F")) {
						str = "文件级";
					}
					else {
						str = "案卷级";
					}
				}
				else {
					str = "文件级";
				}
				sys_table.setTablelabel(templet.getTempletname() + "_" + str);
				tableDao.update(sys_table);
			}
		}
		
		Sys_templet result = templetDao.update(templet);
		if (null == result) {
			return 0;
		}
		return 1;
	}

	@Transactional("txManager")
	@Override
	public String move(String id, String targetid) {
		if (id == null || id.equals("") || targetid == null || targetid.equals("")) {
			return "failure";
		}
		List<Object> values = new ArrayList<Object>();
		String sql = "";
		//处理id
		String[] idsStrings = id.split(",");
		
		if (!targetid.equals("0")) {
			//首先取出第一个等待移动的id，与目标id，这2个对象对应的tree的treenode来判断是否是移动到本级或下级。如果是就拒绝移动
			values.clear();
			values.add(idsStrings[0]);
			List<Sys_tree> actionTrees = treeDao.search("select * from sys_tree where templetid=?", values);
			
			values.clear();
			values.add(targetid);
			List<Sys_tree> targetTrees = treeDao.search("select * from sys_tree where templetid=?", values);
			Boolean b = targetTrees.get(0).getTreenode().contains(actionTrees.get(0).getTreenode());
			
			if (b) {
				return "error";
			}
		}
		
		
		
		//单个处理
		for(int i=0;i<idsStrings.length;i++){
			//实体
			Sys_templet tmpTemplet = getByid(idsStrings[i]);
			
			Sys_templet templet = new Sys_templet();
			templet.setId(idsStrings[i]);
			templet.setParentid(targetid);
			
			//查sort最大值＋1赋予新sort
			
			sql = "select max(sort) from sys_templet where parentid=?";
			values.clear();
			values.add(targetid);
			Long lo = templetDao.getLong(sql, values);
			
			templet.setSort(lo.intValue()+1);
			
			//处理tree相同的parentid和sort
			Sys_tree tree = new Sys_tree();
			tree.setTempletid(templet.getId());
			if (tmpTemplet.getTemplettype().equals("T")) {
				tree.setTreetype("T");
			}
			else {
				tree.setTreetype("F");
			}
			tree = treeDao.searchOne(tree);
			if (null == tree) {
				return "failure";
			}
			Sys_tree tmp = new Sys_tree();
			tmp.setId(tree.getId());
			//获取tree的parentid
			values.clear();
			values.add(targetid);
			List<Sys_tree> parentTrees = treeDao.search("select * from sys_tree where templetid=?", values);
			
			if (null != parentTrees && parentTrees.size() > 0) {
				tmp.setParentid(parentTrees.get(0).getId());
			}
			else {
				tmp.setParentid("0");
			}
			
//			//查sort最大值＋1赋予新字段sort
//			values.clear();
//			sql = "select max(sort) from sys_tree where parentid=?";
//			values.clear();
//			values.add(tmp.getParentid());
//			lo = treeDao.getLong(sql, values);
//			
//			tmp.setSort(lo.intValue()+1);
			tmp.setSort(templet.getSort());
			
			//更改tree的treenode
			String treenode = tmp.getId().substring(tmp.getId().length()-8, tmp.getId().length());
			if (tmp.getParentid().equals("0")) {
				tmp.setTreenode("0#"+treenode);
			}
			else {
				tmp.setTreenode(parentTrees.get(0).getTreenode()+"#"+treenode);
			}
			
			treeDao.update(tmp);
			
			//更新tree的所有下级的treenode
//			update sys_tree_copy set treenode=concat('0#e4c577be#',right(id,8)) where parentid='44f9e61a-d97a-4357-94b4-7936e4c577be'
			sql = "update sys_tree set treenode=concat(?,right(id,8)) where parentid=?";
			values.clear();
			values.add(tmp.getTreenode()+"#");
			values.add(tmp.getId());
			treeDao.update(sql, values);
			
			templetDao.update(templet);
		}
		
		return "success";
	}

	@Transactional("txManager")
	@Override
	public int sortsave(Sys_templet templet) {
		//更新对应tree的sort
		Sys_templet tmp = templetDao.get(templet.getId());
		
		//获取tree对象
		Sys_tree tree = new Sys_tree();
		tree.setTempletid(tmp.getId());
		if (tmp.getTemplettype().equals("T")) {
			tree.setTreetype("T");
		}
		else {
			tree.setTreetype("F");
		}
		tree = treeDao.searchOne(tree);
		if (null == tree) {
			return 0;
		}
		Sys_tree updateTree = new Sys_tree();
		updateTree.setId(tree.getId());
		updateTree.setSort(templet.getSort());
		treeDao.update(updateTree);
		
		templetDao.update(templet);
		return 1;
	}
	
	@Transactional("txManager")
	@Override
	public String delete(String id) throws SocketException, IOException {
		
		/*
		 * 删除档案类型：
		 * 1、删除table
		 * 	（1）删除真实表档案数据挂接的电子全文。
		 * 	（2）删除sys_doc表里的对应数据
		 * 	（3）删除真实表
		 * 	（4）删除field表字段，根据tableid
		 * 	（5）删除sys_table表记录
		 * 2、删除tree
		 * 	（1）删除帐户组、帐户对应的tree的访问权限
		 * 	（2）删除tree表数据
		 * 3、删除templet
		 * 
		 */
		
		String result = "failure";
		
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		//判断要删除的档案类型，如果是档案类型，继续删除。
		//如果是档案类型夹，查是否有下级，如果有，就返回提示，需要清空下级，才能删除
		Sys_templet templet = templetDao.get(id);
		if (templet.getTemplettype().equals("T")) {
			sql = "select * from sys_templet where parentid=?";
			values.clear();
			values.add(templet.getId());
			List<Sys_templet> t = templetDao.search(sql, values);
			if (t.size() > 0) {
				return "error";
			}
		}
		
		//读取tree对象，获取treeid，用来删除account_tree org_tree表的权限关联数据
		sql = "select * from sys_tree where templetid=?";
		values.clear();
		values.add(templet.getId());
		List<Sys_tree> trees = treeDao.search(sql, values);
		
		for (Sys_tree sys_tree : trees) {
			sql = "delete from sys_account_tree where treeid=?";
			values.clear();
			values.add(sys_tree.getId());
			accountTreeDao.del(sql, values);
			
			sql = "delete from sys_org_tree where treeid=?";
			orgTreeDao.del(sql, values);
			
		}
		
		//删除tree里的数据
		sql = "delete from sys_tree where templetid=?";
		values.clear();
		values.add(templet.getId());
		treeDao.del(sql, values);
		
		//如果删除的是T（档案类型夹）。只删除templet及tree中的数据就可以了。不涉及table、真是表、数据
		if (!templet.getTemplettype().equals("T")) {
			//如果是档案类型，就要删除好多其他的。例如table、数据、doc等
			//1  删除档案挂接的电子全文
			sql = "select * from sys_table where templetid=?";
			values.clear();
			values.add(templet.getId());
			List<Sys_table> tables = tableDao.search(sql, values);
			
			for (Sys_table sys_table : tables) {
				//删除档案数据挂接的电子文件
				sql = "select * from sys_doc where tableid=?";
				values.clear();
				values.add(sys_table.getId());
				List<Sys_doc> docs = docDao.search(sql, values);
				if (null != docs && docs.size() > 0) {
					for (Sys_doc doc : docs) {
						
						//因档案复制粘贴功能，将造成电子文件共用的情况，所以在删除时，判断是否表中有存在多个
						sql = "select * from sys_doc where docnewname=?";
						values.clear();
						values.add(doc.getDocnewname());
						
						List<Sys_doc> tmpList = docDao.search(sql, values);
						
						if (null != tmpList && tmpList.size() == 1) {
							//获取doc的server
							Sys_docserver docserver = docserverDao.get(doc.getDocserverid());
							//删除物理文件
							if ("LOCAL".equals(docserver.getServertype())) {
								//得到服务器路径
								String serverPath = docserver.getServerpath();
								if (!serverPath.substring(serverPath.length()-1,serverPath.length()).equals("/")) {
									serverPath += "/";
					            }
								serverPath += doc.getDocpath();
								String filename = doc.getDocnewname();
								FileOperate fo = new FileOperate();
								boolean b = fo.delFile(serverPath + filename);
							}
							else {
								//处理ftp删除
					            FtpUtil util = new FtpUtil();
					            util.connect(docserver.getServerip(),
					                    docserver.getServerport(),
					                    docserver.getFtpuser(),
					                    docserver.getFtppassword(),
					                    docserver.getServerpath());
//					                FileInputStream s = new FileInputStream(newFile);
//					                util.uploadFile(s, newName);
					            util.changeDirectory(doc.getDocpath());
					            boolean isDel = util.deleteFile(doc.getDocnewname());
					            util.closeServer();
							}
						}
						//删除文件记录
						docDao.del(doc.getId());
						
					}
				}
				
				//删除真实表
//				DROP TABLE IF EXISTS `A`;
				StringBuffer sb = new StringBuffer();
				sb.append("drop table if exists ").append(sys_table.getTablename()).append(";");
				tableDao.execute(sb.toString());
				
				//删除field字段
				sql = "delete from sys_templetfield where tableid=?";
				values.clear();
				values.add(sys_table.getId());
				templetfieldDao.del(sql, values);
				
				tableDao.del(sys_table.getId());
				
			}
			
		}
		//删除templet本身
		int num = templetDao.del(id);
		
		if (num > 0) {
			result = "success";
		}
		
		return result;
	}
}
