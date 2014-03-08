package net.ussoft.archive.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.CodeDao;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.service.ICodeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CodeService implements ICodeService {
	
	@Resource
	private CodeDao codeDao;

	@Override
	public Sys_code selectById(String id) {
		return codeDao.get(id);
	}

	@Override
	public List<Sys_code> list() {
		return codeDao.getAll();
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_code code) {
		Sys_code tmp = codeDao.update(code);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	@Override
	public List<Sys_code> selectByWhere(Sys_code code) {
		if (null == code) {
			return null;
		}
		List<Sys_code> codeList = codeDao.search(code);
		
		return codeList;
	}

	@Transactional("txManager")
	@Override
	public Sys_code insert(Sys_code code) {
		if (null == code) {
			return null;
		}
		return codeDao.save(code);
	}
	@Transactional("txManager")
	@Override
	public int delete(String id) {
		return codeDao.del(id);
	}

	@Override
	public List<Sys_code> list(String where, List<Object> values, String order) {
		String sql = "select * from sys_code ";
		if (null != where && !where.equals("")) {
			sql += where;
		}
		
		if (null != order && !where.equals("")) {
			sql += order;
		}
		
		return codeDao.search(sql, values);
	}

}
