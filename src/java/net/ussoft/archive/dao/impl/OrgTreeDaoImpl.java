package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.OrgTreeDao;
import net.ussoft.archive.model.Sys_org_tree;

import org.springframework.stereotype.Repository;

@Repository("orgtreeDao")
public class OrgTreeDaoImpl extends BaseDaoMysqlImpl<Sys_org_tree, String> implements OrgTreeDao {

	public OrgTreeDaoImpl() {
		super(Sys_org_tree.class);
	}
}
