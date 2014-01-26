package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.RoleDao;
import net.ussoft.archive.model.Sys_role;

import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class RoleDaoImpl extends BaseDaoMysqlImpl<Sys_role, String> implements RoleDao {

	public RoleDaoImpl() {
		super(Sys_role.class);
	}
}
