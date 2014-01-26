package net.ussoft.archive.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.RolegroupDao;
import net.ussoft.archive.model.Sys_rolegroup;
import net.ussoft.archive.service.IRolegroupService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RolegroupService implements IRolegroupService {
	
	@Resource
	private RolegroupDao rolegroupDao;

	@Override
	public Sys_rolegroup selectById(String id) {
		return rolegroupDao.get(id);
	}

	@Override
	public List<Sys_rolegroup> list() {
		return rolegroupDao.getAll("groupsort asc");
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_rolegroup rolegroup) {
		Sys_rolegroup tmp = rolegroupDao.update(rolegroup);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	@Transactional("txManager")
	@Override
	public Sys_rolegroup insert(Sys_rolegroup rolegroup) {
		return rolegroupDao.save(rolegroup);
	}

	@Override
	public Sys_rolegroup selectByWhere(Sys_rolegroup rolegroup) {
		if (null == rolegroup) {
			return null;
		}
		List<Sys_rolegroup> rolegroupList = rolegroupDao.search(rolegroup);
		
		if (null != rolegroupList && rolegroupList.size() == 1) {
			return rolegroupList.get(0);
		}
		return null;
	}

	@Transactional("txManager")
	@Override
	public int delete(String id) {
//		//删除角色时，要取消与该角色关联的帐户组、帐户的关联关系。
//	    //移除帐户组与角色的关联。
//		List<Object> values = new ArrayList<Object>();
//		values.add(id);
//		String sql = "update sys_org set roleid='' where roleid = ?";
//		orgDao.update(sql, values);
//		//移除帐户与角色的关联
//		sql = "update sys_account set roleid='' where roleid = ?";
//		accountDao.update(sql, values);
//		
		return rolegroupDao.del(id);
	}

}
