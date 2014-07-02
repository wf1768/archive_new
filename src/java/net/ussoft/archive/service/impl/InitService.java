package net.ussoft.archive.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.InitDao;
import net.ussoft.archive.model.Sys_init;
import net.ussoft.archive.service.IInitService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InitService implements IInitService {

	@Resource
	private InitDao initDao;
	@Override
	public Sys_init selectById(String id) {
		return initDao.get(id);
	}

	@Override
	public List<Sys_init> list() {
		return initDao.getAll();
	}

	@Transactional("txManager")
	@Override
	public Sys_init insert(Sys_init init) {
		Sys_init tmp = initDao.save(init);
		return tmp;
	}
	
	@Transactional("txManager")
	@Override
	public int update(Sys_init init) {
		Sys_init tmp = initDao.update(init);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	@Override
	public Sys_init selectByWhere(Sys_init init) {
		if (null == init) {
			return null;
		}
		List<Sys_init> initList = initDao.search(init);
		
		if (null != initList && initList.size() == 1) {
			return initList.get(0);
		}
		return null;
	}

}
