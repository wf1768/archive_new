package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_function;
import net.ussoft.archive.model.Sys_role;

public interface IRoleService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_role getById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_role> list();
	/**
	 * 根据条件，获取全部信息
	 * @return
	 */
	public List<Sys_role> list(Sys_role role);
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_role role);
	
	/**
	 * 插入新记录
	 * @param docserver
	 * @return 
	 */
	public Sys_role insert(Sys_role role);
	
	/**
	 * 根据条件获取数据
	 * @param role
	 * @return
	 */
	public Sys_role selectByWhere(Sys_role role);
	
	/**
	 * 删除一条记录
	 * @param id
	 * @return
	 */
	public int delete(String id);
	
	/**
	 * 获取角色对应的功能实体list
	 * @param roleid		角色id
	 * @return
	 */
	public List<Sys_function> searchFunctions(String roleid);
	
	/**
	 * 为角色赋权
	 * @param funList
	 * @param roleid
	 */
	public void setFunctions(List<String> funList,String roleid);
	
	/**
	 * 当前帐户对某个功能，是否有权限。主要为主页main的快捷方式，单个获取，主页显示哪些功能快捷方式图标
	 * @param funid
	 * @return
	 */
	public Boolean getRoleFun(String funid);
}
