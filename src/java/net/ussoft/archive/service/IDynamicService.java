package net.ussoft.archive.service;

import java.util.List;
import java.util.Map;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.util.resule.ResultInfo;

public interface IDynamicService {
	
	/**
	 * 获取档案数据。
	 * @param treeid		档案树节点
	 * @param tabletype		表类型  01 or 02
	 * @return
	 */
	public PageBean<Map<String,Object>> archiveList(String treeid,String tabletype,PageBean<Map<String,Object>> pageBean);
	
	/**
	 * 保存档案数据
	 * @param treeid
	 * @param archiveList
	 * @return
	 */
	public ResultInfo saveArchive(String treeid,List<Map<String,String>> archiveList);

	/**
	 * 查询表数据
	 * @param searchTxt
	 * @param tablename
	 * @param treeid
	 * @param tmpFieldLis
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * */
	public PageBean<Map<String, Object>> search(String searchTxt,String tablename,String treeid,List<Sys_templetfield> tmpFieldLis,int currentPage,int pageSize);
	
}
