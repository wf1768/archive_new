package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.RoleFuntionDao;
import net.ussoft.archive.model.Sys_role_function;

import org.springframework.stereotype.Repository;

@Repository("rolefunctionDao")
public class RoleFunctionDaoImpl extends BaseDaoMysqlImpl<Sys_role_function, String> implements RoleFuntionDao {

	public RoleFunctionDaoImpl() {
		super(Sys_role_function.class);
	}
}
