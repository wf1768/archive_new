package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.dao.CodeDao;
import net.ussoft.archive.dao.ConfigDao;
import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.service.IConfigService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ConfigService implements IConfigService {
	
	@Resource
	private ConfigDao configDao;
	@Resource
	private CodeDao codeDao;
	@Resource
	private OrgTreeDao orgTreeDao;
	@Resource
	private AccountTreeDao accountTreeDao;

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

	@Transactional("txManager")
	@Override
	public int deleteDocAuth(String id) {
		if (null == id || id.equals("")) {
			return 0;
		}
		//删除组、帐户关联的代码id
		String sql = "UPDATE SYS_ORG_TREE SET DOCAUTH='' WHERE DOCAUTH=?";
		List<Object> values = new ArrayList<Object>();
		values.add(id);
		orgTreeDao.update(sql, values);
		
		sql = "UPDATE SYS_ACCOUNT_TREE SET DOCAUTH='' WHERE DOCAUTH=?";
		accountTreeDao.update(sql, values);
		
		return codeDao.del(id);
	}

}
