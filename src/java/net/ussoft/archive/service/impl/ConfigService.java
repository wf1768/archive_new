package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	public List<Sys_config> list(String accountid) {
		Sys_config config = new Sys_config();
		config.setAccountid(accountid);
		return configDao.search(config);
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

	@Transactional("txManager")
	@Override
	public Sys_config insert(Sys_config config) {
		if (null == config) {
			return null;
		}
		return configDao.save(config);
	}

	@Transactional("txManager")
	@Override
	public int delete(String id) {
		return configDao.del(id);
	}

	@Transactional("txManager")
	@Override
	public int deleteByWhere(String where) {
		
		String sql = "delete from sys_config ";
		if (null != where && !where.equals("")) {
			sql += "where " + where;
		}
		else {
			return 0;
		}
		List<Object> values = new ArrayList<Object>();
		return configDao.del(sql, values);
		
	}

	@Transactional("txManager")
	@Override
	public List<Sys_config> getAccountConfig(String accountid) {
		
		Sys_config config = new Sys_config();
		config.setAccountid(accountid);
		List<Sys_config> configs = configDao.search(config);
		
		if (null == configs || configs.size() == 0) {
			//创建帐户自己的配置文件
			Sys_config config1 = new Sys_config();
			config.setConfigkey("PAGE");
			config.setAccountid("SYSTEM");
			config = selectByWhere(config);
			config.setId(UUID.randomUUID().toString());
			config.setAccountid(accountid);
			insert(config);
			
			//创建字段截取字数config
			Sys_config config2 = new Sys_config();
			config2.setId(UUID.randomUUID().toString());
			config2.setAccountid(accountid);
			config2.setConfigkey("SUBSTRING");
			config2.setConfigmemo("列表显示截取后文字数");
			config2.setConfigname("截取文字显示");
			config2.setConfigvalue("8");
			insert(config2);
			configs = configDao.search(config);
		}
		return configs;
		
	}

}
