package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.dao.AccountDao;
import net.ussoft.archive.dao.FunctionDao;
import net.ussoft.archive.dao.OrgDao;
import net.ussoft.archive.dao.RoleDao;
import net.ussoft.archive.dao.RoleFuntionDao;
import net.ussoft.archive.model.Sys_function;
import net.ussoft.archive.model.Sys_role;
import net.ussoft.archive.model.Sys_role_function;
import net.ussoft.archive.service.IRoleService;
import net.ussoft.archive.util.CommonUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RoleService implements IRoleService {
	
	@Resource
	private RoleDao roleDao;
	@Resource
	private OrgDao orgDao;
	@Resource
	private AccountDao	accountDao;
	@Resource
	private RoleFuntionDao roleFuntionDao;
	@Resource
	private FunctionDao functionDao;

	@Override
	public Sys_role getById(String id) {
		return roleDao.get(id);
	}

	@Override
	public List<Sys_role> list() {
		return roleDao.getAll();
	}
	
	@Override
	public List<Sys_role> list(Sys_role role) {
		return roleDao.search(role);
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_role role) {
		Sys_role tmp = roleDao.update(role);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	@Transactional("txManager")
	@Override
	public Sys_role insert(Sys_role role) {
		return roleDao.save(role);
	}

	@Override
	public Sys_role selectByWhere(Sys_role role) {
		if (null == role) {
			return null;
		}
		List<Sys_role> roleList = roleDao.search(role);
		
		if (null != roleList && roleList.size() == 1) {
			return roleList.get(0);
		}
		return null;
	}

	@Transactional("txManager")
	@Override
	public int delete(String id) {
		//删除角色时，要取消与该角色关联的帐户组、帐户的关联关系。
	    //移除帐户组与角色的关联。
		List<Object> values = new ArrayList<Object>();
		values.add(id);
		String sql = "update sys_org set roleid='' where roleid = ?";
		orgDao.update(sql, values);
		//移除帐户与角色的关联
		sql = "update sys_account set roleid='' where roleid = ?";
		accountDao.update(sql, values);
		//删除角色与功能的关联
		sql = "delete from sys_role_function where roleid =?";
		roleFuntionDao.del(sql, values);
		return roleDao.del(id);
	}
	
	@Override
	public List<Sys_function> searchFunctions(String roleid) {
		if (roleid == null || roleid.equals("")) {
			return null;
		}
		//获取角色id与功能的关联list
		Sys_role_function ref = new Sys_role_function();
		ref.setRoleid(roleid);
		List<Sys_role_function> role_functions = roleFuntionDao.search(ref);
		
		if (role_functions.size() <= 0) {
			return null;
		}
		
//		List<Object> idList = new ArrayList<Object>();
//		for (Sys_role_function ref : role_functions) {
//			idList.add(ref.getFunctionid());
//		}
		
		List<Object> values=new ArrayList<Object>();
		StringBuilder sb=new StringBuilder();
		for (Sys_role_function role_function : role_functions) {
			values.add(role_function.getFunctionid());
			sb.append("?,");
		}
		CommonUtils.deleteLastStr(sb, ",");
		
		//TODO 这里能不能直接sql in list？不用这样拼id in的字符串？
//		List values = new ArrayList();
//		values.add(idList);
		String sql = "select * from sys_function where id in ("+sb.toString()+") order by funorder asc";
		
		List<Sys_function> funList = functionDao.search(sql, values);
		
		return funList;
	}

	@Transactional("txManager")
	@Override
	public void setFunctions(List<String> funList,
			String roleid) {
		//删除roleid与功能对应的连接
		String sql = "delete from sys_role_function where roleid=?";
		List<Object> values = new ArrayList<Object>();
		values.add(roleid);
		roleFuntionDao.del(sql, values);
		
		List<List<Object>> values2 = new ArrayList<List<Object>>();
		for (int i = 0; i < funList.size(); i++) {
			List<Object> tmp = new ArrayList<Object>();
			tmp.add(UUID.randomUUID().toString());
			tmp.add(roleid);
			tmp.add(funList.get(i));
			
			values2.add(tmp);
		}
		
		roleFuntionDao.batchAdd("insert into sys_role_function (id,roleid,functionid) values (?,?,?)", values2);
		
	}


}
