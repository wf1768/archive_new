package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.service.ITableService;
@Service
public class TableService implements ITableService {
	
	@Resource
	private TableDao tableDao;
	@Resource
	private TreeDao treeDao;
	@Resource
	private TempletfieldDao templetfieldDao;

	@Override
	public List<Sys_table> list() {
		return tableDao.getAll("tabletype");
	}

	@Override
	public List<Sys_templetfield> geTempletfields(String tableid) {
		
		String sql = "select * from sys_templetfield where tableid=? and sort != -1 order by sort";
		List<Object> values=new ArrayList<Object>();
		
		//获取字段
		values.add(tableid);
		List<Sys_templetfield> fieldList = templetfieldDao.search(sql, values);
		
		return fieldList;
	}

}
