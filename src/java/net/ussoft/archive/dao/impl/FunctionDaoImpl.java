package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.FunctionDao;
import net.ussoft.archive.model.Sys_function;

import org.springframework.stereotype.Repository;

@Repository("functionDao")
public class FunctionDaoImpl extends BaseDaoMysqlImpl<Sys_function, String> implements FunctionDao {

	public FunctionDaoImpl() {
		super(Sys_function.class);
	}
}
