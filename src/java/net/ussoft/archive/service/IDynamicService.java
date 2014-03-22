package net.ussoft.archive.service;

import java.util.List;
import java.util.Map;

import net.ussoft.archive.model.PageBean;
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

}
