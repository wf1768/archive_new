package net.ussoft.archive.service;

import java.util.List;
import java.util.Map;

import net.ussoft.archive.util.resule.ResultInfo;

public interface IDynamicService {
	
	/**
	 * 保存档案数据
	 * @param treeid
	 * @param archiveList
	 * @return
	 */
	public ResultInfo saveArchive(String treeid,List<Map<String,String>> archiveList);

}
