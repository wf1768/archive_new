package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.CodeDao;
import net.ussoft.archive.model.Sys_code;

import org.springframework.stereotype.Repository;

@Repository("codeDao")
public class CodeDaoImpl extends BaseDaoMysqlImpl<Sys_code, String> implements CodeDao {

	public CodeDaoImpl() {
		super(Sys_code.class);
	}
}
