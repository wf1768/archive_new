package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.DocserverDao;
import net.ussoft.archive.model.Sys_docserver;

import org.springframework.stereotype.Repository;

@Repository("docserverDao")
public class DocserverDaoImpl extends BaseDaoMysqlImpl<Sys_docserver, String> implements DocserverDao {

	public DocserverDaoImpl() {
		super(Sys_docserver.class);
	}
}
