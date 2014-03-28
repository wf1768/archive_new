package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_code;
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
	 * 插入
	 * @param org
	 * @return
	 */
	public Sys_config insert(Sys_config config);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int delete(String id);
	
	/**
	 * 根据条件删除
	 * @param where		为where字符串 。例如 “accountid='1'”
	 * @return
	 */
	public int deleteByWhere(String where);
	
	/**
	 * 根据帐户id获取配置。accountid＝‘SYSTEM’的表示系统配置
	 * @param accountid
	 * @return
	 */
	public List<Sys_config> list(String accountid);
	
	/**
	 * 设置帐户自己的配置，判断帐户自己的配置有没有，如果没有，就创建，有就返回list
	 * @param accountid
	 * @return
	 */
	public List<Sys_config> getAccountConfig(String accountid);
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
