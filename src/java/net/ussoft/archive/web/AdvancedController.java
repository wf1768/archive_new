package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.BaseSelector;

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
	@Resource
    private ITableService tableService;
	@Resource
	private IAccountService accountService;
	@Resource
    private IOrgService orgService;
	
	
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
	 * @param groupitem		检索条件
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String treeid,Boolean allwj,String parentid,String tabletype,Integer page, String groupitem,ModelMap modelMap) {
		
		if (null == allwj) {
			allwj = false;
		}
		if (null == page) {
			page = 1;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			tabletype = "01";
		}
		
		if (null == groupitem || groupitem.trim().equals("")) {
			groupitem = "";
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
		
		//获取点击后页面右侧档案类型列表
		PageBean<Map<String, Object>> pageBean = new PageBean<Map<String,Object>>();
		if (null == treeid || treeid.equals("")) {
			treeid = "0";
		}else {
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
			////////////////////////////////////////////////////////////////
			//获取当前treeid下数据
			Sys_tree tree = treeService.getById(treeid);
			//获取table
			Sys_table table = new Sys_table();
			table.setTempletid(tree.getTempletid());
			table.setTabletype(tabletype);
			table = tableService.selectByWhere(table);

			//获取数据访问权限
			String filter = getDataAuth(treeid);
			String fieldAuth_Sql = ""; //权限字段Sql
		    if(!"".equals(filter) && filter != null){
				List list = new ArrayList();
				//如果已经设置过
				list = (List) JSON.parseObject(filter, new ArrayList().getClass());
				for (int i=0;i<list.size();i++) {
					HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(list.get(i).toString(),new HashMap<String,String>().getClass());
					if (map.get("tableType").equals(tabletype)) {
						String selF = map.get("selectField");
						String selFv = map.get("dataAuthValue");
						String oper = map.get("oper");
						if("like".equals(oper)){
							fieldAuth_Sql += " AND "+selF + " like'%" + selFv +"%'";
						}else if("equal".equals(oper)){
							fieldAuth_Sql += " AND "+selF + " = '" + selFv +"'";
						}
					}
				}
		    }
	            
			StringBuffer sql = new StringBuffer(); //列表
	    	sql.append("SELECT * FROM "+ table.getTablename() +" WHERE 1=1 AND TREEID='"+treeid+"'");
	    	StringBuffer sql_count = new StringBuffer(); //统计
	    	sql_count.append("SELECT COUNT(0) FROM "+ table.getTablename() +" WHERE 1=1 AND TREEID='"+treeid+"'");
			if(!"".equals(groupitem)){
				List list = new ArrayList();
				list = JSON.parseObject(groupitem,new ArrayList().getClass());
				for(int i=0;i<list.size();i++){
					HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(list.get(i).toString(),new HashMap<String,String>().getClass());
					String column = map.get("name");
					String oper = map.get("operatorType");
					String value = map.get("value");
					
					sql.append(" AND " +BaseSelector.getSql(Integer.parseInt(oper), column, value));
		    		sql_count.append(" AND " +BaseSelector.getSql(Integer.parseInt(oper), column, value));
				}
			}
			//访问权
			if(!fieldAuth_Sql.equals("")){
	    		sql.append(fieldAuth_Sql);
	    		sql_count.append(fieldAuth_Sql);
	    	}
			
	    	System.out.println(sql.toString());

	    	
			pageBean = dynamicService.archiveList(sql.toString(),treeid,allwj,parentid,tabletype,0,pageBean);
			modelMap.put("fields", fieldList);
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
	 * @throws IOException 
	 */
	@RequestMapping(value="/getFieldAdvanced",method=RequestMethod.POST)
	private void getFieldAdvanced(HttpServletResponse response,String treeid,String tabletype) throws IOException {
		PrintWriter out = response.getWriter();
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		
		//得到节点对应的模板。用来判断档案类型
		Sys_templet templet = treeService.getTemplet(treeid);
		String result = "var templeType='"+templet.getTemplettype()+"';";
		
		//获取档案字段(先取帐户自己的字段配置，如果没有，获取系统的)
		List<Sys_templetfield> fieldList = new ArrayList<Sys_templetfield>();
		fieldList = treeService.getTempletfields(treeid, tabletype,account.getId());
		
		if (null == fieldList || fieldList.size() == 0) {
			fieldList = treeService.geTempletfields(treeid, tabletype);
		}
		result += "var fields = " + JSON.toJSONString(fieldList);
		
		out.write(result);
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
	
	/**
     * 高级查询
     * */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/searchAdvanced",method=RequestMethod.GET)
	public ModelAndView searchAdvanced(String treeid,Boolean allwj,String parentid,String tabletype,Integer page, String groupitem,ModelMap modelMap) {
		
		if (null == allwj) {
			allwj = false;
		}
		if (null == page) {
			page = 1;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			tabletype = "01";
		}
		
		if (null == groupitem || groupitem.trim().equals("")) {
			groupitem = "";
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
		
		//获取点击后页面右侧档案类型列表
		PageBean<Map<String, Object>> pageBean = new PageBean<Map<String,Object>>();
		if (null == treeid || treeid.equals("")) {
			treeid = "0";
		}else {
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
//				List<String> idList = new ArrayList<String>();
//				idList.add(parentid);
//				List<Map<String, Object>> maps = dynamicService.get(treeid,"", "01", idList,null,null,null);
//				modelMap.put("maps", maps);
				
//				Integer page_aj = Integer.valueOf(request.getParameter("page_aj").toString());
//				String searchTxt_aj = request.getParameter("searchTxt_aj");
//				modelMap.put("page_aj", page_aj);
//				modelMap.put("searchTxt_aj", searchTxt_aj);
				
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
			////////////////////////////////////////////////////////////////
			//获取当前treeid下数据
			Sys_tree tree = treeService.getById(treeid);
			//获取table
			Sys_table table = new Sys_table();
			table.setTempletid(tree.getTempletid());
			table.setTabletype(tabletype);
			table = tableService.selectByWhere(table);

			//获取数据访问权限
			String filter = getDataAuth(treeid);
			String fieldAuth_Sql = ""; //权限字段Sql
		    if(!"".equals(filter) && filter != null){
				List list = new ArrayList();
				//如果已经设置过
				list = (List) JSON.parseObject(filter, new ArrayList().getClass());
				for (int i=0;i<list.size();i++) {
					HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(list.get(i).toString(),new HashMap<String,String>().getClass());
					if (map.get("tableType").equals(tabletype)) {
						String selF = map.get("selectField");
						String selFv = map.get("dataAuthValue");
						String oper = map.get("oper");
						if("like".equals(oper)){
							fieldAuth_Sql += " AND "+selF + " like'%" + selFv +"%'";
						}else if("equal".equals(oper)){
							fieldAuth_Sql += " AND "+selF + " = '" + selFv +"'";
						}
					}
				}
		    }
	            
			StringBuffer sql = new StringBuffer(); //列表
	    	//sql.append("SELECT * FROM "+ table.getTablename() +" WHERE 1=1 AND TREEID='"+treeid+"'");
	    	//StringBuffer sql_count = new StringBuffer(); //统计
	    	//sql_count.append("SELECT COUNT(0) FROM "+ table.getTablename() +" WHERE 1=1 AND TREEID='"+treeid+"'");
			if(!"".equals(groupitem)){
				List list = new ArrayList();
				list = JSON.parseObject(groupitem,new ArrayList().getClass());
				for(int i=0;i<list.size();i++){
					HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(list.get(i).toString(),new HashMap<String,String>().getClass());
					String column = map.get("name");
					String oper = map.get("operatorType");
					String value = map.get("value");
					
					sql.append(" AND " +BaseSelector.getSql(Integer.parseInt(oper), column, value));
		    		//sql_count.append(" AND " +BaseSelector.getSql(Integer.parseInt(oper), column, value));
				}
			}
			//访问权
			if(!fieldAuth_Sql.equals("")){
	    		sql.append(fieldAuth_Sql);
	    		//sql_count.append(fieldAuth_Sql);
	    	}
			
	    	System.out.println(sql.toString());

	    	
			pageBean = dynamicService.archiveList(sql.toString(),treeid,allwj,parentid,tabletype,0,pageBean);
			modelMap.put("fields", fieldList);
			modelMap.put("treename", tree.getTreename());
		}
		modelMap.put("pagebean", pageBean);
		
		//获取templet
		Sys_templet templet = treeService.getTemplet(treeid);
		modelMap.put("templet", templet);
		modelMap.put("selectid", treeid);
		
		return new ModelAndView("/view/search/advanced/list",modelMap);
	}
    /*public ModelAndView searchAdvanced(HttpServletResponse response,String treeid,String tabletype,String groupitem,ModelMap modelMap) throws IOException{
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCHADVANCED");
		PrintWriter out = response.getWriter();
		Sys_tree tree = treeService.getById(treeid);
		//获取table
		Sys_table table = new Sys_table();
		table.setTempletid(tree.getTempletid());
		table.setTabletype(tabletype);
		table = tableService.selectByWhere(table);

		//获取数据访问权限
		String filter = getDataAuth(treeid);
		String fieldAuth_Sql = ""; //权限字段Sql
	    if(!"".equals(filter) && filter != null){
			List list = new ArrayList();
			//如果已经设置过
			list = (List) JSON.parseObject(filter, new ArrayList().getClass());
			for (int i=0;i<list.size();i++) {
				HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(list.get(i).toString(),new HashMap<String,String>().getClass());
				if (map.get("tableType").equals(tabletype)) {
					String selF = map.get("selectField");
					String selFv = map.get("dataAuthValue");
					String oper = map.get("oper");
					if("like".equals(oper)){
						fieldAuth_Sql += " AND "+selF + " like'%" + selFv +"%'";
					}else if("equal".equals(oper)){
						fieldAuth_Sql += " AND "+selF + " = '" + selFv +"'";
					}
				}
			}
	    }
            
		StringBuffer sql = new StringBuffer(); //列表
    	sql.append("SELECT * FROM "+ table.getTablename() +" WHERE 1=1 AND TREEID='"+treeid+"'");
    	StringBuffer sql_count = new StringBuffer(); //统计
    	sql_count.append("SELECT COUNT(0) FROM "+ table.getTablename() +" WHERE 1=1 AND TREEID='"+treeid+"'");
		if(!"".equals(groupitem)){
			List list = new ArrayList();
			list = JSON.parseObject(groupitem,new ArrayList().getClass());
			for(int i=0;i<list.size();i++){
				HashMap<String, String> map = (HashMap<String, String>) JSON.parseObject(list.get(i).toString(),new HashMap<String,String>().getClass());
				String column = map.get("name");
				String oper = map.get("operatorType");
				String value = map.get("value");
				
				sql.append(" AND " +BaseSelector.getSql(Integer.parseInt(oper), column, value));
	    		sql_count.append(" AND " +BaseSelector.getSql(Integer.parseInt(oper), column, value));
			}
		}
		//访问权
		if(!fieldAuth_Sql.equals("")){
    		sql.append(fieldAuth_Sql);
    		sql_count.append(fieldAuth_Sql);
    	}
		
    	System.out.println(sql.toString());

    	return null;
    }*/
	
	/**
     * 获取数据访问权限
     * @param 
     * @return
     * */
    public String getDataAuth(String treeid){
    	//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		
		//先查看账户本身是否有权限
		Sys_account_tree accountTree =  accountService.getTreeAuth(account.getId(), treeid);
		if(accountTree != null){
			return accountTree.getFilter();
		}else{
			//否则查看该账户的所在组
		 	Sys_org_tree orgTree = orgService.getTreeAuth(account.getOrgid(), treeid);
		 	if(orgTree != null){
		 		return orgTree.getFilter();
		 	}
		}
		return "";
    }
}
