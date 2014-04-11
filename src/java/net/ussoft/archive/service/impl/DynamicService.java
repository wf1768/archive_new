package net.ussoft.archive.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.dao.DocDao;
import net.ussoft.archive.dao.DocserverDao;
import net.ussoft.archive.dao.DynamicDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.util.ArchiveUtil;
import net.ussoft.archive.util.FileOperate;
import net.ussoft.archive.util.FtpUtil;
import net.ussoft.archive.util.resule.ResultInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Override
	public PageBean<Map<String, Object>> archiveList(String treeid,String parentid,String tabletype,String searchTxt,
			PageBean<Map<String, Object>> pageBean) {
		if (null == treeid || treeid.equals("")) {
			return null;
		}
		
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
		
		if (null == tables || tables.size() != 1) {
			return null;
		}
		
		
		
		//获取字段
		values.clear();
		values.add(tables.get(0).getId());
		List<Sys_templetfield> fields = templetfieldDao.search("select * from sys_templetfield where tableid=? and accountid='SYSTEM'", values);
		
		sql = ArchiveUtil.createSql(tables.get(0).getTablename(), searchTxt, fields);
		
		if (sql.contains("WHERE")) {
			sql += " and treeid=? and status = 0";
		}
		else {
			sql += " WHERE treeid=? and status = 0";
		}
		
		values.clear();
		values.add(tree.getId());
		
		if (tabletype.equals("02")) {
			if (null != parentid && !parentid.equals("")) {
				sql += " and parentid=?";
				values.add(parentid);
			}
		}
		
		pageBean = dynamicDao.searchForMap(sql, values, pageBean);
		
		return pageBean;
	}
	
	@Override
	public List<Map<String, Object>> get(String treeid, String tabletype,String id,String orderby) {
		if (null == treeid || treeid.equals("")) {
			return null;
		}
		
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
		
		if (null == tables || tables.size() != 1) {
			return null;
		}
		
		String[] ids = id.split(",");
		List<String> idList = Arrays.asList(ids);
		
		StringBuilder sb = new StringBuilder("select * from ");
		sb.append(tables.get(0).getTablename());
		sb.append(" where ");
		sb.append(" id in (");
		Serializable[] ss=new Serializable[idList.size()];
		Arrays.fill(ss, "?");
		sb.append(StringUtils.join(ss,','));
		sb.append(")");
		values.clear();
		values.addAll(idList);
		
		if (null != orderby && !"".equals(orderby)) {
			sb.append(" ").append(orderby);
		}
		
		List<Map<String, Object>> maps = dynamicDao.searchForMap(sb.toString(), values);
		
		return maps;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo saveArchive(Map<String, String> sysFieldMap,List<Map<String, String>> archiveList) {
		
		String treeid = sysFieldMap.get("treeid");
		String tabletype = sysFieldMap.get("tabletype");
		Integer status = 0;
		
		if (null != sysFieldMap.get("status") || !sysFieldMap.get("status").equals("")) {
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
		
		List<Object> values=new ArrayList<Object>();
		values.add(tree.getTempletid());
		values.add(tabletype);
		//根据templetid获取tableid
		List<Sys_table> tableList = tableDao.search("select * from sys_table where templetid=? and tabletype=?", values);
		
		
		//获取templet
//		Sys_templet templet = templetDao.get(tree.getTempletid());
//		//获取字段
//		values.clear();
//		values.add(tableList.get(0).getId());
//		List<Sys_templetfield> fieldList = templetfieldDao.search("select * from sys_templetfield where tableid=? and accountid='SYSTEM'  and sort != -1 order by sort", values);
		
		for (int i=0;i<archiveList.size();i++) {
			StringBuilder sb=new StringBuilder("insert into ");
			sb.append(tableList.get(0).getTablename());
			
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
			
		}
		
		info.setSuccess(true);
		info.setMsg("处理数据成功。");
		
		return info;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo updaetArchive(String tabletype, List<Map<String, String>> archiveList) {
		
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
		
		//获取tree对象
		Sys_tree tree = treeDao.get(treeid);
		if (tree == null) {
			info.setSuccess(false);
			info.setMsg("根据参数treeid，没有获得tree对象。");
			return info;
		}
		
		List<Object> values=new ArrayList<Object>();
		values.add(tree.getTempletid());
		values.add(tabletype);
		//根据templetid获取tableid
		List<Sys_table> tableList = tableDao.search("select * from sys_table where templetid=? and tabletype=?", values);
		
		
		//获取templet
//		Sys_templet templet = templetDao.get(tree.getTempletid());
//		//获取字段
//		values.clear();
//		values.add(tableList.get(0).getId());
//		List<Sys_templetfield> fieldList = templetfieldDao.search("select * from sys_templetfield where tableid=? and accountid='SYSTEM'  and sort != -1 order by sort", values);
		
		for (int i=0;i<archiveList.size();i++) {
			StringBuilder sb=new StringBuilder("update ");
			sb.append(tableList.get(0).getTablename());
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
	public ResultInfo deleteDoc(List<Sys_doc> docs) throws SocketException, IOException {
		
		ResultInfo info = new ResultInfo();
		
		if (null != docs && docs.size() > 0) {
			for (Sys_doc doc : docs) {
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
		            //删除文件记录
					docDao.del(doc.getId());
				}
				else {
					//处理ftp删除
		            FtpUtil util = new FtpUtil();
		            util.connect(docserver.getServerip(),
		                    docserver.getServerport(),
		                    docserver.getFtpuser(),
		                    docserver.getFtppassword(),
		                    docserver.getServerpath());
//		                FileInputStream s = new FileInputStream(newFile);
//		                util.uploadFile(s, newName);
		            util.changeDirectory(doc.getDocpath());
		            boolean isDel = util.deleteFile(doc.getDocnewname());
		            util.closeServer();
		            //删除文件记录
		            docDao.del(doc.getId());
				}
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
	private void exeDelete(String tableid,String tablename,List<String> ids) throws SocketException, IOException {
		String sql = "";
		List<Object> values=new ArrayList<Object>();
		
		for (int i=0;i<ids.size();i++) {
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
			values.clear();
			values.add(tree.getTempletid());
			values.add("02");
			//根据templetid获取tableid
			List<Sys_table> wjTable = tableDao.search("select * from sys_table where templetid=? and tabletype=?", values);
			
			String wjTableNameString = wjTable.get(0).getTablename();
			//获取文件级，并删除
			for (int i=0;i<ids.size();i++) {
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
					exeDelete(wjTable.get(0).getId(), wjTableNameString, wjids);
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
		
		exeDelete(tableList.get(0).getId(), tableList.get(0).getTablename(), ids);
		
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

}
