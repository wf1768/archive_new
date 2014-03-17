package net.ussoft.archive.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import net.ussoft.archive.model.Sys_templet;


public interface ITempletService {
	
	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	public Sys_templet getByid(String id);
	
	/**
	 * 获取templet全部信息
	 * @return
	 */
	public List<Sys_templet> list();
	/**
	 * 直接传where、order查
	 * @param where
	 * @param order
	 * @return
	 */
	public List<Sys_templet> list(String where,List<Object> values,String order);
	/**
	 * 插入新档案类型（可以是档案夹或者档案类型）
	 * @param templet
	 * @return
	 */
	public String insert(Sys_templet templet,String copyTempletid);
	
	/**
	 * 更新
	 * @param templet
	 * @return
	 */
	public int update(Sys_templet templet);
	/**
	 * 移动档案类型
	 * @param id
	 * @param targetid
	 * @return
	 */
	public String move(String id,String targetid);
	/**
	 * 修改排序
	 * @param templet
	 * @return
	 */
	public int sortsave(Sys_templet templet);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 * @throws IOException 
	 * @throws SocketException 
	 */
	public String delete(String id) throws SocketException, IOException;


}
