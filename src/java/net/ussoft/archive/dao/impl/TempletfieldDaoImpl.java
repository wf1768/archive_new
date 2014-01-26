package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.model.Sys_templetfield;

import org.springframework.stereotype.Repository;

@Repository("templetfieldDao")
public class TempletfieldDaoImpl extends BaseDaoMysqlImpl<Sys_templetfield, String> implements TempletfieldDao {

	public TempletfieldDaoImpl() {
		super(Sys_templetfield.class);
	}
}
