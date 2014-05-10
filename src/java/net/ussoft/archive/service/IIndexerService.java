package net.ussoft.archive.service;

import java.util.HashMap;
import java.util.List;

import net.ussoft.archive.model.Sys_doc;

/**
 * 索引操作专用服务
 * @author guodh
 *
 */
public interface IIndexerService {
	
	//创建索引     ........... 本次没有用
	public void createIndex(String tablename,List fieldList,List dataList,String openMode);
	
	//创建电子全文索引
	public String createIndex(String docServerid,List<Sys_doc> docList,HashMap<String,String> contentMap,String openMode);
}
