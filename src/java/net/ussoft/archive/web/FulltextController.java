package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDocserverService;
import net.ussoft.archive.service.ISearchService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;

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
	
	private final int pageSize = 5;
	
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
//		modelMap.put("pageName", "全文检索");
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
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public ModelAndView search(ModelMap modelMap,String schTreeid,String searchText,HttpServletRequest request,int currentPage){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCHFILE");
		HashMap bb = new HashMap();
		if(currentPage == 0)
			currentPage = 1;
		List<Sys_tree> tmpList = new ArrayList<Sys_tree>();
		StringBuffer sbTreeid = new StringBuffer();
		
		List<Sys_docserver> docserverList = docserverService.list();
		Sys_docserver docserver = docserverList.get(1);
		
		//要查询的树节点
		String schTreeids = "";
		//查询树节点结果集中的单个节点
		String schTreeidByTreeid = CommonUtils.replaceNull2Space(request.getParameter("treeid"));
		if("".equals(schTreeidByTreeid)){
			schTreeids = schTreeid;
		}else{
			schTreeids = schTreeidByTreeid;
		}
		
		// 根据treeid来执行不同的查询
		if ("all".equals(schTreeids.toLowerCase())) {
			//获得当前帐户的树节点范围
			List<Sys_tree> treeList = new ArrayList<Sys_tree>();
			treeList = getTreeNode();
			for (Sys_tree tree :treeList) {
				if (!"0".equals(tree.getParentid())) {
					if ("W".equals(tree.getTreetype())) {
						tmpList.add(tree);
						sbTreeid.append(tree.getId()+",");
					}
				}
			}
			//权限字段
			bb = searchService.search(searchText, docserver.getId(), tmpList, currentPage, pageSize);
		}else {
			String[] treeIds = schTreeids.split(",");
			for(int i=0;i<treeIds.length;i++){
				Sys_tree tree = treeService.getById(treeIds[i]);
				tmpList.add(tree);
				sbTreeid.append(tree.getId()+",");
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
		
		String treeids = CommonUtils.replaceNull2Space(request.getParameter("treeids"));
		if("".equals(treeids)){
			treeids = sbTreeid.toString();
		}
		//查询结果树节点
		List<Sys_tree> searchTreeList = new ArrayList<Sys_tree>();
		List searchNumList = new ArrayList();
		String[] treeidArr = treeids.split(",");
		for(int i=0;i<treeidArr.length;i++){
			HashMap<String, Integer> resultMap = searchService.searchNumber(searchText, docserver.getId(), treeidArr[i]);
			searchNumList.add(resultMap);
			getParentTree(treeidArr[i], searchTreeList);
		}
		//统计档案类型节点F下W的数量和
		List countNum = getParentTreeFNum(searchTreeList, searchNumList);
		for(int i=0;i<countNum.size();i++){
			HashMap<String, Integer> resultMap = (HashMap<String, Integer>) countNum.get(i);
			searchNumList.add(resultMap);
		}
		modelMap.put("treeid", schTreeidByTreeid);
		modelMap.put("treeids", treeids);
		String searchTree = JSON.toJSONString(searchTreeList);
		//要显示的树节点
		String searchJsonTree = jsonTree(request,searchTree);
		modelMap.put("searchTrees", searchJsonTree);
		//节点结果数量
		String searchNum = JSON.toJSONString(searchNumList);
		modelMap.put("searchNum", searchNum);
		//查询类型选择的树节点
		modelMap.put("schTreeid", schTreeid); 
		//检索的关键字
		modelMap.put("searchText", searchText);
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
	/**
	 * /统计档案类型节点F下W的数量和
	 * @param treeList
	 * @param searchNumList 节点W的数量
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	private List getParentTreeFNum(List<Sys_tree> treeList,List searchNumList) {
		List countList = new ArrayList();
		for(Sys_tree tree:treeList){
			if("F".equals(tree.getTreetype())){
				HashMap<String, Integer> countMap = new HashMap<String, Integer>();
				int count = 0;
				for(int i=0;i<searchNumList.size();i++){
					Map.Entry map = getOneMap((Map)searchNumList.get(i));
					Sys_tree obj = treeService.getById(map.getKey().toString());
					if(obj.getTreenode().indexOf(tree.getTreenode()) > -1){
						count = count + Integer.parseInt(map.getValue().toString());
					}
				}
				countMap.put(tree.getId(), count);
				countList.add(countMap);
			}
		}
		return countList;
	}
	
	/**
	 * 此Map中只有一个值
	 * 为了直接获取map的key和value
	 * */
	@SuppressWarnings("unchecked")
	public Map.Entry getOneMap(Map m){
		Iterator i=m.entrySet().iterator();
		while(i.hasNext()){//只遍历一次,速度快
			return (Map.Entry)i.next();
		}
		return null;
	}

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
