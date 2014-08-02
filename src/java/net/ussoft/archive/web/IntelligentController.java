package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

/**
 * 智能检索
 * @author guodh
 * 
 * */

@Controller
@RequestMapping(value="intelligent")
public class IntelligentController extends BaseConstroller{
	
	@Resource
	private IAccountService accountService;
	@Resource
	private ITreeService treeService;
	@Resource
	private IOrgService orgService;
	@Resource
	private ITableService tableService;
	@Resource
	private IDynamicService dynamicService;
	
	/**
	 * 计算选中树节点的查询数量，存入session
	 * @param searchTreeids		选中的树节点
	 * @param searchTxt			查询值
	 * @param request
	 */
	private void createSearchCount(String searchTreeids,String searchTxt,HttpServletRequest request) {
		
		HashMap<String, HashMap<String, String>> treeMap = new HashMap<String, HashMap<String,String>>();
		
		List<Object> treeidList = (List<Object>) JSON.parse(searchTreeids);
		String paramString = "";
		for (int i = 0; i < treeidList.size(); i++) {
			paramString += "?,";
		}
		paramString = paramString.substring(0, paramString.length()-1);
		String where = " where id in ("+paramString+") ";
		List<Sys_tree> treeList = treeService.list(where, treeidList, null);
		
		for (Sys_tree sys_tree : treeList) {
			if (sys_tree.getTreetype().equals("W")) {
				//获取tree的档案类型
				Sys_templet templet = treeService.getTemplet(sys_tree.getId());
				if (templet.getTemplettype().equals("A") || templet.getTemplettype().equals("P")) {
					Integer ajCount = dynamicService.archiveCount(sys_tree.getId(), "01", searchTxt, 0);
					
					Integer wjCount = dynamicService.archiveCount(sys_tree.getId(), "02", searchTxt, 0);
					
					//修改：查到了才显示数量，没查到就不显示，看起来更清楚
					String ajTxt = "";
					String wjTxt = "";
					if (null != ajCount && ajCount > 0) {
						ajTxt = "案:" + ajCount;
					}
					
					if (null != wjCount && wjCount > 0) {
						wjTxt = "文:" + wjCount;
					}
					
					String countStr = "[<span style='color:red;margin-right:0px;'>" + ajTxt + " " + wjTxt + "</span>]";
//					if (null == ajCount || ajCount < 0) {
//						ajCount = 0;
//					}
//					Integer wjCount = dynamicService.archiveCount(sys_tree.getId(), "02", searchTxt, 0);
//					if (null == wjCount || wjCount < 0) {
//						wjCount = 0;
//					}
//					String countStr = "[<span style='color:red;margin-right:0px;'>案:" + ajCount + " 文:" + wjCount + "</span>]";
					HashMap<String, String> countMap = new HashMap<String, String>();
					countMap.put("countStr", countStr);
					countMap.put("aj", ajCount.toString());
					countMap.put("wj", wjCount.toString());
					countMap.put("templettype", templet.getTemplettype());
					countMap.put("treenode", sys_tree.getTreenode());
					treeMap.put(sys_tree.getId(), countMap);
				}
				else if (templet.getTemplettype().equals("F")) {
					Integer wjCount = dynamicService.archiveCount(sys_tree.getId(), "01", searchTxt, 0);
					
					//修改：查到了才显示数量，没查到就不显示，看起来更清楚
					String wjTxt = "";
					
					if (null != wjCount && wjCount > 0) {
						wjTxt = "文:" + wjCount;
					}
					
					HashMap<String, String> countMap = new HashMap<String, String>();
					String countStr = "[<span style='color:red;margin-right:0px;'>" + wjTxt + "</span>]";
					countMap.put("countStr", countStr);
					countMap.put("wj", wjCount.toString());
					countMap.put("templettype", templet.getTemplettype());
					countMap.put("treenode", sys_tree.getTreenode());
					treeMap.put(sys_tree.getId(), countMap);
				}
			}
		}
		//将数量map存入session，如果查询值美没有变，就不需要每次都获取数量，浪费
		CommonUtils.setSessionAttribute(request, Constants.intel_search_count_session, treeMap);
		//将查询值存入session，如果查询值美没有变，就不需要每次都获取数量，浪费
		CommonUtils.setSessionAttribute(request, Constants.intel_search_session, searchTxt);
		//将选择的树节点存入session，
		CommonUtils.setSessionAttribute(request, Constants.intel_search_treeid_session, searchTreeids);
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
	
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelMap modelMap,HttpServletRequest request){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCH");
		
		//默认访问查询案卷页
		String url = "/view/search/intelligent/list";
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		//根据当前帐户id，获取帐户的档案树节点，用来档案管理里画档案树
		List<Sys_tree> trees = treeService.getAuthTree(account.getId());
		
		//获取点击后页面右侧档案类型列表
		PageBean<Map<String, Object>> pageBean = new PageBean<Map<String,Object>>();

	    String treeJson = "";
		if (null != trees && trees.size() >0 ) {
			treeJson = JSON.toJSONString(trees);
		}
	    treeJson = treeService.createTreeJson(treeJson, getProjectBasePath());
		modelMap.put("treeList", treeJson);
		modelMap.put("pagebean", pageBean);
		modelMap.put("isSearchTreeid", false);
		
		CommonUtils.removeSessionAttribute(request, Constants.intel_search_count_session);
		CommonUtils.removeSessionAttribute(request, Constants.intel_search_session);
		CommonUtils.removeSessionAttribute(request, Constants.intel_search_treeid_session);
		return new ModelAndView(url,modelMap);

		
		
	}
	
	@RequestMapping(value="/listSearch",method=RequestMethod.POST)
	public ModelAndView listSearch(String searchTreeids,String parentid,String treeid,String tabletype,Integer page,
			String searchTxt,String expand ,Integer isSearchWj,ModelMap modelMap,HttpServletRequest request){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCH");
		
		//默认访问查询案卷页
		String url = "/view/search/intelligent/list";
		
		if (null == page) {
			page = 1;
		}
		
		if (null == isSearchWj) {
			isSearchWj = 0;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			tabletype = "01";
		}
		
		if (null == searchTxt || searchTxt.trim().equals("")) {
			searchTxt = "";
		}
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		//根据当前帐户id，获取帐户的档案树节点，用来档案管理里画档案树
		List<Sys_tree> trees = treeService.getAuthTree(account.getId());
		
		modelMap.put("searchTxt", searchTxt);
		//前台展开的夹id，返回去，页面展开这些
		modelMap.put("expand", expand);
		
		//获取点击后页面右侧档案类型列表
		PageBean<Map<String, Object>> pageBean = new PageBean<Map<String,Object>>();
		
		//如果没有treeid，并且查询值为空，就直接返回到智能检索页面
		if (null == treeid || treeid.equals("")) {
			if (null == searchTxt || searchTxt.trim().equals("")) {
				String treeJson = "";
				if (null != trees && trees.size() >0 ) {
					treeJson = JSON.toJSONString(trees);
				}
				treeJson = treeService.createTreeJson(treeJson, getProjectBasePath());
				modelMap.put("treeList", treeJson);
				modelMap.put("pagebean", pageBean);
				modelMap.put("isSearchTreeid", false);
				
				CommonUtils.removeSessionAttribute(request, Constants.intel_search_count_session);
				CommonUtils.removeSessionAttribute(request, Constants.intel_search_session);
				CommonUtils.removeSessionAttribute(request, Constants.intel_search_treeid_session);
				return new ModelAndView(url,modelMap);
			}
		}
		
		
		//获取session里的查询值，与传入的查询值比较，如果不相同，重新计算选中的树节点的查询数量。
		Object object = CommonUtils.getSessionAttribute(request, Constants.intel_search_session);
		String sessionSearchTxt = object == null ? null : (String) object;
		
		HashMap<String, HashMap<String, String>> treeMap = new HashMap<String, HashMap<String,String>>();
		
		modelMap.put("searchTreeids", searchTreeids);
		
		//获取session里的树节点，如果树节点变化，也要重新查。
		Object objectTreeid = CommonUtils.getSessionAttribute(request, Constants.intel_search_treeid_session);
		String sessionTreeid =  objectTreeid == null ? null : objectTreeid.toString();
		
		//计算查询值，在选中的树节点的数据中，数量
//	    if (null == sessionSearchTxt || !sessionSearchTxt.equals(searchTxt) || null == sessionTreeid || !sessionTreeid.equals(searchTreeids)) {
		if (null == treeid || "".equals(treeid)) {
			if (null == sessionSearchTxt || !sessionSearchTxt.equals(searchTxt) || null == sessionTreeid || !sessionTreeid.equals(searchTreeids)) {
				//获取选中节点
				if (null != searchTreeids && !"".equals(searchTreeids) && searchTreeids.length() > 2) {
					//计算选中树节点的查询数量，存入session
					createSearchCount(searchTreeids,searchTxt,request);
				}
			}
		}
		else {
			//如果页面选择了tree，获取数据
			if (null != treeid && !"".equals(treeid)) {
				
				//获取每页条数(首先获取帐户自己的页数配置，如果没有设置，读取系统配置)
				HashMap<String, Object> configMap = getConfig(account.getId());
				//每页行数
				Integer pageSize = 30;
				//字段截取标准。（列表里字段长度超过标准，被截取)
				Integer subString = 10;
				String imageshow = "LIST";
				if (null == configMap || configMap.size() == 0) {
					configMap = getConfig("SYSTEM");
					pageSize = Integer.parseInt(configMap.get("PAGE").toString());
				}
				else {
					pageSize = Integer.parseInt(configMap.get("PAGE").toString());
					subString = Integer.parseInt(configMap.get("SUBSTRING").toString());
					imageshow = configMap.get("IMAGESHOW").toString();
				}
				
				//获取档案字段(先取帐户自己的字段配置，如果没有，获取系统的)
				List<Sys_templetfield> fieldList = getTempletfields(treeid, tabletype);
				
				modelMap.put("parentid", parentid);
				pageBean.setOrderBy(getOrderby(fieldList));
				modelMap.put("fields", fieldList);
				
				Sys_tree tree = treeService.getById(treeid);
				modelMap.put("treename", tree.getTreename());
				//用来前台判断文字截取多少个
				modelMap.put("subString", subString);
				
				if (pageSize == 0) {
					pageBean.setIsPage(false);
				}
				else {
					pageBean.setIsPage(true);
					pageBean.setPageSize(pageSize);
					pageBean.setPageNo(page);
				}
				
				List<String> treeidList = (List<String>) JSON.parse(sessionTreeid);
				
				//判断选择的treeid在不在查询范围内，如果在，获取查询后的数据，如果不在，获取全部数据
				Boolean isSearchTreeid = false;
				if (null != treeidList && treeidList.size() > 0) {
					for (String str : treeidList) {
						if (str.equals(treeid)) {
							isSearchTreeid = true;
						}
					}
				}
				modelMap.put("isSearchTreeid", isSearchTreeid);
				
				//如果当前选中的treeid在查询里
				if (isSearchTreeid) {
					if (isSearchWj == 1) {
						pageBean = dynamicService.archiveList(treeid, false, parentid, tabletype, sessionSearchTxt, 0, pageBean);
					}
					else {
						//如果当前treeid不在检索里，如果是文件级，获取对应的案卷级，供文件级页面显示
						if (tabletype.equals("02")) {
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
							
							pageBean = dynamicService.archiveList(treeid, false, parentid, tabletype, null, 0, pageBean);
						}
						else {
							pageBean = dynamicService.archiveList(treeid, false, parentid, tabletype, sessionSearchTxt, 0, pageBean);
						}
					}
				}
				else {
					//如果当前treeid不在检索里，如果是文件级，获取对应的案卷级，供文件级页面显示
					if (tabletype.equals("02")) {
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
					
					pageBean = dynamicService.archiveList(treeid, false, parentid, tabletype, null, 0, pageBean);
				}
				modelMap.put("searchTxt", sessionSearchTxt);
				
				//获取templet
				Sys_templet templet = treeService.getTemplet(treeid);
				modelMap.put("templet", templet);
				modelMap.put("selectid", treeid);
				
				//返回的url。返回案卷页，或是文件级页
				if (tabletype.equals("02")) {
					url = "/view/search/intelligent/list_wj";
					if (isSearchWj == 1) {
						url = "/view/search/intelligent/list_search_wj";
					}
				}
				
				if (null != templet && templet.getTemplettype().equals("P")) {
					if ("IMAGE".equals(imageshow)) {
						//如果不在范围内，返回正常文件页
						url = "/view/search/intelligent/listpic";
						if (tabletype.equals("02")) {
							url = "/view/search/intelligent/listpic_wj";
						}
//						//如果选中的树节点在检索范围内，返回查询文件页
//						if (isSearchTreeid) {
//							url = "/view/search/intelligent/listpic_search";
//							if (tabletype.equals("02")) {
//								url = "/view/search/intelligent/listpic_search_wj";
//							}
//						}
//						else {
//							//如果不在范围内，返回正常文件页
//							url = "/view/search/intelligent/listpic";
//							if (tabletype.equals("02")) {
//								url = "/view/search/intelligent/listpic_wj";
//							}
//						}
						
					}
					else {
						url = "/view/search/intelligent/listpic_list";
						if (tabletype.equals("02")) {
							url = "/view/search/intelligent/listpic_wj_list";
							if (isSearchWj == 1) {
								url = "/view/search/intelligent/listpic_wj_list_search";
							}
						}
//						//如果选中的树节点在检索范围内，返回查询文件页
//						if (isSearchTreeid) {
//							url = "/view/search/intelligent/listpic_search_list";
//							if (tabletype.equals("02")) {
//								url = "/view/search/intelligent/listpic_search_list_wj";
//							}
//						}
//						else {
//							//如果不在范围内，返回正常文件页
//							url = "/view/search/intelligent/listpic_list";
//							if (tabletype.equals("02")) {
//								url = "/view/search/intelligent/listpic_list_wj";
//							}
//						}
					}
				}
			}
		}
		
		modelMap.put("pagebean", pageBean);
		//获取session里的查询值的数量，赋予页面显示。
		Object objectCountMap = CommonUtils.getSessionAttribute(request, Constants.intel_search_count_session);
		treeMap =  objectCountMap == null ?null : (HashMap<String, HashMap<String, String>>)objectCountMap;
		
//	    modelMap.put("treeMap", treeMap);
		
		String treeJson = "";
		if (null != trees && trees.size() >0 ) {
			treeJson = JSON.toJSONString(trees);
		}
		if (null == treeMap || treeMap.size() == 0) {
			treeJson = treeService.createTreeJson(treeJson, getProjectBasePath());
		}
		else {
			treeJson = treeService.createTreeJson(treeJson, getProjectBasePath(),treeMap);
		}
		
		modelMap.put("treeList", treeJson);
		
		return new ModelAndView(url,modelMap);
	}
	
	
	
	
	/**
	 * 智能检索-检索结果集
	 * @param modelMap
	 * @return
	 * */
	@RequestMapping(value="/list1",method=RequestMethod.GET)
	public ModelAndView list1(HttpServletRequest request, ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCH");
		
//		List<Sys_tree> treeList = new ArrayList<Sys_tree>();
//		treeList = getTreeNode(request);
//		String jsonTreeList = JSON.toJSONString(treeList);
		
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		//根据当前帐户id，获取帐户的档案树节点，用来档案管理里画档案树
		List<Sys_tree> trees = treeService.getAuthTree(account.getId());
		String treeJson = "";
		if (null != trees && trees.size() >0 ) {
			treeJson = JSON.toJSONString(trees);
		}
		
		treeJson = treeService.createTreeJson(treeJson, getProjectBasePath());
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> m = new HashMap<String, String>();
		m.put("a", "aaa");
		m.put("b", "bbb");
		list.add(m);
		Map<String, String> m1 = new HashMap<String, String>();
		m1.put("a", "1aaa");
		m1.put("b", "1bbb");
		list.add(m1);
		
		modelMap.put("pageName", "智能检索");
		modelMap.put("treeList", treeJson);
		
		modelMap.put("list", list);
		modelMap.put("key", "a");
		return new ModelAndView("/view/search/intelligent/list",modelMap);
	}
	
	/**
	 * 帐户能查询的档案树节点-查询
	 * 
	 * */
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public ModelAndView search(HttpServletRequest request,String searchText,ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCH");
		
		System.out.println(searchText);
		List<Sys_tree> treeList = new ArrayList<Sys_tree>();
		treeList = getTreeNode(request);
		//检索结果数据
		List<Object> resultList = new ArrayList<Object>();
		//检索表字段
		List<Object> fieldList = new ArrayList<Object>();
		for(Sys_tree aTree:treeList){
			List<Sys_table> tableList = tableService.getTableByTempletid(aTree.getTempletid());
			for(Sys_table table:tableList){
				System.out.println(table.getTablename());
				List<Sys_templetfield> templetFieldList = tableService.geTempletfields(table.getId());
				fieldList.add(templetFieldList);
				
				PageBean<Map<String, Object>> pb = dynamicService.search(searchText,table.getTablename(),aTree.getId(),templetFieldList,1,10000);
				resultList.add( pb.getList());
				
				String jsonList = JSON.toJSONString(pb.getList());
				System.out.println(jsonList);
			}
		}
		
		
		String jsonTreeList = JSON.toJSONString(treeList);
		modelMap.put("pageName", "智能检索");
		modelMap.put("treeList", jsonTreeList);
		modelMap.put("resultList", resultList);
		modelMap.put("fieldList", fieldList);
		return new ModelAndView("/view/search/intelligent/list",modelMap);
	}
	
	/**
	 * 获取当前帐户所能管理的树节点
	 * @return
	 */
	private List<Sys_tree> getTreeNode(HttpServletRequest httpRequest) {
		//获取session里的登录帐户
		Object object = CommonUtils.getSessionAttribute(httpRequest, Constants.user_in_session);
	    Sys_account account = object == null ? null : (Sys_account) object; 
	    List<Sys_tree> treeList = new ArrayList<Sys_tree>();
		treeList = treeService.getAuthTree(account.getId());
		return treeList;
	}
	
}
