package net.ussoft.archive.service;

import net.ussoft.archive.util.resule.ResultInfo;

public interface ITempletfieldService {
	
	/**
	 * 获取字段列表
	 * @param treeid	树节点id
	 * @return
	 */
	public ResultInfo getField(String treeid);

}
