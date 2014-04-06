package net.ussoft.archive.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.util.resule.ResultInfo;

public interface IDocService {

	/**
	 * 根据id，获取对象
	 * @param id
	 * @return
	 */
	public Sys_doc selectById(String id);
	
	/**
	 * 获取全部信息
	 * @return
	 */
	public List<Sys_doc> list();
	/**
	 * 更新
	 * @param 
	 * @return
	 */
	public int update(Sys_doc doc);
	
	/**
	 * 插入新记录
	 * @param docserver
	 * @return 
	 */
	public Sys_doc insert(Sys_doc doc);
	
	public Sys_doc selectByWhere(Sys_doc doc);
	
	public ResultInfo delete(String id) throws SocketException, IOException;
	
	public List<Sys_doc> exeSql(String sql,List<Object> values);
}
