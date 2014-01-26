package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.OrgownerDao;
import net.ussoft.archive.model.Sys_orgowner;

import org.springframework.stereotype.Repository;

@Repository("orgownerDao")
public class OrgownerDaoImpl extends BaseDaoMysqlImpl<Sys_orgowner, String> implements OrgownerDao {

	public OrgownerDaoImpl() {
		super(Sys_orgowner.class);
	}
}
