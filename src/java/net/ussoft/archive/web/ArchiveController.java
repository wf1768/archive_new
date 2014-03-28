package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.ICodeService;
import net.ussoft.archive.service.IConfigService;
import net.ussoft.archive.service.IDynamicService;
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
			List<Sys_templetfield> fieldList = new ArrayList<Sys_templetfield>();
			fieldList = treeService.geTempletfields(treeid, tabletype,account.getId());
			
			//如果是文件级，获取对应的案卷级，供文件级页面显示
			if (tabletype.equals("02")) {
				List<Sys_templetfield> ajFieldList = treeService.geTempletfields(treeid, "01",account.getId());
				modelMap.put("ajFieldList", ajFieldList);
				//获取aj级信息
				List<Map<String, Object>> maps = dynamicService.getOne(treeid, "01", parentid);
				modelMap.put("maps", maps);
				modelMap.put("parentid", parentid);
				
				Integer page_aj = Integer.valueOf(request.getParameter("page_aj").toString());
				String searchTxt_aj = request.getParameter("searchTxt_aj");
				modelMap.put("page_aj", page_aj);
				modelMap.put("searchTxt_aj", searchTxt_aj);
				
			}
			
			if (null == fieldList || fieldList.size() == 0) {
				fieldList = treeService.geTempletfields(treeid, tabletype);
			}
			
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
				pageBean.setOrderBy(orderbyString);
			}
			
			
			if (pageSize == 0) {
				pageBean.setIsPage(false);
			}
			else {
				pageBean.setIsPage(true);
				pageBean.setPageSize(pageSize);
				pageBean.setPageNo(page);
			}
			
			//获取当前treeid下数据
			pageBean = dynamicService.archiveList(treeid,parentid,tabletype, searchTxt,pageBean);
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
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		
		//获取档案字段(先取帐户自己的字段配置，如果没有，获取系统的)
		List<Sys_templetfield> fieldList = new ArrayList<Sys_templetfield>();
		fieldList = treeService.geTempletfields(treeid, tabletype,account.getId());
		
		if (null == fieldList || fieldList.size() == 0) {
			fieldList = treeService.geTempletfields(treeid, tabletype);
		}
		
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
	public void save(String str,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
//		String result = "success";
//		if (templetfield == null || null == templetfield.getEnglishname() || templetfield.getEnglishname().equals("")) {
//			result = "failure";
//			out.print(result);
//			return;
//		}
		
		@SuppressWarnings({ "unchecked", "unused" })
		Map<String, String> map = (Map<String, String>) JSON.parse(str);
		
		List<Map<String, String>> archiveList = new ArrayList<Map<String,String>>();
		archiveList.add(map);
		ResultInfo info = dynamicService.saveArchive(null, null, archiveList);
		
		out.print(info.getMsg());
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
	
}
