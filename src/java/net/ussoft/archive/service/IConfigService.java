package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_config;

public interface IConfigService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_config selectById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_config> list();
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_config config);
	
	public Sys_config selectByWhere(Sys_config config);
	
	/**
	 * 删除电子全文权限代码
	 * @return
	 */
	public int deleteDocAuth(String id);
}
