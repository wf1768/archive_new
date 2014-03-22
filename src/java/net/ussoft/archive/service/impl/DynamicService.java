package net.ussoft.archive.service.impl;

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
import net.ussoft.archive.util.resule.ResultInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Override
	public PageBean<Map<String, Object>> archiveList(String treeid,String tabletype,
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
		//获取字段
		values.clear();
		values.add(tables.get(0).getId());
		List<Sys_templetfield> fields = templetfieldDao.search("select * from sys_templetfield where tableid=? ", values);
		
		sql = ArchiveUtil.createSql(tables.get(0).getTablename(), "1995年 1996", fields);
		
		if (sql.contains("WHERE")) {
			sql += " and treeid=? and status = 0";
		}
		else {
			sql += " WHERE treeid=? and status = 0";
		}
		
//		StringBuffer sb = new StringBuffer();
//		sb.append("select * from ");
//		sb.append(tables.get(0).getTablename());
//		sb.append(" where ");
//		sb.append(" treeid=? and status = 0");
		System.out.println(sql);
		values.clear();
		values.add(tree.getId());
		pageBean = dynamicDao.searchForMap(sql, values, pageBean);
		
		return pageBean;
	}
	
	@Transactional("txManager")
	@Override
	public ResultInfo saveArchive(String treeid, List<Map<String, String>> archiveList) {
		ResultInfo info = new ResultInfo();
		
		if (treeid.equals("") || null == treeid) {
			info.setSuccess(false);
			info.setMsg("没有获得treeid参数。");
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
		
//		//获取templetid
//		List<Object> values=new ArrayList<Object>();
//		values.add(treeid);
//		List<Sys_tree_templet> treeTempletList = treeTempletDao.search("select * from sys_tree_templet where treeid=?", values);
		
		List<Object> values=new ArrayList<Object>();
		values.add(tree.getTempletid());
		values.add("02");
		//根据templetid获取tableid
		List<Sys_table> tableList = tableDao.search("select * from sys_table where templetid=? and tabletype=?", values);
		
		//获取字段
		values.clear();
		values.add(tableList.get(0).getId());
		List<Sys_templetfield> fieldList = templetfieldDao.search("select * from sys_templetfield where tableid=? and sort != -1", values);
		
		for (int i=0;i<archiveList.size();i++) {
			StringBuilder sb=new StringBuilder("insert into ");
			sb.append(tableList.get(0).getTablename());
			
			List<String> columns=new ArrayList<String>();
			List<Object> par =new ArrayList<Object>();
			Object files = null;
			
			columns.add("id");
			String id = UUID.randomUUID().toString();
			par.add(id);
			columns.add("treeid");
			par.add(treeid);
			columns.add("status");
			par.add(2);
			Boolean isdoc = false;
			for(Entry<String,String> e:archiveList.get(i).entrySet()){
				if (!e.getKey().equals("files")) {
					columns.add(e.getKey());
					par.add(e.getValue());
				}
				else {
					isdoc = true;
					files = e.getValue();
				}
			}
			
			if (isdoc) {
				columns.add("isdoc");
				par.add(1);
			}
			else {
				columns.add("isdoc");
				par.add(0);
			}
			
			sb.append("(");
			sb.append(StringUtils.join(columns,','));
			sb.append(") values(");
			String[] paras=new String[par.size()];
			Arrays.fill(paras, "?");
			sb.append(StringUtils.join(paras,','));
			sb.append(")");
			
			dynamicDao.add(sb.toString(), par);
			
			//保存完档案数据后，处理电子全文
			if (files != null) {
				List<Sys_doc> docList = (List<Sys_doc>)JSONObject.parseArray(files.toString(), Sys_doc.class);
				//获取电子全文服务器信息
				List<Object> docvalues=new ArrayList<Object>();
				docvalues.add(1);
				List<Sys_docserver> docServerList = docserverDao.search("select * from sys_docserver where serverstate = ?", docvalues);
				
				for (Sys_doc doc : docList) {
					doc.setId(UUID.randomUUID().toString());
					doc.setDocserverid(docServerList.get(0).getId());
					doc.setDocnewname(doc.getDocoldname());
					doc.setFileid(id);
					doc.setTableid(tableList.get(0).getId());
					doc.setTreeid(treeid);
					
					docDao.save(doc);
				}
			}
		}
		
		info.setSuccess(true);
		info.setMsg("导入数据成功。");
		
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
}
