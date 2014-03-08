package net.ussoft.archive.service;

import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.util.resule.ResultInfo;

public interface ITempletfieldService {
	
	/**
	 * 根据id获取对象
	 * @param id
	 * @return
	 */
	public Sys_templetfield getById(String id);
	
	/**
	 * 获取字段列表
	 * @param treeid	树节点id
	 * @return
	 */
	public ResultInfo getField(String treeid);
	
	/**
	 * 插入
	 * @param templetfield
	 * @return
	 */
	public String insert(Sys_templetfield templetfield);
	
	/**
	 * 更新
	 * @param templetfield
	 * @return
	 */
	public int update(Sys_templetfield templetfield);
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int delete(String id);
	/**
	 * 移动字段排序
	 * @param id	字段id
	 * @param type	‘up’向上移动排序，‘down’向下移动排序
	 * @return
	 */
	public Boolean sort(String id,String type);
	
	/**
	 * 修改字段的一些属性，例如检索字段、列表显示的快捷修改
	 * @param id		field id
	 * @param type		"issearch" "isgridshow"
	 * @param value		0 or 1
	 * @return
	 */
	public Boolean updateOtherInfo(String id,String type,Integer value);
	/**
	 * 粘贴字段
	 * @param fieldid
	 * @param targetid
	 * @return
	 */
	public Boolean fieldpaste(String fieldid,String tableid);
	
	/**
	 * 新建字段的代码项
	 * @param id
	 * @param columndata
	 * @return
	 */
	public Boolean insertFieldCode(String id,String columndata);
	
	/**
	 * 删除字段的代码项
	 * @param id
	 * @return
	 */
	public Boolean delCode(String id);
	
	/**
	 * 移动字段代码排序
	 * @param id	字段代码id
	 * @param type	‘up’向上移动排序，‘down’向下移动排序
	 * @return
	 */
	public Boolean sortcode(String id,String type);
	
	/**
	 * 粘贴字段代码
	 * @param fieldid	复制的字段id
	 * @param targetid  粘贴的字段id
	 * @return
	 */
	public Boolean pastecode(String fieldid,String targetid);
	

}
