package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.RolegroupDao;
import net.ussoft.archive.model.Sys_rolegroup;

import org.springframework.stereotype.Repository;

@Repository("rolegroupDao")
public class RolegroupDaoImpl extends BaseDaoMysqlImpl<Sys_rolegroup, String> implements RolegroupDao {

	public RolegroupDaoImpl() {
		super(Sys_rolegroup.class);
	}
}
