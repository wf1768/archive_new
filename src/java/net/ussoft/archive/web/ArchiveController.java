package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.ICodeService;
import net.ussoft.archive.service.IConfigService;
import net.ussoft.archive.service.IDocService;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITempletfieldService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

/**
 * 档案管理
 * @author wangf
 *
 */

@Controller
@RequestMapping(value="archive")
public class ArchiveController extends BaseConstroller {
	
	@Resource
	private ITreeService treeService;
	@Resource
	private IDynamicService dynamicService;
	@Resource
	private IAccountService accountService;
	@Resource
	private IConfigService configService;
	@Resource
	private ITempletfieldService templetfieldService;
    @Autowired  
    private  HttpServletRequest request;  
    @Resource
    private ICodeService codeService;
    
    @Resource
    private IDocService docService;
    @Resource
    private IOrgService orgService;
    @Resource
    private IEncryService encryService;
    @Resource
    private ITableService tableService;
	
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView index(ModelMap modelMap) {
		modelMap = super.getModelMap("ARCHIVEMANAGE","");
		return new ModelAndView("/view/archive/index",modelMap);
	}
	
	
	/**
	 * 档案管理
	 * @param selectid		前台选择的treeid
	 * @param tabletype		表类型  01 or 02
	 * @param page			当前页数
	 * @param searchTxt		检索值
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String treeid,String parentid,String tabletype,Integer page,String searchTxt,ModelMap modelMap) {
		
		if (null == page) {
			page = 1;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			tabletype = "01";
		}
		
		if (null == searchTxt || searchTxt.trim().equals("")) {
			searchTxt = "";
		}
        
		modelMap = super.getModelMap("ARCHIVEMANAGE","ARCHIVE");
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		//根据当前帐户id，获取帐户的档案树节点，用来档案管理里画档案树
		List<Sys_tree> trees = treeService.getAuthTree(account.getId());
		String treeJson = "";
		if (null != trees && trees.size() >0 ) {
			treeJson = JSON.toJSONString(trees);
		}
		
		treeJson = treeService.createTreeJson(treeJson, getProjectBasePath());
		
		modelMap.put("result", treeJson);
		modelMap.put("searchTxt", searchTxt);
		
		//获取点击后页面右侧档案类型列表
		PageBean<Map<String, Object>> pageBean = new PageBean<Map<String,Object>>();
		if (null == treeid || treeid.equals("")) {
			treeid = "0";
		}
		else {
			//获取每页条数(首先获取帐户自己的页数配置，如果没有设置，读取系统配置)
			HashMap<String, Object> configMap = getConfig(account.getId());
			//每页行数
			Integer pageSize = 30;
			//字段截取标准。（列表里字段长度超过标准，被截取)
			Integer subString = 10;
			if (null == configMap || configMap.size() == 0) {
				configMap = getConfig("SYSTEM");
				pageSize = Integer.parseInt(configMap.get("PAGE").toString());
			}
			else {
				pageSize = Integer.parseInt(configMap.get("PAGE").toString());
				subString = Integer.parseInt(configMap.get("SUBSTRING").toString());
			}
			//防止数字错误
			if (pageSize < 0) {
				pageSize = 30;
			}
			if (subString < 0) {
				subString = 10;
			}
			//用来前台判断文字截取多少个
			modelMap.put("subString", subString);
			
			//获取档案字段(先取帐户自己的字段配置，如果没有，获取系统的)
			List<Sys_templetfield> fieldList = getTempletfields(treeid, tabletype);
//			List<Sys_templetfield> fieldList = new ArrayList<Sys_templetfield>();
//			fieldList = treeService.geTempletfields(treeid, tabletype,account.getId());
			
			//如果是文件级，获取对应的案卷级，供文件级页面显示
			if (tabletype.equals("02")) {
				List<Sys_templetfield> ajFieldList = treeService.geTempletfields(treeid, "01",account.getId());
				//如果帐户私有字段为空
				if (null == ajFieldList || ajFieldList.size() == 0) {
					ajFieldList = treeService.geTempletfields(treeid, "01");
				}
				
				modelMap.put("ajFieldList", ajFieldList);
				//获取aj级信息
				List<String> idList = new ArrayList<String>();
				idList.add(parentid);
				List<Map<String, Object>> maps = dynamicService.get(treeid,"", "01", idList,null,null);
				modelMap.put("maps", maps);
				modelMap.put("parentid", parentid);
				
				Integer page_aj = Integer.valueOf(request.getParameter("page_aj").toString());
				String searchTxt_aj = request.getParameter("searchTxt_aj");
				modelMap.put("page_aj", page_aj);
				modelMap.put("searchTxt_aj", searchTxt_aj);
				
			}
			
//			if (null == fieldList || fieldList.size() == 0) {
//				fieldList = treeService.geTempletfields(treeid, tabletype);
//			}
			
			//获取排序规则
//			String orderbyString = "";
//			for (Sys_templetfield field : fieldList) {
//				if (null != field.getOrderby() && !field.getOrderby().equals("")) {
//					if (field.getFieldtype().equals("INT")) {
//						orderbyString += field.getEnglishname() + " " + field.getOrderby() + ",";
//					}
//					else {
//						if (field.getOrderby().equals("GBK")) {
//							orderbyString += "CONVERT("+field.getEnglishname()+" USING gbk),";
//						}
//						else {
//							orderbyString += field.getEnglishname() + " " + field.getOrderby() + ",";
//						}
//					}
//				}
//			}
//			if (orderbyString.length() > 0) {
//				orderbyString = orderbyString.substring(0, orderbyString.length()-1);
//				pageBean.setOrderBy(orderbyString);
//			}
//			else {
//				pageBean.setOrderBy("createtime desc");
//			}
			pageBean.setOrderBy(getOrderby(fieldList));
			
			
			if (pageSize == 0) {
				pageBean.setIsPage(false);
			}
			else {
				pageBean.setIsPage(true);
				pageBean.setPageSize(pageSize);
				pageBean.setPageNo(page);
			}
			
			//获取当前treeid下数据
			pageBean = dynamicService.archiveList(treeid,parentid,tabletype, searchTxt,0,pageBean);
			modelMap.put("fields", fieldList);
			
			Sys_tree tree = treeService.getById(treeid);
			modelMap.put("treename", tree.getTreename());
		}
		modelMap.put("pagebean", pageBean);
		
		//获取templet
		Sys_templet templet = treeService.getTemplet(treeid);
		modelMap.put("templet", templet);
		modelMap.put("selectid", treeid);
		
		
		//返回的url。返回案卷页，或是文件级页
		String url = "/view/archive/archive/list";
		if (null != templet && templet.getTemplettype().equals("F")) {
			url = "/view/archive/archive/list_wj";
		}
		if (tabletype.equals("02")) {
			url = "/view/archive/archive/list_wj";
		}
		
		return new ModelAndView(url,modelMap);
	}
	
	/**
	 * 获取字段
	 * @param treeid
	 * @param tabletype
	 * @return
	 */
	private List<Sys_templetfield> getTempletfields(String treeid,String tabletype) {
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		
		//获取档案字段(先取帐户自己的字段配置，如果没有，获取系统的)
		List<Sys_templetfield> fieldList = new ArrayList<Sys_templetfield>();
		fieldList = treeService.geTempletfields(treeid, tabletype,account.getId());
		
		if (null == fieldList || fieldList.size() == 0) {
			fieldList = treeService.geTempletfields(treeid, tabletype);
		}
		return fieldList;
	}
	
	private String getOrderby(List<Sys_templetfield> fieldList) {
		//获取排序规则
		String orderbyString = "";
		for (Sys_templetfield field : fieldList) {
			if (null != field.getOrderby() && !field.getOrderby().equals("")) {
				if (field.getFieldtype().equals("INT")) {
					orderbyString += field.getEnglishname() + " " + field.getOrderby() + ",";
				}
				else {
					if (field.getOrderby().equals("GBK")) {
						orderbyString += "CONVERT("+field.getEnglishname()+" USING gbk),";
					}
					else {
						orderbyString += field.getEnglishname() + " " + field.getOrderby() + ",";
					}
				}
			}
		}
		if (orderbyString.length() > 0) {
			orderbyString = orderbyString.substring(0, orderbyString.length()-1);
		}
		else {
			orderbyString = "createtime desc";
		}
		return orderbyString;
	}
	
	
	/**
	 * 打开添加页面
	 * @param	treeid		树节点id
	 * @param	status		状态值，案卷级-状态：0为正常，1为组卷  。文件级-状态：0为正常，1为组卷，2为零散文件
	 * @param	parentid	如果是案卷，没有用。如果是文件，案卷级的id
	 * @param	tabletype	01 or 02
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(String treeid,Integer status,String parentid,String tabletype,ModelMap modelMap) {
		
		if (null == tabletype || tabletype.equals("")) {
			tabletype = "01";
		}
		
		List<Sys_templetfield> fieldList = getTempletfields(treeid, tabletype);
//		//获取当前session登录帐户
//		Sys_account account = getSessionAccount();
//		
//		//获取档案字段(先取帐户自己的字段配置，如果没有，获取系统的)
//		List<Sys_templetfield> fieldList = new ArrayList<Sys_templetfield>();
//		fieldList = treeService.geTempletfields(treeid, tabletype,account.getId());
//		
//		if (null == fieldList || fieldList.size() == 0) {
//			fieldList = treeService.geTempletfields(treeid, tabletype);
//		}
		
		//获取字段代码，前台页面上生成select
		Map<String, List<Sys_code>> codeMap =  getFieldCode(fieldList);
		modelMap.put("codeMap", codeMap);
		modelMap.put("fields", fieldList);
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		if (null == status || status < 0) {
			status = 0;
		}
		modelMap.put("status", status);
		if (null != parentid && !parentid.equals("")) {
			modelMap.put("parentid", parentid);
		}
		
		String resultsString = JSON.toJSONString(fieldList);
		modelMap.put("fieldjson", resultsString);
		
		return "/view/archive/archive/add";
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(String data,String sys,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		@SuppressWarnings("unchecked")
		//档案数据
		Map<String, String> archiveMap = (Map<String, String>) JSON.parse(data);
		//档案系统字段数据
		@SuppressWarnings("unchecked")
		Map<String, String> sysFieldMap = (Map<String, String>) JSON.parse(sys);
		
		List<Map<String, String>> archiveList = new ArrayList<Map<String,String>>();
		archiveList.add(archiveMap);
		ResultInfo info = dynamicService.saveArchive(sysFieldMap, archiveList);
		
		out.print(info.getMsg());
	}
	
	/**
	 * 打开编辑页面
	 * @param treeid		档案树节点id
	 * @param tabletype		表类型  01 or 02
	 * @param id			档案记录id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView edit(String treeid,String tabletype,String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
//		//获取当前session登录帐户
//		Sys_account account = getSessionAccount();
//				
//		List<Sys_templetfield> fields = treeService.geTempletfields(treeid, tabletype,account.getId());
//		//如果帐户私有字段为空
//		if (null == fields || fields.size() == 0) {
//			fields = treeService.geTempletfields(treeid, tabletype);
//		}
		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		
		modelMap.put("fields", fields);
		
		//获取信息
		List<String> idList = new ArrayList<String>();
		idList.add(id);
		List<Map<String, Object>> maps = dynamicService.get(treeid,"", tabletype, idList,null,null);
		modelMap.put("maps", maps);
		
		String resultsString = JSON.toJSONString(fields);
		modelMap.put("fieldjson", resultsString);
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		
		//获取字段代码，前台页面上生成select
		Map<String, List<Sys_code>> codeMap =  getFieldCode(fields);
		modelMap.put("codeMap", codeMap);
		
		//获取对象
		return new ModelAndView("/view/archive/archive/edit",modelMap);
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public void update(String data,String tabletype,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) JSON.parse(data);
		
		List<Map<String, String>> archiveList = new ArrayList<Map<String,String>>();
		archiveList.add(map);
		ResultInfo info = dynamicService.updaetArchive(tabletype, archiveList);
		
		out.print(info.getMsg());
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void delete(String treeid,String tabletype,String ids,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		if (null == ids || ids.equals("")) {
			out.print("未获得要删除的数据，请重新操作或与管理员联系。");
			return;
		}
		
		String[] idsStrings = ids.split(",");
		
		List<String> list = Arrays.asList(idsStrings);
		
		ResultInfo info = dynamicService.deleteArchive(treeid, tabletype, list);
		
		out.print(info.getMsg());
	}
	
	@RequestMapping(value="/show",method=RequestMethod.GET)
	public ModelAndView show(String treeid,String tabletype,String id,ModelMap modelMap) throws Exception {
		//判断id是否存在
		if (null == id || id.equals("")) {
			return null;
		}
//		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
//		
//		List<Sys_templetfield> fields = treeService.geTempletfields(treeid, tabletype,account.getId());
		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		
		modelMap.put("fields", fields);
		//获取档案信息
		List<String> idList = new ArrayList<String>();
		idList.add(id);
		List<Map<String, Object>> maps = dynamicService.get(treeid,"", tabletype, idList,null,null);
		
		
		if (null == maps || maps.size() == 0) {
			return null;
		}
		modelMap.put("maps", maps);
		Sys_account_tree account_tree = accountService.getTreeAuth(account.getId(), treeid);
		if (null == account_tree) {
			Sys_org_tree org_tree = orgService.getTreeAuth(account.getOrgid(), treeid);
			modelMap.put("treeauth", org_tree);
		}
		else {
			modelMap.put("treeauth", account_tree);
		}
		
		//获取docauth的code，用于前台doc列表填充
		Sys_code code = new Sys_code();
		code.setTempletfieldid("DOCAUTH");
		List<Sys_code> codes = codeService.selectByWhere(code);
		HashMap<String, String> codeMap = new HashMap<String, String>();
		for (Sys_code sys_code : codes) {
			codeMap.put(sys_code.getId(), sys_code.getColumndata());
		}
		modelMap.put("codeMap", codeMap);
		
		String resultsString = JSON.toJSONString(fields);
		modelMap.put("fieldjson", resultsString);
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		
		Sys_tree tree = treeService.getById(treeid);
		
		//获取table
		Sys_table table = new Sys_table();
		table.setTempletid(tree.getTempletid());
		table.setTabletype(tabletype);
		table = tableService.selectByWhere(table);
		
		List<Sys_doc> docs = new ArrayList<Sys_doc>();
		//获取档案的电子全文  TODO 要获取当前帐户电子全文权限范围的
		if (maps.get(0).get("isdoc").toString().equals("1")) {
			String sql = "select * from sys_doc where tableid=? and fileid=?";
			List<Object> values = new ArrayList<Object>();
			values.clear();
			values.add(table.getId());
			values.add(id);
			docs = docService.exeSql(sql, values);
		}
		Boolean isFileShow = false;
		//获取有没有全文浏览器，如果没有，前台不显示查看按钮
		if (encryService.getInit(24)) {
			isFileShow = true;
		}
		modelMap.put("isFileShow", isFileShow);
		
		modelMap.put("docs", docs);
		
		//获取对象
		return new ModelAndView("/view/archive/archive/show",modelMap);
	}
	
	@RequestMapping(value="/doc",method=RequestMethod.GET)
	public ModelAndView doc(String treeid,String tabletype,String id,ModelMap modelMap) throws Exception {
		//判断id是否存在
		if (null == id || id.equals("")) {
			return null;
		}
		
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		
		Boolean singleArchive = false;
		String resultUrl = "";
		
		String[] idArr = id.split(",");
		//如果就一个档案，转到单个档案全文页面
		if (idArr.length == 1) {
			singleArchive = true;
			resultUrl = "/view/archive/archive/doc_single";
		}
		else {
			//多个档案，转到批量挂接页面
			resultUrl = "/view/archive/archive/doc_multiple";
		}
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		Sys_tree tree = treeService.getById(treeid);
		
		//获取table
		Sys_table table = new Sys_table();
		table.setTempletid(tree.getTempletid());
		table.setTabletype(tabletype);
		table = tableService.selectByWhere(table);
		
		
		//获取档案字段(先取帐户自己的字段配置，如果没有，获取系统的)
//		List<Sys_templetfield> fieldList = new ArrayList<Sys_templetfield>();
//		fieldList = treeService.geTempletfields(treeid, tabletype,account.getId());
//		
//		if (null == fieldList || fieldList.size() == 0) {
//			fieldList = treeService.geTempletfields(treeid, tabletype);
//		}
		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		
//		//获取排序规则
//		String orderbyString = "";
//		for (Sys_templetfield field : fields) {
//			if (null != field.getOrderby() && !field.getOrderby().equals("")) {
//				if (field.getFieldtype().equals("INT")) {
//					orderbyString += field.getEnglishname() + " " + field.getOrderby() + ",";
//				}
//				else {
//					if (field.getOrderby().equals("GBK")) {
//						orderbyString += "CONVERT("+field.getEnglishname()+" USING gbk),";
//					}
//					else {
//						orderbyString += field.getEnglishname() + " " + field.getOrderby() + ",";
//					}
//				}
//			}
//		}
//		if (orderbyString.length() > 0) {
//			orderbyString = " order by " + orderbyString.substring(0, orderbyString.length()-1);
//		}
//		else {
//			orderbyString = " order by createtime desc";
//		}
		
		String orderbyString = getOrderby(fields);
		
		//获取档案信息
		List<String> idList = Arrays.asList(idArr);
		List<Map<String, Object>> maps = dynamicService.get(treeid, "",tabletype, idList,orderbyString,null);
		modelMap.put("maps", maps);
//		modelMap.put("maps_json", JSON.toJSON(maps));
		
		//解决档案信息字符转为json后，特殊字符问题。
		List<Map<String, Object>> tmpMaps = new ArrayList<Map<String,Object>>();
		tmpMaps = doMaps(fields,maps);
//		for (Map<String, Object> map : maps) {
//			for (Sys_templetfield field : fields) {
//				if (!field.getFieldtype().equals("INT") && field.getSort() >= 0) {
//					Object object = map.get(field.getEnglishname());
//					String tmpStr = "";
//					if (null != object) {
//						tmpStr = object.toString();
//					}
//					
//					if (null != tmpStr && !"".equals(tmpStr)) {
//						tmpStr = tmpStr.replaceAll("\\\\","\\\\\\\\");
//						tmpStr = tmpStr.replace("'","\\\'");
//						tmpStr = tmpStr.replace("\"","\\\"");
//						tmpStr = tmpStr.replaceAll("[\\t\\n\\r]", "");
//					}
//					map.put(field.getEnglishname(), tmpStr);
//				}
//			}
//			tmpMaps.add(map);
//		}
		
		modelMap.put("maps_json", JSON.toJSON(tmpMaps));
		
		//获取每页条数(首先获取帐户自己的页数配置，如果没有设置，读取系统配置)
		HashMap<String, Object> configMap = getConfig(account.getId());
		//字段截取标准。（列表里字段长度超过标准，被截取)
		Integer subString = 10;
		if (null == configMap || configMap.size() == 0) {
		}
		else {
			subString = Integer.parseInt(configMap.get("SUBSTRING").toString());
		}
		//防止数字错误
		if (subString < 0) {
			subString = 10;
		}
		//用来前台判断文字截取多少个
		modelMap.put("subString", subString);
		
		
		if (singleArchive) {
			//获取当前帐户与树的权限（下载、查看）
			Sys_account_tree account_tree = accountService.getTreeAuth(account.getId(), treeid);
			if (null == account_tree) {
				Sys_org_tree org_tree = orgService.getTreeAuth(account.getOrgid(), treeid);
				modelMap.put("treeauth", org_tree);
			}
			else {
				modelMap.put("treeauth", account_tree);
			}
			//获取docauth的code，用于前台doc列表填充
			Sys_code code = new Sys_code();
			code.setTempletfieldid("DOCAUTH");
			List<Sys_code> codes = codeService.selectByWhere(code);
			HashMap<String, String> codeMap = new HashMap<String, String>();
			for (Sys_code sys_code : codes) {
				codeMap.put(sys_code.getId(), sys_code.getColumndata());
			}
			modelMap.put("codeMap", codeMap);
			
			List<Sys_doc> docs = new ArrayList<Sys_doc>();
			//获取档案的电子全文  TODO 要获取当前帐户电子全文权限范围的
			if (maps.get(0).get("isdoc").toString().equals("1")) {
				String sql = "select * from sys_doc where tableid=? and fileid=?";
				List<Object> values = new ArrayList<Object>();
				values.clear();
				values.add(table.getId());
				values.add(id);
				docs = docService.exeSql(sql, values);
			}
			
			modelMap.put("docs", docs);
		}
		else {
//			List<Sys_templetfield> fields = treeService.geTempletfields(treeid, tabletype,account.getId());
			modelMap.put("fields", fields);
		
			if (null == maps || maps.size() == 0) {
				return null;
			}
			
			Sys_account sessionAccount = getSessionAccount();
	    	if (null == sessionAccount) {
	            return null;
	        }
	    	//获取当前帐户上传的未挂接的所有电子全文，作为批量挂接
	    	List<Sys_doc> docs_no = new ArrayList<Sys_doc>();
	    	String sql = "select * from sys_doc where createrid=? and fileid=?";
			List<Object> values = new ArrayList<Object>();
			values.clear();
			values.add(sessionAccount.getId());
			values.add("");
			docs_no = docService.exeSql(sql, values);
			modelMap.put("docs_no", docs_no);
			modelMap.put("doc_no_json", JSON.toJSON(docs_no));
			
			String resultsString = JSON.toJSONString(fields);
			modelMap.put("fieldjson", resultsString);
		}
		
		Boolean isFileShow = false;
		//获取有没有全文浏览器，如果没有，前台不显示查看按钮
		if (encryService.getInit(24)) {
			isFileShow = true;
		}
		modelMap.put("isFileShow", isFileShow);
		//获取对象
		return new ModelAndView(resultUrl,modelMap);
	}
	
	/**
	 * 处理map里的特殊字符
	 * @param fields
	 * @param maps
	 * @return
	 */
	private List<Map<String, Object>> doMaps(List<Sys_templetfield> fields,List<Map<String, Object>> maps) {
		List<Map<String, Object>> tmpMaps = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map : maps) {
			for (Sys_templetfield field : fields) {
				if (!field.getFieldtype().equals("INT") && field.getSort() >= 0) {
					Object object = map.get(field.getEnglishname());
					String tmpStr = "";
					if (null != object) {
						tmpStr = object.toString();
					}
					
					if (null != tmpStr && !"".equals(tmpStr)) {
						tmpStr = tmpStr.replaceAll("\\\\","\\\\\\\\");
						tmpStr = tmpStr.replace("'","\\\'");
						tmpStr = tmpStr.replace("\"","\\\"");
						tmpStr = tmpStr.replaceAll("[\\t\\n\\r]", "");
					}
					map.put(field.getEnglishname(), tmpStr);
				}
			}
			tmpMaps.add(map);
		}
		return tmpMaps;
	}
	
	
	/**
	 * 获取字段的代码项
	 * @return
	 */
	private Map<String, List<Sys_code>> getFieldCode(List<Sys_templetfield> fields) {
		Map<String, List<Sys_code>> codeMap = new HashMap<String, List<Sys_code>>();
		
		List<Object> values = new ArrayList<Object>();
		
		//判断哪些字段有code
		for (Sys_templetfield field : fields) {
			if (field.getIscode() == 1) {
				values.clear();
				values.add(field.getId());
				List<Sys_code> codes = codeService.list("where templetfieldid=?", values, " order by codeorder asc");
				if (null != codes && codes.size() > 0) {
					codeMap.put(field.getId(), codes);
				}
			}
		}
		return codeMap;
	}
	
	/**
	 * 打开设置帐户，档案页面显示设置
	 * @return
	 */
	@RequestMapping(value="/setshow",method=RequestMethod.GET)
	public String setshow(String templetid,String tabletype,ModelMap modelMap) {
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
//		modelMap.put("account", account);
		//帐户页面显示设置有二类。1、帐户config，包括每页显示条数，字符截取数。2、字段设置，字段显示、字段排序
		//获取帐户的配置，如果没有，以系统为标准️添加
		List<Sys_config> configs = configService.getAccountConfig(account.getId());
		modelMap.put("configs", configs);
		
		List<Sys_templetfield> templetfields = templetfieldService.getAccountTempletfields(templetid, tabletype, account.getId());
		modelMap.put("templetfields", templetfields);
		return "/view/archive/archive/setshow";
	}
	
	/**
	 * 打开编辑字段页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/fieldedit",method=RequestMethod.GET)
	public ModelAndView fieldedit(String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
		//获取对象
		Sys_templetfield templetfield = templetfieldService.getById(id);
		modelMap.put("field", templetfield);
		return new ModelAndView("/view/archive/archive/fieldedit",modelMap);
	}
	
	/**
	 * 打印页面调用。
	 * @param treeid		treeid
	 * @param parentid		如果是文件级打印，需要条件parentid
	 * @param tabletype		01 or 02
	 * @param ids			如果是打印选择的，ids就是选择的记录id
	 * @return
	 */
	@RequestMapping(value="/openprint",method=RequestMethod.GET)
	public ModelAndView openprint(String treeid,String parentid,String tabletype,String ids,ModelMap modelMap) {
		//打印编码
		//1、案卷目录		AJML
		
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		modelMap.put("ids", ids);
		modelMap.put("parentid", parentid);
		
		return new ModelAndView("/view/archive/archive/print",modelMap);
	}
	
	/**
	 * 
	 * @param treeid
	 * @param parentid		如果是文件级打印，需要条件parentid
	 * @param tabletype
	 * @param printcode		打印什么，每个打印有自己的编码，例如要打印案卷目录 为 AJML
	 * @param printtype		打印的类别。打印选择的，或者打印全部  select  or all
	 * @param ids			如果是打印选择的，ids就是选择的记录id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/print",method=RequestMethod.POST)
	public void print(String treeid,String parentid,String tabletype,String printcode,String printtype,String ids,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		String orderby = getOrderby(fields);
		
		List<String> idList = new ArrayList<String>();
		
		if (null != ids && !"".equals(ids)) {
			String[] idsStrings = ids.split(",");
			idList = Arrays.asList(idsStrings);
		}
		
		List<Map<String, Object>> maps = dynamicService.get(treeid, parentid, tabletype, idList, orderby, 0);
		
		List<Map<String,Object>> tmpMaps = doMaps(fields, maps);
		
		String result = JSON.toJSONString(tmpMaps);
		
		out.print(result);
	} 
	
}
