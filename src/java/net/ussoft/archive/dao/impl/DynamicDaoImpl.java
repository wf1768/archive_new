package net.ussoft.archive.dao.impl;

import org.springframework.stereotype.Repository;
import net.ussoft.archive.dao.DynamicDao;
import net.ussoft.archive.model.Dynamic;


@Repository("dynamicDao")
public class DynamicDaoImpl extends BaseDaoMysqlImpl<Dynamic, String> implements
		DynamicDao {

	public DynamicDaoImpl() {
		super(Dynamic.class);
	}

}
