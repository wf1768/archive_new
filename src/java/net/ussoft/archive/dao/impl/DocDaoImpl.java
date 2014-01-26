package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.DocDao;
import net.ussoft.archive.model.Sys_doc;

import org.springframework.stereotype.Repository;

@Repository("docDao")
public class DocDaoImpl extends BaseDaoMysqlImpl<Sys_doc, String> implements DocDao {

	public DocDaoImpl() {
		super(Sys_doc.class);
	}
}
