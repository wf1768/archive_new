package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ITempletfieldService;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.stereotype.Service;

@Service
public class TempletfieldService implements ITempletfieldService {
	
	@Resource
	private TreeDao treeDao;
	@Resource
	private TableDao tableDao;
	@Resource
	private TempletDao templetDao;
	@Resource
	private TempletfieldDao templetfieldDao;

	@Override
	public ResultInfo getField(String treeid) {
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
		
		List<Object> values=new ArrayList<Object>();
		values.add(tree.getTempletid());
		values.add("02");
		//根据templetid获取tableid
		List<Sys_table> tableList = tableDao.search("select * from sys_table where templetid=? and tabletype=?", values);
		
		//获取字段
		values.clear();
		values.add(tableList.get(0).getId());
		List<Sys_templetfield> fieldList = templetfieldDao.search("select * from sys_templetfield where tableid=? and sort != -1", values);
		
		info.setSuccess(true);
		info.setMsg("获取字段成功。");
		info.put("list", fieldList);
		
		return info;
		
	}

}
