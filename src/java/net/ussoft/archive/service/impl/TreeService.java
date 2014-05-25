package net.ussoft.archive.service.impl;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.DocDao;
import net.ussoft.archive.dao.DocserverDao;
import net.ussoft.archive.dao.DynamicDao;
import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.FileOperate;
import net.ussoft.archive.util.FtpUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


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
	private AccountTreeDao accountTreeDao;
	@Resource
	private OrgTreeDao orgtreeDao;
	@Resource
	private DocDao docDao;
	@Resource
	private DocserverDao docserverDao;
	@Resource
	private DynamicDao dynamicDao;
	@Resource
	private AccountDao accountDao;
	
	@Override
	public Sys_tree getById(String id) {
		return treeDao.get(id);
	}

	@Override
	public List<Sys_tree> list() {
		return treeDao.getAll(" sort asc");
//		return treeDao.getAll("CONVERT(treename USING gbk)");
	}
	
	
	
	@Override
	public List<Sys_tree> list(String where, List<Object> values, String order) {
		String sql = "select * from sys_tree";
		if (null != where && !where.equals("")) {
			sql += where;
		}
		
		if (null != order && !order.equals("")) {
			sql += order;
		}
		return treeDao.search(sql, values);
	}

	@Transactional("txManager")
	@Override
	public String insert(Sys_tree tree) {
		
		String result = "failure";
		
		tree.setId(UUID.randomUUID().toString());
		String treenode = tree.getId().substring(tree.getId().length()-8, tree.getId().length());
		Sys_tree parentTree = treeDao.get(tree.getParentid());
		if (tree.getParentid().equals("0")) {
			tree.setTreenode("0#"+treenode);
		}
		else {
			//生成treenode
			if (null == parentTree || parentTree.getTreenode().equals("")) {
				return result;
			}
			tree.setTreenode(parentTree.getTreenode() + "#" + treenode);
		}
		
		tree.setTempletid(parentTree.getTempletid());
		//排序
		String sql = "select max(sort) from sys_tree where parentid=?";
		List<Object> values = new ArrayList<Object>();
		values.clear();
		values.add(tree.getParentid());
		Long lo = treeDao.getLong(sql, values);
		
		tree.setSort(lo.intValue()+1);
		
		treeDao.save(tree);
		
		return "success";
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_tree tree) {
		treeDao.update(tree);
		return 1;
	}

	@Transactional("txManager")
	@Override
	public String delete(String id) throws SocketException, IOException {
		
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		//判断要删除如果是档案节点，继续删除。
		//如果是档案节点夹，查是否有下级，如果有，就返回提示，需要清空下级，才能删除
		Sys_tree tree = treeDao.get(id);
		if (tree.getTreetype().equals("FT")) {
			sql = "select * from sys_tree where parentid=?";
			values.clear();
			values.add(tree.getId());
			List<Sys_tree> t = treeDao.search(sql, values);
			if (t.size() > 0) {
				return "error";
			}
		}

		//删除帐户组、帐户的权限关联树节点
		sql = "delete from sys_account_tree where treeid=?";
		values.clear();
		values.add(tree.getId());
		accountTreeDao.del(sql, values);
		
		sql = "delete from sys_org_tree where treeid=?";
		orgtreeDao.del(sql, values);
		
		
		if (tree.getTreetype().equals("FT")) {
			treeDao.del(id);
			return "success";
		}
		
		//获取treeid对应的templet、table对象。用来删除电子全文和档案数据
		Sys_templet templet = templetDao.get(tree.getTempletid());
		sql = "select * from sys_table where templetid=?";
		values.clear();
		values.add(templet.getId());
		List<Sys_table> tables = tableDao.search(sql, values);
		
		//删除电子全文
		sql = "select * from sys_doc where treeid=?";
		values.clear();
		values.add(tree.getId());
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
//			                FileInputStream s = new FileInputStream(newFile);
//			                util.uploadFile(s, newName);
			            util.changeDirectory(doc.getDocpath());
			            boolean isDel = util.deleteFile(doc.getDocnewname());
			            util.closeServer();
					}
				}
				//删除文件记录
				docDao.del(doc.getId());
				
			}
		}
		
		//删除档案数据
		for (Sys_table table : tables) {
			sql = "delete from " + table.getTablename() + " where treeid='" + tree.getId() + "'";
			values.clear();
			dynamicDao.del(sql, values);
		}
		
		treeDao.del(id);
		
		return "success";
	}
	
	@Transactional("txManager")
	@Override
	public String move(String id, String targetid) {
		if (id == null || id.equals("") || targetid == null || targetid.equals("")) {
			return "failure";
		}
		List<Object> values = new ArrayList<Object>();
		String sql = "";
		
		//要移动的节点
		Sys_tree tree = treeDao.get(id);
		//移动到目标节点
		Sys_tree targetTree = treeDao.get(targetid);
		
		if (null == tree) {
			return "failure";
		}
		
		Sys_tree tmp = new Sys_tree();
		tmp.setId(tree.getId());
		tmp.setParentid(targetid);
		
		//查sort最大值＋1赋予新字段sort
		values.clear();
		sql = "select max(sort) from sys_tree where parentid=?";
		values.clear();
		values.add(tmp.getParentid());
		Long lo = treeDao.getLong(sql, values);
		
		tmp.setSort(lo.intValue()+1);
//		tmp.setSort(templet.getSort());
		
		//更改tree的treenode
		String treenode = tmp.getId().substring(tmp.getId().length()-8, tmp.getId().length());
		tmp.setTreenode(targetTree.getTreenode()+"#"+treenode);
		
		treeDao.update(tmp);
		
		//更新tree的所有下级的treenode
		sql = "update sys_tree set treenode=concat(?,right(id,8)) where parentid=?";
		values.clear();
		values.add(tmp.getTreenode()+"#");
		values.add(tmp.getId());
		treeDao.update(sql, values);
		
		return "success";
	}
	
	
	@Transactional("txManager")
	@Override
	public int sortsave(Sys_tree tree) {
		treeDao.update(tree);
		return 1;
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
		String sql = "select * from sys_templetfield where tableid=? and accountid='SYSTEM' and sort != -1 order by sort";
		List<Object> values = new ArrayList<Object>();
		values.add(table.getId());
		List<Sys_templetfield> templetfields = templetfieldDao.search(sql, values);
		return templetfields;
	}
	
	@Override
	public List<Sys_templetfield> getTempletfields(String treeid,
			String tabletype,String accountid) {
		
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
		String sql = "select * from sys_templetfield where tableid=? and accountid=?  order by sort";
		List<Object> values = new ArrayList<Object>();
		values.add(table.getId());
		values.add(accountid);
		List<Sys_templetfield> templetfields = templetfieldDao.search(sql, values);
		
		if (null == templetfields || templetfields.size() == 0) {
			//根据tableid，获取字段 templetfield的list
			sql = "select * from sys_templetfield where tableid=? and accountid='SYSTEM'  order by sort";
			values.clear();
			values.add(table.getId());
			templetfields = templetfieldDao.search(sql, values);
		}
		
		return templetfields;
	}
	

	//修改档案树list的结构，增加根节点，修改节点图标
	public String createTreeJson(String jsonStr,String basePath) {
		
		JSONArray jsonArray = new JSONArray();
		if (!jsonStr.equals("")) {
			jsonArray = JSON.parseArray(jsonStr);
		}
		
		//通过json对象，插入isparent
		
		//添加根节点
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "0");
		jsonObject.put("treename", "档案树");
		jsonObject.put("treetype", "T");
		jsonArray.add(jsonObject);
		
		//这里要的大写字母要注意：
		// F：档案类型。
		// T: 档案类型的容器夹。
		// FT: 档案类型下的树节点容器夹
		// W : 档案树节点
		// 关系说明：档案类型的容器夹 包含 档案类型 包含 档案树节点夹  包含  树节点
		for (int i=0;i<jsonArray.size();i++) {
			String typeString = ((JSONObject) jsonArray.get(i)).get("treetype").toString();
			if (typeString.equals("F") || typeString.equals("T") || typeString.equals("FT")) {
				((JSONObject) jsonArray.get(i)).put("isParent", true);
			}
			if (typeString.equals("F")) {
				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"/images/icons/1.gif");
				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"/images/icons/2.gif");
			}
			if (typeString.equals("T") || typeString.equals("FT")) {
				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"/images/folder.gif");
				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"/images/folder-open.gif");
			}
			if (typeString.equals("W")) {
				((JSONObject) jsonArray.get(i)).put("icon", basePath+"/images/icons/page.png");
			}
			
		}
		
		String jsonString = JSON.toJSONString(jsonArray);
		
		return jsonString;
	}
	
	private String createCountStr(HashMap<String, HashMap<String, String>> treeMap,String treenode) {
		
		String countStr = "";
		String templettype = "";
		Integer ajCount = 0;
		Integer wjCount = 0;
		for (String key: treeMap.keySet()) {
			HashMap<String, String> countMap = treeMap.get(key);
			String countTreeNode = countMap.get("treenode");
			int num = countTreeNode.indexOf(treenode + "#");
			if (num != -1 && num == 0) {
				templettype = countMap.get("templettype");
				if (templettype.equals("A") || templettype.equals("P")) {
					ajCount += Integer.valueOf(countMap.get("aj").toString());
					wjCount += Integer.valueOf(countMap.get("wj").toString());
				}
				else if (templettype.equals("F")) {
					wjCount += Integer.valueOf(countMap.get("wj").toString());
				}
			}
		}
		
		if (!"".equals(templettype)) {
			if (templettype.equals("A") || templettype.equals("P")) {
				countStr = "[<span style='color:red;margin-right:0px;'>案:" + ajCount + " 文:" + wjCount + "</span>]";
			}
			else if (templettype.equals("F")) {
				countStr = "[<span style='color:red;margin-right:0px;'>文:" + wjCount + "</span>]";
			}
		}
		return countStr;
	}
	
	@Override
	public String createTreeJson(String jsonStr, String basePath,
			HashMap<String, HashMap<String, String>> treeMap) {
		
		JSONArray jsonArray = new JSONArray();
		if (!jsonStr.equals("")) {
			jsonArray = JSON.parseArray(jsonStr);
		}
		
		//通过json对象，插入isparent
		
		//添加根节点
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "0");
		String baseStr = createCountStr(treeMap,"0");
		jsonObject.put("treename", "档案树"+ baseStr);
		jsonObject.put("treetype", "T");
		jsonArray.add(jsonObject);
		
		//这里要的大写字母要注意：
		// F：档案类型。
		// T: 档案类型的容器夹。
		// FT: 档案类型下的树节点容器夹
		// W : 档案树节点
		// 关系说明：档案类型的容器夹 包含 档案类型 包含 档案树节点夹  包含  树节点
		for (int i=0;i<jsonArray.size();i++) {
			String typeString = ((JSONObject) jsonArray.get(i)).get("treetype").toString();
			if (typeString.equals("F") || typeString.equals("T") || typeString.equals("FT")) {
				((JSONObject) jsonArray.get(i)).put("isParent", true);
			}
			if (typeString.equals("F")) {
				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"/images/icons/1.gif");
				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"/images/icons/2.gif");
				String countStr = createCountStr(treeMap,((JSONObject) jsonArray.get(i)).getString("treenode"));
				((JSONObject) jsonArray.get(i)).put("treename",((JSONObject) jsonArray.get(i)).getString("treename") + countStr );
			}
			if (typeString.equals("T") || typeString.equals("FT")) {
				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"/images/folder.gif");
				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"/images/folder-open.gif");
			}
			if (typeString.equals("W")) {
				((JSONObject) jsonArray.get(i)).put("icon", basePath+"/images/icons/page.png");
				HashMap<String, String> countMap = treeMap.get(((JSONObject) jsonArray.get(i)).get("id"));
				if (null != countMap && countMap.size() >0) {
					Sys_templet templet = templetDao.get(((JSONObject) jsonArray.get(i)).get("templetid").toString());
					String templettype = templet.getTemplettype();
					String countStr = "";
					if (templettype.equals("A") || templettype.equals("P")) {
						countStr = "[<span style='color:red;margin-right:0px;'>案:" + countMap.get("aj") + " 文:" + countMap.get("wj") + "</span>]";
					}
					else if (templettype.equals("F")) {
						countStr = "[<span style='color:red;margin-right:0px;'>文:" + countMap.get("wj") + "</span>]";
					}
					
					((JSONObject) jsonArray.get(i)).put("treename",((JSONObject) jsonArray.get(i)).getString("treename") + countStr );
				}
			}
			
		}
		
		String jsonString = JSON.toJSONString(jsonArray);
		
		return jsonString;
	}

	@Override
	public List<Sys_tree> getAuthTree(String accountid) {
		
		List<Sys_tree> trees = new ArrayList<Sys_tree>();
		//读取帐户本身的树节点权限
		trees = getAccountTree(accountid);
		//如果帐户没有设置，读取所属组的
		if (null == trees || trees.size() == 0) {
			Sys_account account = accountDao.get(accountid);
			trees = getOrgTree(account.getOrgid());
		}
		
		return trees;
	}
	
	@Override
	public List<Sys_tree> getAccountTree(String accountid) {
		List<Object> values=new ArrayList<Object>();
		values.add(accountid);
		List<Sys_account_tree> accountTreeList = accountTreeDao.search("select * from sys_account_tree where accountid=?", values);
		
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
		
		List<Sys_tree> treeList = treeDao.search("select * from sys_tree where id in ("+sb.toString()+") order by sort asc", values);
		
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
		
		List<Sys_tree> treeList = treeDao.search("select * from sys_tree where id in ("+sb.toString()+") order by sort asc", values);
		
		return treeList;
	}

}