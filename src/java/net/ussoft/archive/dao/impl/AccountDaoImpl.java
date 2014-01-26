package net.ussoft.archive.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.model.Sys_account;

@Repository("accountDao")
public class AccountDaoImpl extends BaseDaoMysqlImpl<Sys_account, String> implements AccountDao {

	public AccountDaoImpl() {
		super(Sys_account.class);
	}
}
