package net.ussoft.archive.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.DocDao;
import net.ussoft.archive.dao.DocserverDao;
import net.ussoft.archive.dao.DynamicDao;
import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.util.ArchiveUtil;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;
import net.ussoft.archive.util.FileOperate;
import net.ussoft.archive.util.FtpUtil;
import net.ussoft.archive.util.resule.ResultInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class DynamicService implements IDynamicService {
	
	@Resource
	private DynamicDao dynamicDao;
	@Resource
	private TreeDao treeDao;
	@Resource
	private TableDao tableDao;
	@Resource
	private TempletDao templetDao;
	@Resource
	private TempletfieldDao templetfieldDao;
	@Resource
	private DocserverDao docserverDao;
	@Resource
	private DocDao docDao;
	@Resource
	private AccountTreeDao accountTreeDao;
	@Resource
	private OrgTreeDao orgTreeDao;
	
	@Resource
	private HttpServletRequest request;
	
	/**
	 * 获取帐户或组的tree节点的数据访问权限
	 * @param account
	 * @param treeid
	 * @return
	 */
	private String getTreeAuth(Sys_account account,String treeid) {
		Sys_account_tree account_tree = new Sys_account_tree();
		account_tree.setAccountid(account.getId());
		account_tree.setTreeid(treeid);
		account_tree = accountTreeDao.searchOne(account_tree);
		//判断当前帐户对当前tree节点的数据访问权限是否设置
		if (null != account_tree && !"".equals(account_tree.getFilter())) {
			return account_tree.getFilter();
		}
		//如果没有设置，获取帐户所属组的数据访问权限
		Sys_org_tree org_tree = new Sys_org_tree();
		org_tree.setOrgid(account.getOrgid());
		org_tree.setTreeid(treeid);
		org_tree = orgTreeDao.searchOne(org_tree);
		//判断当前帐户对当前tree节点的数据访问权限是否设置
		if (null != org_tree && !"".equals(org_tree.getFilter())) {
			return org_tree.getFilter();
		}

		return "";
	}
	
	@Override
	public PageBean<Map<String, Object>> archiveList(String treeid,Boolean allwj,String parentid,String tabletype,String searchTxt,
			Integer status,PageBean<Map<String, Object>> pageBean) {
		if (null == treeid || treeid.equals("")) {
			return null;
		}
		
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		Sys_table tables =  getTable(treeid,tabletype);
		if (null == tables) {
			return null;
		}
		
		//获取字段
		List<Sys_templetfield> fields = getTempletfields(treeid,tabletype);
		
		sql = ArchiveUtil.createSql(tables.getTablename(), searchTxt, fields);
		
		if (sql.contains("WHERE")) {
			sql += " and treeid=? and status = " + status;
		}
		else {
			sql += " WHERE treeid=? and status = " + status;
		}
		
		values.clear();
		values.add(treeid);
		
		//TODO 这里要加上记录访问权限
//		sql += " and tm like '%2007%'";
		Sys_account accountSession = (Sys_account) CommonUtils.getSessionAttribute(request, Constants.user_in_session);
		String dataFilter = getTreeAuth(accountSession,treeid);
		//如果当前节点的数据访问权限不为空
		if (null != dataFilter && !"".equals(dataFilter)) {
			//循环判断属于当前treeid和tabletype的数据访问权限，加入sql
			JSONArray jsonArray = new JSONArray();
			jsonArray = JSON.parseArray(dataFilter);
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject)jsonArray.get(i);
				
				String filterTableType = (String) jsonObject.get("tableType");
				if (null != filterTableType && !"".equals(filterTableType)) {
					String filterSelectField = (String) jsonObject.get("selectField");
					String filterOper = (String) jsonObject.get("oper");
					String filterDataAuthValue = (String) jsonObject.get("dataAuthValue");
					if (filterTableType.equals(tabletype)) {
						sql += " and " + filterSelectField;
						if (filterOper.equals("equal")) {
							sql += " = ?";
							values.add(filterDataAuthValue);
						}
						else {
							sql += " like '%"+filterDataAuthValue+"%'";
						}
					}
				}
//				[{"dataAuthValue":"kolllljhh","fieldname":"归档单位","id":"10084513-c588-4380-815c-eb184f03e0ff","oper":"equal","selectField":"GDDW","tableType":"01"},{"dataAuthValue":"90","fieldname":"文件号","id":"d58575a2-e5a4-46ce-a56f-4edd09876e1e","oper":"equal","selectField":"WJH","tableType":"02"}]
			}
		}
		
		//如果是文件级，并且不显示全文件，赋予parentid
		if (tabletype.equals("02") && allwj == false) {
			if (null != parentid && !parentid.equals("")) {
				sql += " and parentid=?";
				values.add(parentid);
			}
		}
		//如果是全文件，判断要取文件级的范围，部分案卷级的全部文件，还是全部案卷级的文件
		if (allwj) {
			if (null != parentid && !"".equals(parentid)) {
				
				String[] idArr = parentid.split(",");
				List<String> idList = Arrays.asList(idArr);
				
				StringBuilder sb = new StringBuilder();
				if (null != idList && idList.size() > 0) {
					sb.append(" and parentid in (");
					Serializable[] ss=new Serializable[idList.size()];
					Arrays.fill(ss, "?");
					sb.append(StringUtils.join(ss,','));
					sb.append(")");
					values.addAll(idList);
					
					sql += sb.toString();
				}
			}
		}
		
		pageBean = dynamicDao.searchForMap(sql, values, pageBean);
		
		return pageBean;
	}
	
	@Override
	public PageBean<Map<String, Object>> archiveList(String w_sql,String treeid,Boolean allwj,String parentid,String tabletype,Integer status,PageBean<Map<String, Object>> pageBean) {
		if (null == treeid || treeid.equals("")) {
			return null;
		}
		
		List<Object> values = new ArrayList<Object>();
		Sys_table tables =  getTable(treeid,tabletype);
		if (null == tables) {
			return null;
		}
		//获取字段
		List<Sys_templetfield> fields = getTempletfields(treeid,tabletype);
		
		String sql = ArchiveUtil.createSql(tables.getTablename(), "", fields);
		
		if (sql.contains("WHERE")) {
			sql += " and treeid=? and status = " + status + w_sql;
		}
		else {
			sql += " WHERE treeid=? and status = " + status + w_sql;
		}
		
		//TODO 这里要加上记录访问权限
		values.clear();
		values.add(treeid);
		//如果是文件级，并且不显示全文件，赋予parentid
		if (tabletype.equals("02") && allwj == false) {
			if (null != parentid && !parentid.equals("")) {
				values.add(parentid);
			}
		}
		//如果是全文件，判断要取文件级的范围，部分案卷级的全部文件，还是全部案卷级的文件
		if (allwj) {
			if (null != parentid && !"".equals(parentid)) {
				String[] idArr = parentid.split(",");
				List<String> idList = Arrays.asList(idArr);
				if (null != idList && idList.size() > 0) {
					values.addAll(idList);
				}
			}
		}
		pageBean = dynamicDao.searchForMap(sql, values, pageBean);
		
		return pageBean;
	}
	
	@Override
	public Integer archiveCount(String treeid,
			String tabletype, String searchTxt, Integer status) {
		
		if (null == treeid || treeid.equals("")) {
			return null;
		}
		
		if (null == status || status < 0) {
			status = 0;
		}
		
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		Sys_table tables =  getTable(treeid,tabletype);
		if (null == tables) {
			return null;
		}
		
		//获取字段
		List<Sys_templetfield> fields = getTempletfields(treeid,tabletype);
		
		sql = ArchiveUtil.createSql(tables.getTablename(), searchTxt, fields);
		
		if (sql.contains("WHERE")) {
			sql += " and treeid=? and status = " + status;
		}
		else {
			sql += " WHERE treeid=? and status = " + status;
		}
		
		
		
		values.clear();
		values.add(treeid);
		
		//TODO 这里要加上记录访问权限
		Sys_account accountSession = (Sys_account) CommonUtils.getSessionAttribute(request, Constants.user_in_session);
		String dataFilter = getTreeAuth(accountSession,treeid);
		//如果当前节点的数据访问权限不为空
		if (null != dataFilter && !"".equals(dataFilter)) {
			//循环判断属于当前treeid和tabletype的数据访问权限，加入sql
			JSONArray jsonArray = new JSONArray();
			jsonArray = JSON.parseArray(dataFilter);
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject)jsonArray.get(i);
				
				String filterTableType = (String) jsonObject.get("tableType");
				if (null != filterTableType && !"".equals(filterTableType)) {
					String filterSelectField = (String) jsonObject.get("selectField");
					String filterOper = (String) jsonObject.get("oper");
					String filterDataAuthValue = (String) jsonObject.get("dataAuthValue");
					if (filterTableType.equals(tabletype)) {
						sql += " and " + filterSelectField;
						if (filterOper.equals("equal")) {
							sql += " = ?";
							values.add(filterDataAuthValue);
						}
						else {
							sql += " like '%"+filterDataAuthValue+"%'";
						}
					}
				}
//				[{"dataAuthValue":"kolllljhh","fieldname":"归档单位","id":"10084513-c588-4380-815c-eb184f03e0ff","oper":"equal","selectField":"GDDW","tableType":"01"},{"dataAuthValue":"90","fieldname":"文件号","id":"d58575a2-e5a4-46ce-a56f-4edd09876e1e","oper":"equal","selectField":"WJH","tableType":"02"}]
			}
		}
		
		Integer num = dynamicDao.getCount(sql, values);
		
		return num;
	}
	
	private Sys_table getTable(String treeid,String tabletype) {
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		//获取tree 对象
		Sys_tree tree = treeDao.get(treeid);
		//获得tree对应的templet
		Sys_templet templet = templetDao.get(tree.getTempletid());
		
		//获取table
		sql = "select * from sys_table where templetid=? and tabletype=?";
		values.add(templet.getId());
		values.add(tabletype);
		List<Sys_table> tables = tableDao.search(sql, values);
		return tables.get(0);
	}
	/**
	 * 获取档案字段，先获取登录帐户私有字段，没有设置就获取系统字段
	 * @param treeid
	 * @param tabletype
	 * @return
	 */
	private List<Sys_templetfield> getTempletfields(String treeid,String tabletype) {
		
		Sys_account accountSession = (Sys_account) CommonUtils.getSessionAttribute(request, Constants.user_in_session);
		
		Sys_table table = getTable(treeid, tabletype);
		
		if (null == table) {
			return null;
		}
		
		//根据tableid，获取字段 templetfield的list
		String sql = "select * from sys_templetfield where tableid=? and accountid=?  order by sort";
		List<Object> values = new ArrayList<Object>();
		values.add(table.getId());
		values.add(accountSession.getId());
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
	
	@Override
	public List<Map<String, Object>> get(String treeid, String parentid,String tabletype,List<String> idList,String orderby,Integer status,String searchTxt) {
		if (null == treeid || treeid.equals("")) {
			return null;
		}
		
		String sql = "";
		List<Object> values = new ArrayList<Object>();
		
		Sys_table tables =  getTable(treeid,tabletype);
		
		if (null == tables) {
			return null;
		}
		
		//获取字段
		List<Sys_templetfield> fields = getTempletfields(treeid,tabletype);
		
		sql = ArchiveUtil.createSql(tables.getTablename(), searchTxt, fields);
				
		if (sql.contains("WHERE")) {
			sql += " and treeid=? ";
		}
		else {
			sql += " WHERE treeid=? ";
		}
		values.add(treeid);
		
		if (null == status || status<0) {
			status = 0;
		}
		
		sql += " and status = ?";
		values.add(status);
		
//		if (null != status) {
//			sql += " and status = ?";
//			values.add(status);
//		}
		
		
		
		StringBuilder sb = new StringBuilder();
		if (null != idList && idList.size() > 0) {
			sb.append(" and id in (");
			Serializable[] ss=new Serializable[idList.size()];
			Arrays.fill(ss, "?");
			sb.append(StringUtils.join(ss,','));
			sb.append(")");
			values.addAll(idList);
		}
	
		if (null != parentid && !"".equals(parentid)) {
			String[] pidArr = parentid.split(",");
			List<String> pidList = Arrays.asList(pidArr);
			
//			StringBuilder sb = new StringBuilder();
			if (null != pidList && pidList.size() > 0) {
				sb.append(" and parentid in (");
				Serializable[] ss=new Serializable[pidList.size()];
				Arrays.fill(ss, "?");
				sb.append(StringUtils.join(ss,','));
				sb.append(")");
				values.addAll(pidList);
				
//				sql += sb.toString();
			}
//			sb.append(" and parentid = ?");
//			values.add(parentid);
		}
		
		
		if (null != orderby && !"".equals(orderby)) {
			sb.append(" order by ").append(orderby);
		}
		
		List<Map<String, Object>> maps = dynamicDao.searchForMap(sql + sb.toString(), values);
		
		return maps;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo insertArchive(Map<String, String> sysFieldMap,List<Map<String, String>> archiveList) {
		
		String treeid = sysFieldMap.get("treeid");
		String tabletype = sysFieldMap.get("tabletype");
		Integer status = 0;
		
		if (null != sysFieldMap.get("status") && !sysFieldMap.get("status").equals("")) {
			Integer.valueOf(sysFieldMap.get("status"));
		}
		
		ResultInfo info = new ResultInfo();
		
		if (null == treeid || treeid.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得treeid参数。");
			return info;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得tabletype参数。");
			return info;
		}
		
		//获取tree对象
		Sys_tree tree = treeDao.get(treeid);
		if (tree == null) {
			info.setSuccess(false);
			info.setMsg("根据参数treeid，没有获得tree对象。");
			return info;
		}
		
		if (tree.getTreetype().equals("F")) {
			info.setSuccess(false);
			info.setMsg("根据treeid参数，得到的树节点类型为“F”（文件夹）。为“W”的才是数据节点。请重新选择.");
			return info;
		}
		
		Sys_table table = getTable(treeid, tabletype);
		
		List<String> ids = new ArrayList<String>();
		for (int i=0;i<archiveList.size();i++) {
			StringBuilder sb=new StringBuilder("insert into ");
			sb.append(table.getTablename());
			
			List<String> columns=new ArrayList<String>();
			List<Object> par =new ArrayList<Object>();
			
			columns.add("id");
			String id = UUID.randomUUID().toString();
			par.add(id);
			columns.add("treeid");
			par.add(treeid);
			columns.add("status");
			par.add(status);
			columns.add("isdoc");
			par.add(0);
			for(Entry<String,String> e:archiveList.get(i).entrySet()){
				columns.add(e.getKey());
				par.add(e.getValue());
			}
			
			if (tabletype.equals("02")) {
				columns.add("parentid");
				par.add(sysFieldMap.get("parentid"));
			}
			
			sb.append("(");
			sb.append(StringUtils.join(columns,','));
			sb.append(") values(");
			String[] paras=new String[par.size()];
			Arrays.fill(paras, "?");
			sb.append(StringUtils.join(paras,','));
			sb.append(")");
			
			dynamicDao.add(sb.toString(), par);
			ids.add(id);
		}
		
		info.setSuccess(true);
		info.setMsg("处理数据成功。");
		info.put("idsList", ids);
		return info;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo updateArchive(String tabletype, List<Map<String, String>> archiveList) {
		
		ResultInfo info = new ResultInfo();
		
		if (null == archiveList || archiveList.size() == 0) {
			info.setSuccess(false);
			info.setMsg("没有获得更新参数。");
			return info;
		}
		
		String treeid = archiveList.get(0).get("treeid");
		
		if (null == treeid || treeid.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得treeid参数。");
			return info;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得tabletype参数。");
			return info;
		}
		
		
		Sys_table table = getTable(treeid, tabletype);
		
		for (int i=0;i<archiveList.size();i++) {
			StringBuilder sb=new StringBuilder("update ");
			sb.append(table.getTablename());
			sb.append(" set ");
			
			List<String> columns=new ArrayList<String>();
			List<Object> par =new ArrayList<Object>();
			String id = "";
			
			for(Entry<String,String> e:archiveList.get(i).entrySet()){
				if (e.getKey().toLowerCase() != "id") {
//					columns.add(e.getKey());
					columns.add(e.getKey() + "=?");
					par.add(e.getValue());
				}
				else {
					id = e.getValue();
				}
			}
			sb.append(StringUtils.join(columns,','));
			sb.append(" where id=?");
			par.add(id);
			
			dynamicDao.add(sb.toString(), par);
			
		}
		
		info.setSuccess(true);
		info.setMsg("处理数据成功。");
		
		return info;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo updateReplace(String treeid, String tabletype,
			List<String> ids, String tc_th_field, String th_key, String th_value) {
		
		ResultInfo info = new ResultInfo();
		
		if (null == ids || "".equals(ids)) {
			info.setSuccess(false);
			info.setMsg("没有获得更新参数。");
			return info;
		}
		
		if (null == treeid || treeid.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得treeid参数。");
			return info;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得tabletype参数。");
			return info;
		}
		
		
		Sys_table table = getTable(treeid, tabletype);
		
//		update A_74 set name = replace(name,"实际","aa")
		
		StringBuilder sb=new StringBuilder("update ");
		sb.append(table.getTablename());
		sb.append(" set ");
		sb.append(tc_th_field);
		sb.append(" = replace(").append(tc_th_field).append(",").append("'").append(th_key).append("','").append(th_value).append("')");
		
		List<Object> values = new ArrayList<Object>();
		if (null != ids && ids.size() > 0) {
			sb.append(" where id in (");
			Serializable[] ss=new Serializable[ids.size()];
			Arrays.fill(ss, "?");
			sb.append(StringUtils.join(ss,','));
			sb.append(")");
			values.addAll(ids);
		}
		
			
		dynamicDao.add(sb.toString(), values);
			
		
		info.setSuccess(true);
		info.setMsg("处理数据成功。");
		
		return info;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo updateHigh(String treeid, String tabletype,
			List<String> ids, String tc_th_field, String firstField,
			String txt, String secondField) {
		
		ResultInfo info = new ResultInfo();
		
		if (null == ids || "".equals(ids)) {
			info.setSuccess(false);
			info.setMsg("没有获得更新参数。");
			return info;
		}
		
		if (null == treeid || treeid.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得treeid参数。");
			return info;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得tabletype参数。");
			return info;
		}
		
		
		Sys_table table = getTable(treeid, tabletype);
		
//		update A_74 set pass = CONCAT_WS('-', pass,name)
		
		StringBuilder sb=new StringBuilder("update ");
		sb.append(table.getTablename());
		sb.append(" set ");
		sb.append(tc_th_field);
		sb.append(" = CONCAT_WS('").append(txt).append("',");
		if (null == firstField || "".equals(firstField)) {
			sb.append("'',");
		}
		else {
			sb.append(firstField).append(",");
		}
		
		if (null == secondField || "".equals(secondField)) {
			sb.append("'')");
		}
		else {
			sb.append(secondField).append(")");
		}
		
		List<Object> values = new ArrayList<Object>();
		if (null != ids && ids.size() > 0) {
			sb.append(" where id in (");
			Serializable[] ss=new Serializable[ids.size()];
			Arrays.fill(ss, "?");
			sb.append(StringUtils.join(ss,','));
			sb.append(")");
			values.addAll(ids);
		}
		
		System.out.println(sb.toString());
		dynamicDao.add(sb.toString(), values);
			
		
		info.setSuccess(true);
		info.setMsg("处理数据成功。");
		
		return info;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo deleteDoc(List<Sys_doc> docs) throws SocketException, IOException {
		
		ResultInfo info = new ResultInfo();
		
		if (null != docs && docs.size() > 0) {
			for (Sys_doc doc : docs) {
				
				//因档案复制粘贴功能，将造成电子文件共用的情况，所以在删除时，判断是否表中有存在多个
				String sql = "select * from sys_doc where docnewname=?";
				List<Object> values = new ArrayList<Object>();
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
		
		info.setSuccess(true);
		return info;
	}
	
	@Transactional("txManager")
	/**
	 * 执行删除
	 * @param treeid
	 * @param tablename
	 * @param ids
	 * @throws SocketException
	 * @throws IOException
	 */
	private void execDelete(String tableid,String tablename,List<String> ids) throws SocketException, IOException {
		String sql = "";
		List<Object> values=new ArrayList<Object>();
		
		for (int i=0;i<ids.size();i++) {
			//判断是不是多媒体
			
			StringBuilder sb=new StringBuilder("delete from ");
			sb.append(tablename);
			
			List<Object> par =new ArrayList<Object>();
			
			sb.append(" where id=?");
			par.add(ids.get(i));
			
			//删除档案挂接的电子全文
			//删除电子全文
			sql = "select * from sys_doc where tableid=? and fileid=?";
			values.clear();
			values.add(tableid);
			values.add(ids.get(i));
			List<Sys_doc> docs = docDao.search(sql, values);
			//删除电子全文
			deleteDoc(docs);
			
			dynamicDao.del(sb.toString(), par);
		}
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo deleteArchive(String treeid, String tabletype, List<String> ids) throws SocketException, IOException {
		ResultInfo info = new ResultInfo();
		
		if (null == ids || ids.size() == 0) {
			info.setSuccess(false);
			info.setMsg("没有获得要删除的数据。");
			return info;
		}
		
		if (null == treeid || treeid.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得treeid参数。");
			return info;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			info.setSuccess(false);
			info.setMsg("没有获得tabletype参数。");
			return info;
		}
		
		//获取tree对象
		Sys_tree tree = treeDao.get(treeid);
		if (tree == null) {
			info.setSuccess(false);
			info.setMsg("根据参数treeid，没有获得tree对象。");
			return info;
		}
		String sql = "";
		List<Object> values=new ArrayList<Object>();
		
		//首先判断是不是案卷级，如果是案卷，要先删除文件级电子全文、文件级
		Sys_templet templet = templetDao.get(tree.getTempletid());
		if (!templet.getTemplettype().equals("F") && tabletype.equals("01")) {
			Sys_table wjTable = getTable(treeid, "02");
			String wjTableNameString = wjTable.getTablename();
			//获取文件级，并删除
			for (int i=0;i<ids.size();i++) {
				
				//如果是多媒体，不删除挂接的电子文件,要删除slt的文件
				if (templet.getTemplettype().equals("P")) {
					//删除文件级有电子全文的
					sql = "select * from " + wjTableNameString + " where treeid=? and parentid=?";
					values.clear();
					values.add(treeid);
					values.add(ids.get(i));
					List<Map<String, Object>> wjList =dynamicDao.searchForMap(sql, values);
					if (null != wjList && wjList.size() > 0) {
						for (Map<String, Object> map : wjList) {
							if (null != map.get("slt") && !map.get("slt").toString().equals("")) {
								String pathString = request.getSession().getServletContext().getRealPath("/");
								File file = new File(pathString + map.get("slt").toString());
								file.delete();
							}
							if (null != map.get("imgnewname") && !map.get("imgnewname").toString().equals("")) {
								String pathString = request.getSession().getServletContext().getRealPath("/");
								File file = new File(pathString + map.get("imgnewname").toString());
								file.delete();
							}
						}
					}
				}
				else {
					//删除文件级有电子全文的
					sql = "select id from " + wjTableNameString + " where treeid=? and parentid=? and isdoc=?";
					values.clear();
					values.add(treeid);
					values.add(ids.get(i));
					values.add(1);
					List<Map<String, Object>> wjList =dynamicDao.searchForMap(sql, values);
					if (null != wjList && wjList.size() > 0) {
						List<String> wjids = new ArrayList<String>();
						for (Map<String, Object> map : wjList) {
							wjids.add(map.get("id").toString());
						}
						execDelete(wjTable.getId(), wjTableNameString, wjids);
					}
				}
				
				//删除所有文件级
				sql = "delete from " + wjTableNameString + " where treeid=? and parentid=?";
				values.clear();
				values.add(treeid);
				values.add(ids.get(i));
				dynamicDao.del(sql, values);
			}
		}
		
		values.clear();
		values.add(tree.getTempletid());
		values.add(tabletype);
		//根据templetid获取tableid
		List<Sys_table> tableList = tableDao.search("select * from sys_table where templetid=? and tabletype=?", values);
		
		
		//如果是多媒体，不删除挂接的电子文件,要删除slt的文件
		if (templet.getTemplettype().equals("P")) {
			List<Map<String, Object>> pmaps = get(treeid, null, tabletype, ids, null, null, null);
			if (null != pmaps && pmaps.size() > 0) {
				for (Map<String, Object> map : pmaps) {
					if (null != map.get("slt") && !map.get("slt").toString().equals("")) {
						String pathString = request.getSession().getServletContext().getRealPath("/");
						File file = new File(pathString + map.get("slt").toString());
						file.delete();
					}
					if (null != map.get("imgnewname") && !map.get("imgnewname").toString().equals("")) {
						String pathString = request.getSession().getServletContext().getRealPath("/");
						File file = new File(pathString + map.get("imgnewname").toString());
						file.delete();
					}
				}
			}
		}
				
		execDelete(tableList.get(0).getId(), tableList.get(0).getTablename(), ids);
		
		info.setSuccess(true);
		info.setMsg("处理数据成功。");
		
		return info;
	}
	
	

	@Override
	public PageBean<Map<String, Object>> search(String searchTxt,String tablename,String treeid,List<Sys_templetfield> tmpFieldList,int currentPage,int pageSize) {
		PageBean<Map<String, Object>> pb = new PageBean<Map<String,Object>>();
		pb.setPageNo(currentPage); 	//页码
		pb.setPageSize(pageSize);			//每页显示的条数
		if(!searchTxt.replace(" ", "").trim().equals("") || searchTxt != null){
			StringBuffer sb = new StringBuffer();
			sb.append(" AND treeid='" + treeid + "' AND (");
			for(int i=0;i<tmpFieldList.size();i++){
				if(i < tmpFieldList.size()-1){
					sb.append(tmpFieldList.get(i).getEnglishname() + " LIKE '%" + searchTxt + "%' OR ");
				}else{
					sb.append(tmpFieldList.get(i).getEnglishname() + " LIKE '%" + searchTxt + "%' )");
				}
			}
			String sql = "SELECT * FROM " + tablename +" WHERE 1=1 " + sb.toString();
			System.out.println(sql);
			List<Object> values = new ArrayList<Object>();
			pb = dynamicDao.searchForMap(sql, values, pb);
		}
		return pb;
	}

	@Transactional("txManager")
	@Override
	public void exeSql(String sql) {
		dynamicDao.execute(sql);
	}

	@Transactional("txManager")
	@Override
	public ResultInfo dataPaster(Map<String, Object> param) {
		
		ResultInfo info = new ResultInfo();
		
		//复制的档案记录id
		String ids = param.get("ids").toString();
		String treeid = param.get("treeid").toString();
		String tabletype = param.get("tabletype").toString();
		
		//获取档案信息
		String[] idArr = ids.split(",");
		List<String> idList = Arrays.asList(idArr);
		List<Map<String, Object>> maps = get(treeid, "",tabletype, idList,null,null,null);
		
		if (null == maps || maps.size() == 0) {
			info.setSuccess(false);
			return info;
		}
		
		//获取目标粘贴档案类型信息
		String targetTreeid = param.get("targetTreeid").toString();
		String targetTabletype = param.get("targetTabletype").toString();
		//如果粘贴到文件级，会赋予案卷级的id为parentid
		String parentid = param.get("parentid").toString();
		//如果选择了同时复制电子文件，
		Boolean isdoc = (Boolean) param.get("isdoc");
		String status = param.get("status").toString();
		String dy = param.get("dy").toString();
		Map<String, String> dyMap = (Map<String, String>) JSON.parse(dy);
		
		if (null == status || status.equals("")) {
			status = "0";
		}
		
		
		Map<String, String> sysFieldMap = new HashMap<String, String>();
		List<Map<String, String>> archiveList = new ArrayList<Map<String,String>>();
		//生成粘贴的档案系统参数
		sysFieldMap.put("treeid", targetTreeid);
		sysFieldMap.put("tabletype", targetTabletype);
		sysFieldMap.put("status", status);
		sysFieldMap.put("parentid", parentid);
		
		Sys_tree tree = treeDao.get(treeid);
		//获取源templet
		Sys_templet templet = templetDao.get(tree.getTempletid());
		//目标templet
		Sys_tree targetTree = treeDao.get(targetTreeid);
		Sys_templet targeTemplet = templetDao.get(targetTree.getTempletid());
		Sys_table targetTable = getTable(targetTreeid,targetTabletype);
		
		//如果源数据和目标数据属于一个档案类型，就直接copy，不对应字段
		Boolean common = false;
		//如果源数据和目标数据属于一个档案类型，并且是案卷粘贴到案卷，把文件级也带过去
		Boolean wjBoolean = false;
		if (templet.getId().equals(targeTemplet.getId())) {
			if (tabletype.equals(targetTabletype)) {
				common = true;
			}
			if (!templet.getTemplettype().equals("F") && tabletype.equals("01")) {
				if (!targeTemplet.getTemplettype().equals("F") && targetTabletype.equals("01")) {
					wjBoolean = true;
				}
			}
		}
		
		for (Map<String, Object> map : maps) {
			Map<String, String> tmpMap = new HashMap<String, String>();
			//源数据id
			String idString = map.get("ID").toString();
			Integer isdocInteger = (Integer) map.get("ISDOC");
			if (common) {
				map.remove("ID");
				map.remove("ISDOC");
				map.remove("TREEID");
				map.remove("STATUS");
				map.remove("PARENTID");
				map.remove("CREATETIME");
				String json = JSON.toJSONString(map);
				tmpMap = (Map<String, String>) JSON.parse(json);
			}
			else {
				for (String key : dyMap.keySet()) {
				    String value = dyMap.get(key);
				    if (null == map.get(value)) {
				    	tmpMap.put(key, "");
				    }
				    else {
				    	tmpMap.put(key, map.get(value).toString());
				    }
				}
			}
			archiveList.clear();
			archiveList.add(tmpMap);
			//插入一条记录
			ResultInfo tmpInfo = insertArchive(sysFieldMap, archiveList);
			
			//新的目标粘贴数据id
			String targetFileid = ((List<String>)tmpInfo.getData().get("idsList")).get(0).toString();
			Sys_table table = getTable(treeid,tabletype);
			//如果选择了同时copy电子文件
			if (isdoc) {
				if (isdocInteger == 1) {
					Sys_doc doc = new Sys_doc();
					doc.setFileid(idString);
					doc.setTableid(table.getId());
					List<Sys_doc> docs = docDao.search(doc);
					for (Sys_doc sys_doc : docs) {
						sys_doc.setId(UUID.randomUUID().toString());
						
						if (null == targetFileid || targetFileid.equals("")) {
							continue;
						}
						sys_doc.setFileid(targetFileid);
						sys_doc.setTreeid(targetTreeid);
						sys_doc.setTableid(targetTable.getId());
						docDao.save(sys_doc);
					}
					if (docs.size() > 0) {
						HashMap<String, String> updateMap = new HashMap<String, String>();
						updateMap.put("id", targetFileid);
						updateMap.put("isdoc", "1");
						updateMap.put("treeid", targetTreeid);
						List<Map<String, String>> updateList = new ArrayList<Map<String,String>>();
						updateList.add(updateMap);
						updateArchive(targetTabletype, updateList);
					}
				}
			}
			if (wjBoolean) {
//				insert into 表1(字段1,字段2,字段3) select 字段1,字段2,字段3 from 表1 where id=1
//				List<Sys_templetfield> childField = getTempletfields(treeid, "02");
//				
//				List<String> fList = new ArrayList<String>();
//				for (Sys_templetfield field : childField) {
//					if (!field.getEnglishname().toLowerCase().equals("id") && !field.getEnglishname().toLowerCase().equals("treeid")) {
//						fList.add(field.getEnglishname());
//					}
//				}
//				String fString = CommonUtils.ListToString(fList, ",");
//				Sys_table childTable = getTable(treeid, "02");
//				StringBuffer sb = new StringBuffer();
//				sb.append("insert into ");
//				sb.append(childTable.getTablename());
//				sb.append("(").append("id,treeid,").append(fString).append(")");
//				sb.append("select '").append(UUID.randomUUID().toString()).append("','").append(targetTreeid).append("',");
//				sb.append(fString).append(" from ").append(childTable.getTablename());
//				sb.append(" where parentid='").append(map.get("ID").toString()).append("'");
//				
//				List<Object> values = new ArrayList<Object>();
//				dynamicDao.add(sb.toString(), values);
				
				//获取文件级数据
				List<Map<String, Object>> childMaps = get(treeid,idString ,"02", null,null,null,null);
				
				//生成粘贴的档案系统参数
				Map<String, String> sysChildFieldMap = new HashMap<String, String>();
				List<Map<String, String>> archiveChildList = new ArrayList<Map<String,String>>();
				sysChildFieldMap.put("treeid", targetTreeid);
				sysChildFieldMap.put("tabletype", "02");
				sysChildFieldMap.put("parentid", targetFileid);
				
				if (null != childMaps && childMaps.size() > 0) {
					for (Map<String, Object> childMap : childMaps) {
						String childId = childMap.get("ID").toString();
						Integer childIsdoc = (Integer) childMap.get("ISDOC");
						childMap.remove("ID");
						childMap.remove("ISDOC");
						childMap.remove("TREEID");
						sysChildFieldMap.put("status", childMap.get("STATUS").toString());
						childMap.remove("STATUS");
						childMap.remove("PARENTID");
						childMap.remove("CREATETIME");
						String json = JSON.toJSONString(childMap);
						Map<String, String> aaMap = (Map<String, String>) JSON.parse(json);
						archiveChildList.clear();
						archiveChildList.add(aaMap);
						ResultInfo childInfo = insertArchive(sysChildFieldMap, archiveChildList);
						
						//如果选择了同时copy电子文件
						if (isdoc && childIsdoc == 1) {
							Sys_table childTable = getTable(treeid,"02");
							String targetChildFileid = ((List<String>)childInfo.getData().get("idsList")).get(0).toString();
							Sys_doc doc = new Sys_doc();
							doc.setFileid(childId);
							doc.setTableid(childTable.getId());
							List<Sys_doc> docs = docDao.search(doc);
							for (Sys_doc sys_doc : docs) {
								sys_doc.setId(UUID.randomUUID().toString());
								
								if (null == targetFileid || targetFileid.equals("")) {
									continue;
								}
								sys_doc.setFileid(targetChildFileid);
								sys_doc.setTreeid(targetTreeid);
								sys_doc.setTableid(childTable.getId());
								docDao.save(sys_doc);
							}
							if (docs.size() > 0) {
								HashMap<String, String> updateMap = new HashMap<String, String>();
								updateMap.put("id", targetChildFileid);
								updateMap.put("isdoc", "1");
								updateMap.put("treeid", targetTreeid);
								List<Map<String, String>> updateList = new ArrayList<Map<String,String>>();
								updateList.add(updateMap);
								updateArchive("02", updateList);
							}
						}
					}
				}
			}
		}
		
		info.setSuccess(true);
		info.setMsg("粘贴数据完毕。");
		return info;
	}

}
