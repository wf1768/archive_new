package net.ussoft.archive.dao.impl;

import net.ussoft.archive.dao.AccountTreeDao;
import net.ussoft.archive.model.Sys_account_tree;

import org.springframework.stereotype.Repository;

@Repository("accounttreeDao")
public class AccountTreeDaoImpl extends BaseDaoMysqlImpl<Sys_account_tree, String> implements AccountTreeDao {

	public AccountTreeDaoImpl() {
		super(Sys_account_tree.class);
	}
}
