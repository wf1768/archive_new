package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
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
		
		modelMap.put("pageName", "智能检索");
		modelMap.put("treeList", treeList);
		
		return new ModelAndView("/view/search/intelligent/list",modelMap);
	}
	
	/**
	 * 帐户能查询的档案树节点-查询
	 * 
	 * */
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public ModelAndView search(String searchText,ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCH");
		
		System.out.println(searchText);
		
		return new ModelAndView("/view/search/intelligent/list",modelMap);
	}
	
	/**
	 * 帐户能查询的档案树节点
	 * 
	 * */
	public String getSearchTree(){
//		PrintWriter out = this.getPrintWriter();
	
		List<Sys_tree> treeList = new ArrayList<Sys_tree>();
//		treeList = getTreeNode();
		String resultList = JSON.toJSONString(treeList);
//		out.write(result);
		
		return null;
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
		//读取帐户本身的树节点范围
		treeList = accountService.getAccountTree(account.getId());
		
		//判断帐户是否有档案树节点操作
//		if (null == treeList || treeList.size() < 1) {
//			//如果未设置帐户自己的树节点范围，读取所属组的范围
//			SysOrg org = accountService.getAccountOfOrg(account);
//			treeList = orgService.getTree(org.getOrgid());
//			if (null == treeList || treeList.size() <= 0 ) {
////					out.write("error");
//				return null;
//			}
//		}
		return treeList;
	}
	
}
