package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.ConfigDao;
import net.ussoft.archive.model.Sys_config;

import org.springframework.stereotype.Repository;

@Repository("configDao")
public class ConfigDaoImpl extends BaseDaoMysqlImpl<Sys_config, String> implements ConfigDao {

	public ConfigDaoImpl() {
		super(Sys_config.class);
	}
}
