package net.ussoft.archive.service;

import java.util.List;

import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;


public interface ITableService {
	
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
	


}
