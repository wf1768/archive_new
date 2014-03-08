package net.ussoft.archive.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.service.ITempletService;

import org.springframework.stereotype.Service;

@Service
public class TempletService implements ITempletService {
	
	@Resource
	private TempletDao templetDao;

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
		
		if (null != order && !where.equals("")) {
			sql += order;
		}
		
		return templetDao.search(sql, values);
	}


}
