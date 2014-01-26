package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.OrgDao;
import net.ussoft.archive.model.Sys_org;

import org.springframework.stereotype.Repository;

@Repository("orgDao")
public class OrgDaoImpl extends BaseDaoMysqlImpl<Sys_org, String> implements OrgDao {

	public OrgDaoImpl() {
		super(Sys_org.class);
	}
}
