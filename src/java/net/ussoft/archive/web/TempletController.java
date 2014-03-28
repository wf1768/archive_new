package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITempletService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping(value="templet")
public class TempletController extends BaseConstroller {
	
	@Resource
	private ITempletService templetService;
	@Resource
	private ITableService tableService;
//	@Autowired
//	private  HttpServletRequest request;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String selectid,ModelMap modelMap) {
		
        
		modelMap = super.getModelMap("SYSTEM","TEMPLET");
		
		String order = " order by sort asc";
		List<Object> values = new ArrayList<Object>();
		
		List<HashMap<String,Object>> result = createTree();
		String resultsString = JSON.toJSONString(result);
		modelMap.put("result", resultsString);
		modelMap.put("selectid", selectid);
		
//		Sys_templet first = new Sys_templet();
//		first.setId("0");
//		first.setTempletname("档案库");
//		first.setTemplettype("T");

//		templetsTree.add(first);
//		String resultsString = JSON.toJSONString(templetsTree);
//		modelMap.put("result", resultsString);
//		modelMap.put("selectid", selectid);
		
		//获取点击后页面右侧档案类型列表
		if (null == selectid || selectid.equals("")) {
			selectid = "0";
		}
		
		values.clear();
		values.add(selectid);
		List<Sys_templet> templets = templetService.list(" where parentid =?", values, order);
		modelMap.put("templets", templets);
		modelMap.put("selectid", selectid);
		
		return new ModelAndView("/view/system/templet/list",modelMap);
	}
	
	private List<HashMap<String,Object>> createTree() {
		
//		String path = request.getContextPath();
//    	String basePath = request.getScheme() + "://"
//    			+ request.getServerName() + ":" + request.getServerPort()
//    			+ path + "/";

		String basePath = getProjectBasePath();
		String where = "";
		String order = " order by sort asc";
		
		
		where = " where templettype not like 'C%'";
		
		List<Object> values = new ArrayList<Object>();
		List<Sys_templet> templetsTree = templetService.list(where, values, order);
		
		//生成档案类型页面需要的templet tree的格式list
		List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
		//生成tree格式
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("id", "0");
		map.put("name", "档案库");
		map.put("templettype", "T");
		map.put("isParent", true);
		map.put("isleaf", false);
//		map.put("open", true);
		List<HashMap<String,Object>> childList = createList("0",templetsTree,basePath);
		map.put("children", childList);
		result.add(map);
		
		return result;
	}
	
	private List<HashMap<String,Object>> createList(String parentid,List<Sys_templet> templets,String path) {
		
		List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
		
		for (Sys_templet templet : templets) {
			if (templet.getParentid().equals(parentid)) {
				HashMap<String,Object> map = new HashMap<String,Object>();
				if (templet.getTemplettype().equals("T")) {
					map.put("id", templet.getId());
					map.put("name", templet.getTempletname());
					map.put("templettype", templet.getTemplettype());
					map.put("isParent", 1);
					map.put("isleaf", 0);
					map.put("children", this.createList(templet.getId(), templets, path));
					
					result.add(map);
				}
				else {
					map.put("id", templet.getId());
					map.put("name", templet.getTempletname());
					map.put("templettype", templet.getTemplettype());
					map.put("isParent", 0);
					map.put("isleaf", 1);
					map.put("icon", path+"images/icons/1.gif");
					result.add(map);
				}
			}
			
		}
		return result;
	}
	
	/**
	 * 打开添加 T 类型夹页面
	 * @param tableid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/addT",method=RequestMethod.GET)
	public String addT(String parentid,ModelMap modelMap) {
		modelMap.put("parentid", parentid);
		return "/view/system/templet/addT";
	}
	/**
	 * 
	 * @param parentid
	 * @param m
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(String parentid,String m,ModelMap modelMap) {
		modelMap.put("parentid", parentid);
		modelMap.put("m", m);
		//如果m参数为c，是基础模版，读取基础模版，返回前台select
		String where = "";
		String order = " order by sort asc";
		if (m.equals("c")) {
			where = " where templettype like 'C%'";
		}
		else if (m.equals("a")) {
			where = " where templettype = 'A' or templettype = 'F' or templettype = 'P'";
		}
		
		List<Object> values = new ArrayList<Object>();
		List<Sys_templet> templets = templetService.list(where, values, order);
		modelMap.put("templets", templets);
		
		return "/view/system/templet/add";
	}
	
	/**
	 * 
	 * @param id
	 * @param type
	 * @param value
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(String parentid,String templetname,String copyTempletid,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (null == parentid || parentid.equals("") || null == templetname || templetname.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Sys_templet templet = new Sys_templet();
		templet.setId(UUID.randomUUID().toString());
		templet.setParentid(parentid);
		templet.setTempletname(templetname);
		if (null == copyTempletid || copyTempletid.equals("")) {
			templet.setTemplettype("T");
		}
		else {
			Sys_templet tmp = templetService.getByid(copyTempletid);
			String type = tmp.getTemplettype();
			if (type.equals("CA")) {
				templet.setTemplettype("A");
			}
			else if (type.equals("CF")) {
				templet.setTemplettype("F");
			}
			else if (type.equals("CP")) {
				templet.setTemplettype("P");
			}
			else {
				templet.setTemplettype(tmp.getTemplettype());
			}
		}
		
		
		result = templetService.insert(templet,copyTempletid);
		out.print(result);
	}
	/**
	 * 打开编辑档案类型页面
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
		Sys_templet templet = templetService.getByid(id);
		modelMap.put("templet", templet);
		return new ModelAndView("/view/system/templet/edit",modelMap);
	}
	/**
	 * 执行更新
	 * @param templetfield
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public void update(Sys_templet templet,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (templet == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = templetService.update(templet);
		if (num <= 0 ) {
			result = "failure";
		}
		out.print(result);
	}
	/**
	 * 打开档案类型移动页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/move",method=RequestMethod.GET)
	public ModelAndView move(String id,ModelMap modelMap) {
		
		List<HashMap<String,Object>> result = createTree();
		String resultsString = JSON.toJSONString(result);
		modelMap.put("result", resultsString);
		
		modelMap.put("id", id);
		return new ModelAndView("/view/system/templet/move",modelMap);
	}
	/**
	 * 移动档案类型保存
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
		
		result = templetService.move(id, targetid);
		
		out.print(result);
	}
	/**
	 * 打开档案类型排序更改页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/sort",method=RequestMethod.GET)
	public ModelAndView sort(String id,ModelMap modelMap) {
		
		Sys_templet templet = templetService.getByid(id);
		modelMap.put("templet", templet);
		
		return new ModelAndView("/view/system/templet/sort",modelMap);
	}
	/**
	 * 保存排序
	 * @param templet
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/sortsave",method=RequestMethod.POST)
	public void sortsave(Sys_templet templet,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (templet == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = templetService.sortsave(templet);
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
		result = templetService.delete(id);
		out.print(result);
	}

}
