package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.DocserverDao;
import net.ussoft.archive.model.SysDocserverExample;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.service.IDocserverService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocserverService implements IDocserverService {
	
	@Resource
	private DocserverDao docserverDao;

	@Override
	public Sys_docserver selectById(String id) {
		return docserverDao.get(id);
	}

	@Override
	public List<Sys_docserver> list() {
//		return docserverDao.search("select * from sys_docserver order by servertype asc,servername asc", null);
		//为了排序，放弃getall
		String orderby = "servertype asc,servername asc";
		return docserverDao.getAll(orderby);
	}
	
	@Transactional("txManager")
	@Override
	public Sys_docserver insert(Sys_docserver docserver) {
		return docserverDao.save(docserver);
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_docserver docserver) {
		Sys_docserver tmp = docserverDao.update(docserver);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	@Override
	public Sys_docserver selectByWhere(Sys_docserver docserver) {
		if (null == docserver) {
			return null;
		}
		List<Sys_docserver> serverList = docserverDao.search(docserver);
		
		if (null != serverList && serverList.size() == 1) {
			return serverList.get(0);
		}
		return null;
	}

	@Transactional("txManager")
	@Override
	public int delete(String id) {
		return docserverDao.del(id);
	}
	
	@Transactional("txManager")
	@Override
	public int updateState() {
		String sql = "update sys_docserver set serverstate=0";
		List<Object> values = new ArrayList<Object>();
		return docserverDao.update(sql, values);
	}

}
