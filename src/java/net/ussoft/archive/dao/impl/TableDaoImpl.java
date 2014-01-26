package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.model.Sys_table;

import org.springframework.stereotype.Repository;

@Repository("tableDao")
public class TableDaoImpl extends BaseDaoMysqlImpl<Sys_table, String> implements TableDao {

	public TableDaoImpl() {
		super(Sys_table.class);
	}
}
