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
	 * @param parentid		父节点id。案卷级不用，如果是文件级，就需要
	 * @param tabletype		表类型  01 or 02
	 * @param searchTxt		查询值
	 * @param pageBean		分页
	 * @return
	 */
	public PageBean<Map<String,Object>> archiveList(String treeid,String parentid,String tabletype,String searchTxt,PageBean<Map<String,Object>> pageBean);
	
	/**
	 * 获取一条档案记录
	 * @param treeid		档案树节点id
	 * @param tabletype		表类型  01 or 02
	 * @param id			记录id
	 * @return
	 */
	public List<Map<String, Object>> getOne(String treeid,String tabletype,String id);
	
	/**
	 * 保存档案数据
	 * @param treeid
	 * @param archiveList
	 * @return
	 */
	public ResultInfo saveArchive(String treeid,String tabletype,List<Map<String,String>> archiveList);

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
