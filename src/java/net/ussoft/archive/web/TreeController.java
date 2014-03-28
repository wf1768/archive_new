package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ITreeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping(value="tree")
public class TreeController extends BaseConstroller {
	
	@Resource
	private ITreeService treeService;
	
	/**
	 * 档案树管理
	 * @param selectid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String selectid,ModelMap modelMap) {
		
        
		modelMap = super.getModelMap("SYSTEM","TREE");
		
		String where = "";
		String order = " order by sort asc";
		List<Object> values = new ArrayList<Object>();
		
		List<Sys_tree> trees = treeService.list(where, values, order);
		String resultsString = JSON.toJSONString(trees);
		
		String basePath = getProjectBasePath();
		String jsonString = treeService.createTreeJson(resultsString, basePath);
    	
		
		modelMap.put("result", jsonString);
		modelMap.put("selectid", selectid);
		
		values.clear();
		values.add(selectid);
		List<Sys_tree> treeList = treeService.list(" where parentid =?", values, order);
		modelMap.put("trees", treeList);
		
		//获取点击后页面右侧档案类型列表
		if (null == selectid || selectid.equals("")) {
			selectid = "0";
		}
		
		modelMap.put("selectid", selectid);
		
		return new ModelAndView("/view/system/tree/list",modelMap);
	}
	/**
	 * 创建档案树夹
	 * @param parentid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(String parentid,String treetype,ModelMap modelMap) {
		modelMap.put("parentid", parentid);
		modelMap.put("treetype", treetype);
		return "/view/system/tree/add";
	}
	
	/**
	 * 保存树节点、或树节点夹
	 * @param tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(Sys_tree tree,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (null == tree.getParentid() || tree.getParentid().equals("")) {
			out.print(result);
			return;
		}
		
		result = treeService.insert(tree);
		out.print(result);
	}
	
	/**
	 * 打开编辑页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView edit(String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
		//获取对象
		Sys_tree tree = treeService.getById(id);
		modelMap.put("tree", tree);
		return new ModelAndView("/view/system/tree/edit",modelMap);
	}
	/**
	 * 执行更新
	 * @param tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public void update(Sys_tree tree,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (tree == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = treeService.update(tree);
		if (num <= 0 ) {
			result = "failure";
		}
		out.print(result);
	}
	
	/**
	 * 删除
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void delete(String id,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (null == id || id.equals("")) {
			out.print(result);
			return;
		}
		result = treeService.delete(id);
		out.print(result);
	}
	
	/**
	 * 打开档案节点排序更改页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/sort",method=RequestMethod.GET)
	public ModelAndView sort(String id,ModelMap modelMap) {
		
		Sys_tree tree = treeService.getById(id);
		modelMap.put("tree", tree);
		
		return new ModelAndView("/view/system/tree/sort",modelMap);
	}
	/**
	 * 保存排序
	 * @param templet
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/sortsave",method=RequestMethod.POST)
	public void sortsave(Sys_tree tree,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (tree == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = treeService.sortsave(tree);
		if (num <= 0 ) {
			result = "failure";
		}
		out.print(result);
	}
	
	/**
	 * 根据treeid，获取tree对应的templet模版的实体
	 * @param treeid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/getTempletType",method=RequestMethod.POST)
	public void getTempletType(String treeid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (treeid == null || treeid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Sys_templet templet = treeService.getTemplet(treeid);
		
		if (null == templet) {
			result = "failure";
		}
		
		result = JSON.toJSONString(templet);
		
		out.print(result);
	}
	
	/**
	 * 打开档案类型移动页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/move",method=RequestMethod.GET)
	public ModelAndView move(String id,HttpServletRequest request,ModelMap modelMap) {
		
		String where = "";
		String order = " order by sort asc";
		List<Object> values = new ArrayList<Object>();
		
		Sys_tree tree = treeService.getById(id);
		where = " where treetype='F' and instr('"+tree.getTreenode()+"',treenode) > 0";
		//获取要移动节点的档案类型F对象.根据F对象，来获取F的父级和下级
		List<Sys_tree> tmpList = treeService.list(where, values, order);
		Sys_tree fTree = new Sys_tree();
		if (tmpList.size() == 1) {
			fTree = tmpList.get(0);
		}
		else {
			//有问题返回
			return null;
		}
		
		List<Sys_tree> tmpTrees = new ArrayList<Sys_tree>();
		where = "";
		List<Sys_tree> trees = treeService.list(where, values, order);
		//筛选出要移动的树节点的父节点，排除其他的，节点只能在所属的档案库移动
		for (Sys_tree sys_tree : trees) {
			if (fTree.getTreenode().contains(sys_tree.getTreenode())) {
				tmpTrees.add(sys_tree);
			}
			else if (sys_tree.getTreenode().contains(fTree.getTreenode())) {
				if (!sys_tree.getTreenode().equals(tree.getTreenode())) {
					if (!sys_tree.getTreenode().contains(tree.getTreenode())) {
						tmpTrees.add(sys_tree);
					}
				}
			}
		}
		String resultsString = JSON.toJSONString(tmpTrees);
		
		String jsonString = treeService.createTreeJson(resultsString,getProjectPath());
		
		modelMap.put("result", jsonString);
		modelMap.put("id", id);
		return new ModelAndView("/view/system/tree/move",modelMap);
	}
	/**
	 * 移动档案节点夹或节点保存
	 * @param id
	 * @param targetid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/movesave",method=RequestMethod.POST)
	public void movesave(String id,String targetid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (null == id || id.equals("") || null == targetid || targetid.equals("")) {
			out.print(result);
			return;
		}
		
		result = treeService.move(id, targetid);
		
		out.print(result);
	}
	
}
