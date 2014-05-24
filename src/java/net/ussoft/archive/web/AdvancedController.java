package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.ITreeService;

/**
 * 高级检索
 * @author guodh
 * 
 * */

@Controller
@RequestMapping(value="advanced")
public class AdvancedController extends BaseConstroller{
	
	@Resource
	private ITreeService treeService;
	@Resource
	private IDynamicService dynamicService;
	@Autowired  
    private  HttpServletRequest request;
	
	/**
	 * 高级检索-检索结果集
	 * @param modelMap
	 * @return
	 * */
	public ModelAndView list(ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCHADVANCED");
		
		modelMap.put("pageName", "高级检索");
		return new ModelAndView("/view/search/advanced/list",modelMap);
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
        
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCHADVANCED");
		
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
		
		return new ModelAndView("/view/search/advanced/list",modelMap);
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
	
}
