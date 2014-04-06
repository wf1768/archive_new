package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templetfield;


public interface ITableService {
	
	public Sys_table get(String id);
	
	/**
	 * 获取table全部信息
	 * @return
	 */
	public List<Sys_table> list();
	
	/**
	 * 根据tableid来或者指定表的字段list
	 * @param tableid
	 * @return
	 */
	public List<Sys_templetfield> geTempletfields(String tableid);
	
	/**
	 * 根据templetid获取表
	 * @param templetid
	 * @return
	 * */
	public List<Sys_table> getTableByTempletid(String templetid);
	
	/**
	 * 根据条件获取数据
	 * @param role
	 * @return
	 */
	public Sys_table selectByWhere(Sys_table table);
	
}
