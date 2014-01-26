package net.ussoft.archive.dao.impl;

import org.springframework.stereotype.Repository;

import net.ussoft.archive.dao.InitDao;
import net.ussoft.archive.model.Sys_init;

@Repository("initDao")
public class InitDaoImpl extends BaseDaoMysqlImpl<Sys_init, String> implements InitDao {

	public InitDaoImpl() {
		super(Sys_init.class);
	}
}
