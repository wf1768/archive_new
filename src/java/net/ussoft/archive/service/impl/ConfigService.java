package net.ussoft.archive.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.ConfigDao;
import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.service.IConfigService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ConfigService implements IConfigService {
	
	@Resource
	private ConfigDao configDao;

	@Override
	public Sys_config selectById(String id) {
		return configDao.get(id);
	}

	@Override
	public List<Sys_config> list() {
		return configDao.getAll();
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_config config) {
		Sys_config tmp = configDao.update(config);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	@Override
	public Sys_config selectByWhere(Sys_config config) {
		if (null == config) {
			return null;
		}
		List<Sys_config> configList = configDao.search(config);
		
		if (null != configList && configList.size() == 1) {
			return configList.get(0);
		}
		return null;
	}

}
