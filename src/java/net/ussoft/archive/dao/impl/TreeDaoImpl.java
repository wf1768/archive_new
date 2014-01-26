package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_tree;

import org.springframework.stereotype.Repository;

@Repository("treeDao")
public class TreeDaoImpl extends BaseDaoMysqlImpl<Sys_tree, String> implements TreeDao {

	public TreeDaoImpl() {
		super(Sys_tree.class);
	}
}
