package net.ussoft.archive.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;
import net.ussoft.archive.util.Excel;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
	public ModelAndView list(String treeid,Boolean allwj,String parentid,String tabletype,Integer page,String searchTxt,ModelMap modelMap) {
		
		if (null == allwj) {
			allwj = false;
		}
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
			
			//如果是文件级，获取对应的案卷级，供文件级页面显示
			if (tabletype.equals("02") && allwj==false) {
				List<Sys_templetfield> ajFieldList = treeService.getTempletfields(treeid, "01",account.getId());
				
				modelMap.put("ajFieldList", ajFieldList);
				//获取aj级信息
				List<String> idList = new ArrayList<String>();
				idList.add(parentid);
				List<Map<String, Object>> maps = dynamicService.get(treeid,"", "01", idList,null,null,null);
				modelMap.put("maps", maps);
				
				Integer page_aj = Integer.valueOf(request.getParameter("page_aj").toString());
				String searchTxt_aj = request.getParameter("searchTxt_aj");
				modelMap.put("page_aj", page_aj);
				modelMap.put("searchTxt_aj", searchTxt_aj);
				
			}
			modelMap.put("parentid", parentid);
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
			pageBean = dynamicService.archiveList(treeid,allwj,parentid,tabletype, searchTxt,0,pageBean);
			modelMap.put("fields", fieldList);
			
			Sys_tree tree = treeService.getById(treeid);
			modelMap.put("treename", tree.getTreename());
		}
		modelMap.put("pagebean", pageBean);
		
		//获取templet
		Sys_templet templet = treeService.getTemplet(treeid);
		modelMap.put("templet", templet);
		modelMap.put("selectid", treeid);
		//modelMap.put("allwj", allwj);
		
		
		//返回的url。返回案卷页，或是文件级页
		String url = "/view/archive/archive/list";
		if (null != templet && templet.getTemplettype().equals("F")) {
			url = "/view/archive/archive/list";
		}
		if (tabletype.equals("02") && allwj==false) {
			url = "/view/archive/archive/list_wj";
		}
		if (allwj) {
			url = "/view/archive/archive/list_all";
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
		fieldList = treeService.getTempletfields(treeid, tabletype,account.getId());
		
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
				if (field.getOrderby().equals("GBK")) {
					orderbyString += "CONVERT("+field.getEnglishname()+" USING gbk),";
				}
				else if (field.getOrderby().equals("NUM")) {
					orderbyString += field.getEnglishname() + "+0,";
				}
				else {
					orderbyString += field.getEnglishname() + " " + field.getOrderby() + ",";
				}
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
	/**
	 * 添加保存
	 * @param data
	 * @param sys
	 * @param response
	 * @throws IOException
	 */
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
		ResultInfo info = dynamicService.insertArchive(sysFieldMap, archiveList);
		
		out.print(info.getMsg());
	}
	
	/**
	 * 打开编辑页面
	 * @param treeid		档案树节点id
	 * @param tabletype		表类型  01 or 02
	 * @param id			档案记录id
	 * @param multiple		批量编辑。（单个档案编辑、批量编辑）
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView edit(String treeid,String tabletype,String id,Boolean multiple,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
		
		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		
		modelMap.put("fields", fields);
		
		//获取信息
		String[] idArr = id.split(",");
		List<String> idList = Arrays.asList(idArr);
//		List<String> idList = new ArrayList<String>();
//		idList.add(id);
		String orderby = getOrderby(fields);
		List<Map<String, Object>> maps = dynamicService.get(treeid,"", tabletype, idList,orderby,null,null);
		modelMap.put("maps", maps);
		
		String resultsString = JSON.toJSONString(fields);
		modelMap.put("fieldjson", resultsString);
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		
		//获取字段代码，前台页面上生成select
		Map<String, List<Sys_code>> codeMap =  getFieldCode(fields);
		modelMap.put("codeMap", codeMap);
		
		//获取templet
		Sys_templet templet = treeService.getTemplet(treeid);
		modelMap.put("templet", templet);
		
		String url = "/view/archive/archive/edit";
		if (null != multiple && multiple == true) {
			Sys_account account = getSessionAccount();
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
			
			url = "/view/archive/archive/edit_multiple";
		}
			
		
		//获取对象
		return new ModelAndView(url,modelMap);
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
		ResultInfo info = dynamicService.updateArchive(tabletype, archiveList);
		
		out.print(info.getMsg());
	}
	
	@RequestMapping(value="/update_multiple",method=RequestMethod.POST)
	public void update_multiple(String data,HttpServletResponse response) throws IOException {
		//链接2个字段内容，可中间插入字符sql
//		update A_74 set pass = CONCAT_WS('-', pass,name)
		//替换字段部分内容sql
//		update A_74 set name = replace(name,"实际","aa")
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		@SuppressWarnings("unchecked")
		Map<String, String> parMap = (Map<String, String>) JSON.parse(data);
		
		String ids = parMap.get("ids");
		String tc_th_field = parMap.get("tc_th_field");
		String treeid = parMap.get("treeid");
		String tabletype = parMap.get("tabletype");
		String tc_radio = parMap.get("tc_radio");
		
		ResultInfo info = new ResultInfo();
		
		//如果是填充
		if (tc_radio.equals("tc") || tc_radio.equals("zk")) {
			String tc = parMap.get("tc");
			List<Map<String, String>> archiveList = new ArrayList<Map<String,String>>();
			
			String[] idArr = ids.split(",");
			
			for (String id : idArr) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", id);
				map.put("treeid", treeid);
				map.put(tc_th_field, tc);
				archiveList.add(map);
			}
			info = dynamicService.updateArchive(tabletype, archiveList);
		}
		else if (tc_radio.equals("th")) {
			//如果是替换
			String th_key = parMap.get("th_key");
			String th_value = parMap.get("th_value");
			
			if (null == th_value) {
				th_value = "";
			}
			
			String[] idArr = ids.split(",");
			List<String> idList = Arrays.asList(idArr);
			info = dynamicService.updateReplace(treeid, tabletype, idList, tc_th_field, th_key, th_value);
		}
		else if (tc_radio.equals("gj")) {
			//如果是高级修改
			String firstField = parMap.get("firstField");
			String txt = parMap.get("txt");
			String secondField = parMap.get("secondField");
			
			String[] idArr = ids.split(",");
			List<String> idList = Arrays.asList(idArr);
			
			info = dynamicService.updateHigh(treeid, tabletype, idList, tc_th_field, firstField, txt, secondField);
		}
		else if (tc_radio.equals("xl")) {
			//如果是序列修改
			
			String xl_first = parMap.get("xl_first");
			String xl_txt = parMap.get("xl_txt");
			Integer xl_begin = Integer.valueOf(parMap.get("xl_begin"));
			Integer xl_size = Integer.valueOf(parMap.get("xl_size"));
			
			if ("".equals(xl_first) && "".equals(xl_txt)) {
				List<Map<String, String>> archiveList = new ArrayList<Map<String,String>>();
				
				String[] idArr = ids.split(",");
				
				Integer num = xl_begin;
				for (int i=0;i<idArr.length;i++) {
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("id", idArr[i]);
					map.put("treeid", treeid);
					map.put(tc_th_field, num.toString());
					archiveList.add(map);
					num += xl_size;
				}
				info = dynamicService.updateArchive(tabletype, archiveList);
			}
			else {
				//如果序列更新里，包含了序列字段、序列值
				String[] idArr = ids.split(",");
				List<String> idList = Arrays.asList(idArr);
				
				List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
				String orderby = getOrderby(fields);
				
				List<Map<String, Object>> archiveList = dynamicService.get(treeid, "", tabletype, idList, orderby, null, null);
				
				List<Map<String, String>> updateMaps = new ArrayList<Map<String,String>>();
				Integer num = xl_begin;
				for (int i=0;i<archiveList.size();i++) {
					String first = "";
					if (!"".equals(xl_first)) {
						first = archiveList.get(i).get(xl_first).toString();
					}
					
					String updateValue = first + xl_txt + num.toString();
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("id", archiveList.get(i).get("id").toString());
					map.put("treeid", treeid);
					map.put(tc_th_field, updateValue);
					updateMaps.add(map);
					num += xl_size;
				}
				info = dynamicService.updateArchive(tabletype, updateMaps);
			}
			
		}
		
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
		List<Map<String, Object>> maps = dynamicService.get(treeid,"", tabletype, idList,null,null,null);
		
		
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
		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		
		String orderbyString = getOrderby(fields);
		
		//获取档案信息
		List<String> idList = Arrays.asList(idArr);
		List<Map<String, Object>> maps = dynamicService.get(treeid, "",tabletype, idList,orderbyString,null,null);
		modelMap.put("maps", maps);
//		modelMap.put("maps_json", JSON.toJSON(maps));
		
		//解决档案信息字符转为json后，特殊字符问题。
		List<Map<String, Object>> tmpMaps = new ArrayList<Map<String,Object>>();
		tmpMaps = doMaps(fields,maps);
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
	public ModelAndView openprint(String treeid,String parentid,String tabletype,String ids,String searchTxt, ModelMap modelMap) {
		//打印编码
		//1、案卷目录		AJML
		//2、文件卷内目录
		
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		modelMap.put("ids", ids);
		modelMap.put("parentid", parentid);
		modelMap.put("searchTxt", searchTxt);
		
		//获取templet，用来判断是否是纯文件级档案类型
		Sys_templet templet = treeService.getTemplet(treeid);
		modelMap.put("templet", templet);
		
		//获取字段。用来配对打印项
		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		
		modelMap.put("fields", fields);
		
		//如果tabletype ＝ 02 并且parentid不为空，是文件级打印，先获取案卷号，为了打印显示
		if (tabletype.equals("02") && null != parentid && !"".equals(parentid)) {
			//获取aj级信息
			String[] pidArr = parentid.split(",");
			List<String> idList = Arrays.asList(pidArr);
			
//			List<String> idList = new ArrayList<String>();
//			idList.add(parentid);
			List<Map<String, Object>> maps = dynamicService.get(treeid,"", "01", idList,null,null,null);
			modelMap.put("ajh", maps.get(0).get("AJH"));
			modelMap.put("bgqx", maps.get(0).get("BGQX"));
			
		}
		
		return new ModelAndView("/view/archive/archive/print",modelMap);
	}
	
	/**
	 * 获取打印数据
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
	public void print(String treeid,String parentid,String tabletype,String printcode,String printtype,String ids,String searchTxt,HttpServletResponse response) throws IOException {
		
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
		
		List<Map<String, Object>> maps = dynamicService.get(treeid, parentid, tabletype, idList, orderby, 0,searchTxt);
		
		List<Map<String,Object>> tmpMaps = doMaps(fields, maps);
		
		String result = JSON.toJSONString(tmpMaps);
		
		out.print(result);
	}
	
	public ModelAndView importArchive(String data,ModelMap modelMap) {
		@SuppressWarnings("unchecked")
		Map<String, String> parMap = (Map<String, String>) JSON.parse(data);
		
		String treeid = parMap.get("treeid");
		String tabletype = parMap.get("tabletype");
		
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		
		return new ModelAndView("/view/archive/archive/excel_import",modelMap);
	}
	
	/**
	 * 
	 * @param file
	 * @param treeid		树节点id
	 * @param tabletype		01 or 02
	 * @param status		状态值，案卷级-状态：0为正常，1为组卷  。文件级-状态：0为正常，1为组卷，2为零散文件
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/upload.do",method=RequestMethod.POST)
    public ModelAndView upload(@RequestParam("file") MultipartFile file,String treeid,String tabletype,Integer status,HttpServletRequest request,ModelMap modelMap) throws IOException {
		
//        String path = request.getRealPath("/upload");
		//获取临时文件的绝对路径
		String path = getProjectRealPath() + "file" +File.separator + "upload" + File.separator;
		File excFile = new File(path+"/"+file.getOriginalFilename());
        //FileCopyUtils.copy(file.getBytes(),excFile);
        
		//得到excel表内容
		Excel e = new Excel();
        Vector v = null;
        
        try {
            v = e.readFromExcel(excFile);
        }
        catch (Exception e1) {
//            out.write("<script>parent.showCallback('failure','Excel文件读取错误，请检查Excel文件中是否包含上传数据。')</script>");
            return null;
        }
        
      //得到excel表第一行列头，作为字段名称
		String excelFieldName = "";
		if (v.size() >= 2) {
			excelFieldName = (String) v.get(0);
		}
		else {
	//      			out.write("<script>parent.showCallback('failure','Excel文件读取错误，请检查Excel文件中是否包含上传数据。')</script>");
			return null;
		}
		
		List<String> excelField = Arrays.asList(v.get(0).toString().split("&&"));
        
		for (String string : excelField) {
			System.out.println(string + "  ====================");
		}
		return new ModelAndView("/view/archive/archive/excel_import",modelMap);
    }
	
	/**
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/datacopy",method=RequestMethod.POST)
	public void datacopy(String treeid,String tabletype,String ids,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "档案数据复制成功，任意档案树节点下，点击［粘贴］。";
		
		if (null == ids || ids.equals("")) {
			result = "档案数据复制失败，请重新尝试，或与管理员联系。";
			out.print(result);
			return;
		}
		
		//将copy的id存入session
		CommonUtils.setSessionAttribute(request, Constants.data_copy_session, ids);
		CommonUtils.setSessionAttribute(request, Constants.data_copy_treeid_session, treeid);
		CommonUtils.setSessionAttribute(request, Constants.data_copy_tabletype_session, tabletype);
		out.print(result);
	}
	
	/**
	 * 保存粘贴数据
	 * @param dy					目标档案类型与源档案类型，字段对应关系
	 * @param isdoc					是否copy电子文件
	 * @param targetTreeid			目标档案类型treeid
	 * @param targetTabletype		目标档案类型的表类型
	 * @param parentid				如果是文件级，会用到的案卷级id
	 * @param status				档案的状态
	 * @param response		
	 * @throws IOException
	 */
	@RequestMapping(value="/datapaster",method=RequestMethod.POST)
	public void datapaster(String dy,Boolean isdoc,String targetTreeid,String targetTabletype,String parentid,Integer status,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "没有获得足够参数，请重新登录，再尝试操作。";
		if (null == targetTreeid || "".equals(targetTreeid) || null == targetTabletype || "".equals(targetTabletype)) {
			out.print(result);
			return;
		}
		
		if (null == dy || "".equals(dy)) {
			out.print(result);
			return;
		}
		
		if (null == status || status < 0) {
			status = 0;
		}
		
		//获取session里的复制codeid
		String ids = (String) CommonUtils.getSessionAttribute(request, Constants.data_copy_session);
		String treeid = (String) CommonUtils.getSessionAttribute(request, Constants.data_copy_treeid_session);
		String tabletype = (String) CommonUtils.getSessionAttribute(request, Constants.data_copy_tabletype_session);
		
		if (null == ids || ids.equals("")) {
			out.print(result);
			return;
		}
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("ids", ids);
		param.put("treeid", treeid);
		param.put("tabletype", tabletype);
		param.put("dy", dy);
		param.put("targetTreeid", targetTreeid);
		param.put("targetTabletype", targetTabletype);
		param.put("parentid", parentid);
		param.put("isdoc", isdoc);
		param.put("status", status);
		
		ResultInfo info = dynamicService.dataPaster(param);
		
		out.print(info.getMsg());
	}
	
	@RequestMapping(value="/opendatapaster",method=RequestMethod.GET)
	public ModelAndView opendatapaster(String targetTreeid,String targetTabletype,String parentid,HttpServletResponse response,ModelMap modelMap) throws IOException {
		
		if (null == targetTreeid || "".equals(targetTreeid) || null == targetTabletype || "".equals(targetTabletype)) {
			return null;
		}
		
		modelMap.put("targetTreeid", targetTreeid);
		modelMap.put("targetTabletype", targetTabletype);
		modelMap.put("parentid", parentid);
		
		//获取session里的复制codeid
		String ids = (String) CommonUtils.getSessionAttribute(request, Constants.data_copy_session);
		String treeid = (String) CommonUtils.getSessionAttribute(request, Constants.data_copy_treeid_session);
		String tabletype = (String) CommonUtils.getSessionAttribute(request, Constants.data_copy_tabletype_session);
		
		if (null == ids || ids.equals("")) {
			return null;
		}
		
		//获取档案类型、树节点名称等基本信息
		Sys_templet templet = treeService.getTemplet(treeid);
		modelMap.put("templet",templet);
		Sys_templet targetTemplet = treeService.getTemplet(targetTreeid);
		modelMap.put("targetTemplet", targetTemplet);
		
		Sys_tree tree = treeService.getById(treeid);
		modelMap.put("tree", tree);
		Sys_tree targetTree = treeService.getById(targetTreeid);
		modelMap.put("targetTree", targetTree);
		
		String[] idArr = ids.split(",");
		
		//获取原档案类型的字段
		List<Sys_templetfield> fields = getTempletfields(treeid, tabletype);
		modelMap.put("fields", fields);
		//获取目标档案类型的字段
		List<Sys_templetfield> fieldsTarget = getTempletfields(targetTreeid, targetTabletype);
		modelMap.put("fieldsTarget", fieldsTarget);
		modelMap.put("fieldsTarget_json", JSON.toJSON(fieldsTarget));
		
		String orderbyString = getOrderby(fields);
		
		//获取档案信息
		List<String> idList = Arrays.asList(idArr);
		List<Map<String, Object>> maps = dynamicService.get(treeid, "",tabletype, idList,orderbyString,null,null);
		modelMap.put("maps", maps);
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
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
		
		return new ModelAndView("/view/archive/archive/datapaster",modelMap);
	}
	
}
