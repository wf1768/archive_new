package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_function;

public interface IFunctionService {

	
	/**
	 * 根据条件，获取功能对象
	 * @param function
	 * @return
	 */
	public Sys_function getFunction(Sys_function function);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_function> list();
}
