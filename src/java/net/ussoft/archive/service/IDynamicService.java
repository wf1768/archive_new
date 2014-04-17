package net.ussoft.archive.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.util.resule.ResultInfo;

public interface IDynamicService {
	
	/**
	 * 获取档案数据。
	 * @param treeid		档案树节点
	 * @param allwj			是否是全文件显示。
	 * @param parentid		父节点id。案卷级不用，如果是文件级，就需要
	 * @param tabletype		表类型  01 or 02
	 * @param searchTxt		查询值
	 * @param status		档案状态
	 * @param pageBean		分页
	 * @return
	 */
	public PageBean<Map<String,Object>> archiveList(String treeid,Boolean allwj,String parentid,String tabletype,String searchTxt,Integer status,PageBean<Map<String,Object>> pageBean);
	
	/**
	 * 获取档案记录
	 * @param treeid		档案树节点id
	 * @param parentid		父节点id。在tabletype为02时，并且parentid不为null时，增加parentid为查询条件
	 * @param tabletype		表类型  01 or 02
	 * @param idList		记录id集合list
	 * @param orderby		排序规则
	 * @param status		档案状态
	 * @return
	 */
	public List<Map<String, Object>> get(String treeid,String parentid,String tabletype,List<String> idList,String orderby,Integer status,String searchTxt);
	
	/**
	 * 保存档案数据
	 * @param treeid		
	 * @param archiveList
	 * @return
	 */
	public ResultInfo insertArchive(Map<String, String> sysFieldMap,List<Map<String,String>> archiveList);
	
	/**
	 * 更新档案数据
	 * @param tabletype			表类型
	 * @param archiveList
	 * @return
	 */
	public ResultInfo updaetArchive(String tabletype,List<Map<String,String>> archiveList);
	
	/**
	 * 替换
	 * @param treeid		treeid
	 * @param tabletype		表类型
	 * @param ids			要替换的档案id list
	 * @param tc_th_field	要替换的字段名
	 * @param th_key		需要替换的字符
	 * @param th_value		替换的字符
	 * @return
	 */
	public ResultInfo updateReplace(String treeid,String tabletype,List<String> ids,String tc_th_field,String th_key,String th_value);
	
	/**
	 * 高级更改     字段 ＝ 字段 ＋ “字符” ＋ 字段
	 * @param treeid
	 * @param tabletype
	 * @param ids
	 * @param tc_th_field
	 * @param firstField
	 * @param txt
	 * @param second_field
	 * @return
	 */
	public ResultInfo updateHigh(String treeid,String tabletype,List<String> ids,String tc_th_field,String firstField,String txt,String secondField );
	
	/**
	 * 序列流水更改   1、2、3、4  递增
	 * @param treeid
	 * @param tabletype
	 * @param ids				要修改的档案记录id list
	 * @param tc_th_field		要修改的字段
	 * @param xl_begin			起始值
	 * @param xl_size			步长
	 * @return
	 */
	public ResultInfo updateSequence(String treeid,String tabletype,List<String> ids,String tc_th_field,Integer xl_begin,Integer xl_size);
	
	/**
	 * 删除档案
	 * @param treeid
	 * @param tabletype
	 * @param ids
	 * @return
	 * @throws IOException 
	 * @throws SocketException 
	 */
	public ResultInfo deleteArchive(String treeid,String tabletype,List<String> ids) throws SocketException, IOException;

	/**
	 * 删除电子全文
	 * @param docs
	 * @return
	 * @throws IOException 
	 * @throws SocketException 
	 */
	public ResultInfo deleteDoc(List<Sys_doc> docs) throws SocketException, IOException;
	
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
	
	public void exeSql(String sql);
	
}
