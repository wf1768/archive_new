package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.service.ITableService;

import org.springframework.stereotype.Service;
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
		return tableDao.getAll();
	}

	@Override
	public List<Sys_templetfield> geTempletfields(String tableid) {
		
		String sql = "select * from sys_templetfield where tableid=? and accountid='SYSTEM' and sort != -1 order by sort";
		List<Object> values=new ArrayList<Object>();
		
		//获取字段
		values.add(tableid);
		List<Sys_templetfield> fieldList = templetfieldDao.search(sql, values);
		
		return fieldList;
	}

	@Override
	public List<Sys_table> getTableByTempletid(String templetid) {
	
		String sql = "SELECT * FROM sys_table WHERE templetid=?";
		List<Object> values = new ArrayList<Object>();
		//
		values.add(templetid);
		List<Sys_table> tableList = tableDao.search(sql, values);
		
		return tableList;
	}

	@Override
	public Sys_table get(String id) {
		return tableDao.get(id);
	}

	@Override
	public Sys_table selectByWhere(Sys_table table) {
		if (null == table) {
			return null;
		}
		List<Sys_table> tables = tableDao.search(table);
		
		if (null != tables && tables.size() == 1) {
			return tables.get(0);
		}
		return null;
	}

}
