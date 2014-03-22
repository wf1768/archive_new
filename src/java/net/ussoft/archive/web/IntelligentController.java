package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;

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
	 * 智能检索-检索结果集
	 * @param modelMap
	 * @return
	 * */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCH");
		
		List<Sys_tree> treeList = new ArrayList<Sys_tree>();
		treeList = getTreeNode(request);
		String jsonTreeList = JSON.toJSONString(treeList);
		
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
		modelMap.put("treeList", jsonTreeList);
		
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
