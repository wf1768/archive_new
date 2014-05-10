package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDocserverService;
import net.ussoft.archive.service.ISearchService;
import net.ussoft.archive.service.ITreeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 全文检索
 * @author guodh
 * 
 * */

@Controller
@RequestMapping(value="fulltext")
public class FulltextController extends BaseConstroller{
	
	@Resource
	private ITreeService treeService;
	@Resource
	private IDocserverService docserverService;
	@Resource
	private ISearchService searchService;
	
	private final int pageSize = 2;
	
	/**
	 * 全文检索 - 检索页
	 * @param modelMap
	 * @return
	 * */
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request,ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCHFILE");
		//分类-检索树节点
		List<Sys_tree> treeList = new ArrayList<Sys_tree>();
		treeList = getTreeNode();
		String jsonTreeList = JSON.toJSONString(treeList);
		String jsonTree = jsonTree(request, jsonTreeList);

		
		modelMap.put("pageName", "全文检索");
		modelMap.put("treeList", jsonTree);
		return new ModelAndView("/view/search/fulltext/index",modelMap);
	}
	
	/**
	 * 全文检索 - 检索
	 * @param modelmap
	 * @param schTreeid 要查询的树节点
	 * @param searchText
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value="/search",method=RequestMethod.POST)
	public ModelAndView search(ModelMap modelMap,String schTreeid,String searchText,HttpServletRequest request,int currentPage){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCHFILE");
		HashMap bb = new HashMap();
		List<Sys_tree> tmpList = new ArrayList<Sys_tree>();
		
		List<Sys_docserver> docserverList = docserverService.list();
		Sys_docserver docserver = docserverList.get(1);
		
		System.out.println("treeid="+schTreeid);

		// 根据treeid来执行不同的查询
		if ("all".equals(schTreeid.toLowerCase())) {
			//获得当前帐户的树节点范围
			List<Sys_tree> treeList = new ArrayList<Sys_tree>();
			treeList = getTreeNode();
			for (Sys_tree tree :treeList) {
				if (!"0".equals(tree.getParentid())) {
					if ("W".equals(tree.getTreetype())) {
						tmpList.add(tree);
					}
				}
			}
			//权限字段
			bb = searchService.search(searchText, docserver.getId(), tmpList, currentPage, pageSize);
			
		}else {
			String[] treeIds = schTreeid.split(",");
			for(int i=0;i<treeIds.length;i++){
				Sys_tree tree = treeService.getById(treeIds[i]);
				tmpList.add(tree);
			}
			bb = searchService.search(searchText, docserver.getId(), tmpList, currentPage, pageSize);
		}
		
		bb.put("CURRENTPAGE", currentPage);
		bb.put("PAGESIZE", pageSize);
		List result = new ArrayList();
		result.add(bb);
		String jsonResult = JSON.toJSONString(bb);
		modelMap.put("result", jsonResult);
		modelMap.put("pageBean", bb);
		//分类-检索树节点
		List<Sys_tree> treeList = new ArrayList<Sys_tree>();
		treeList = getTreeNode();
		String jsonTreeList = JSON.toJSONString(treeList);
		String jsonTree = jsonTree(request, jsonTreeList);
		modelMap.put("treeList", jsonTree);
		
		//查询结果树节点
		List<Sys_tree> searchTreeList = new ArrayList<Sys_tree>();
		List searchNumList = new ArrayList();
		for(Sys_tree tree:tmpList){
			HashMap<String, Integer> resultMap = searchService.searchNumber(searchText, docserver.getId(), tree.getId());
			searchNumList.add(resultMap);
			getParentTree(tree.getId(), searchTreeList);
		}
		String searchTree = JSON.toJSONString(searchTreeList);
		String searchJsonTree = jsonTree(request,searchTree);
		modelMap.put("searchTrees", searchJsonTree);
		
		String searchNum = JSON.toJSONString(searchNumList);
		modelMap.put("searchNum", searchNum);
		
		return new ModelAndView("/view/search/fulltext/search",modelMap);
	}

	
	/**
	 * 得到档案类型节点的 父节点
	 * @param treeid
	 * @return
	 */
	private void getParentTree(String treeid,List<Sys_tree> treeList) {
		//得到节点实体
		Sys_tree tree = treeService.getById(treeid);
		if(tree != null){
			boolean flag = true;
			for(Sys_tree t:treeList){
				if(t.getId().equals(tree.getId())){
					flag = false;
				}
			}
			if(flag){
				treeList.add(tree);
				getParentTree(tree.getParentid(), treeList);
			}
		}
	}
	
	/*
	*//**
	 * 获取数据访问的权限字段
	 * @param treeid
	 * @param object
	 * *//*
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getDataAuthField(String treeid,Object object){
		//获取数据访问权限
		String filter = getDataAuth(treeid);
	    Gson gson = new Gson();
	    List list = gson.fromJson(filter, new TypeToken<List>(){}.getType());
	    //权限字段+值
	    HashMap<String, String> fMap = new HashMap<String, String>();
	    if(list !=null && list.size() >0){
	    	for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = gson.fromJson(list.get(i).toString(), new TypeToken<HashMap<String, String>>(){}.getType());
				if (map.get("tableType").toString().equals(object)) {
					String selF = map.get("selectField");
					String selFv = map.get("dataAuthValue");
					fMap.put(selF, selFv);
//					System.out.println(treeid+":"+selF+"="+selFv);
				}
			}
	    }
	    return fMap;
	}
	*//**
     * 获取数据访问权限
     * @param 
     * @return
     * *//*
    public String getDataAuth(String treeid){
    	
    	SysAccount account = super.getAccount();
		//先查看账户本身是否有权限
		List<SysAccountTree> accountTreeList =  accountService.getAccountOfTree(account.getAccountid(), treeid);
		if(accountTreeList.size() >0 && accountTreeList != null){
			SysAccountTree accountTree = accountTreeList.get(0);
			return accountTree.getFilter();
		}else{
			//否则查看该账户的所在组
			SysOrg sysOrg = accountService.getAccountOfOrg(account);
			if(sysOrg!=null){
			 	List<SysOrgTree> orgTreeList = orgService.getOrgOfTree(sysOrg.getOrgid(), treeid);
			 	if(orgTreeList.size() >0 && orgTreeList != null){
			 		return orgTreeList.get(0).getFilter();
			 	}
			}
		}
		return "";
    }*/

	/**
	 * 获取当前帐户所能管理的树节点
	 * @return treeList
	 */
	private List<Sys_tree> getTreeNode() {
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		//根据当前帐户id，获取帐户的档案树节点，用来档案管理里画档案树
		List<Sys_tree> treeList = treeService.getAuthTree(account.getId());
		
		return treeList;
	}
	
	/**
	 * 设置树节点图标
	 * 通过数节点-画树 - 自定义图标
	 * @param request
	 * @param jsonTreeList
	 * @return jsonTree
	 * */
	private String jsonTree(HttpServletRequest request,String jsonTreeList){
		//设置图标路径
		String path = request.getContextPath();
    	String basePath = request.getScheme() + "://"
    			+ request.getServerName() + ":" + request.getServerPort()
    			+ path + "/";
    	
		//通过json对象，插入isparent
		JSONArray jsonArray = JSON.parseArray(jsonTreeList);
		for (int i=0;i<jsonArray.size();i++) {
			String typeString = ((JSONObject) jsonArray.get(i)).get("treetype").toString();
			if (typeString.equals("F") || typeString.equals("T") || typeString.equals("FT")) {
				((JSONObject) jsonArray.get(i)).put("isParent", true);
			}
			if (typeString.equals("F")) {
				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"images/icons/1.gif");
				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"images/icons/2.gif");
			}
			if (typeString.equals("T") || typeString.equals("FT")) {
				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"images/folder.gif");
				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"images/folder-open.gif");
			}
		}
		String jsonString = JSON.toJSONString(jsonArray);
		return jsonString;
	}
	
}
