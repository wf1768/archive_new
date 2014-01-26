package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.model.Sys_templet;

import org.springframework.stereotype.Repository;

@Repository("templetDao")
public class TempletDaoImpl extends BaseDaoMysqlImpl<Sys_templet, String> implements TempletDao {

	public TempletDaoImpl() {
		super(Sys_templet.class);
	}
}
