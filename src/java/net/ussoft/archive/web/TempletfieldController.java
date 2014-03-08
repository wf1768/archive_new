package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.service.ICodeService;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITempletService;
import net.ussoft.archive.service.ITempletfieldService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;


@Controller
@RequestMapping(value="templetfield")
public class TempletfieldController extends BaseConstroller {
	
	@Resource
	private ITempletService templetService;
	@Resource
	private ITableService tableService;
	@Resource
	private ITempletfieldService templetfieldService;
	
	@Resource
	private ICodeService codeService;
	
	/**
	 * 打开字段列表页面
	 * @param m
	 * @param selectid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String m,String selectid,ModelMap modelMap) {
		
		String where = "";
		String order = " order by sort asc";
		//m在url里，作为判断是基础模版还是用户档案。如果是c表示基础模版
		if (null != m && m.equals("c")) {
			modelMap = super.getModelMap("SYSTEM","CTEMPLETFIELD");
			where = " where templettype like 'C%'";
		}
		else {
			modelMap = super.getModelMap("SYSTEM","TEMPLETFIELD");
			where = " where templettype not like 'C%'";
			
		}
		
		List<Object> values = new ArrayList<Object>();
		List<Sys_templet> templets = templetService.list(where, values, order);
		
		//获取全部table表内容
		List<Sys_table> tables = tableService.list();
		
		//生成档案类型页面需要的templet tree的格式list
		List<HashMap> result = new ArrayList<HashMap>();
		
		if (null != m && m.equals("c")) {
			for (Sys_templet templet : templets) {
				HashMap map = new HashMap();
//				map.put("id", templet.getId());
				map.put("name", templet.getTempletname());
				map.put("isParent", true);
				map.put("isleaf", false);
				
				List<HashMap> childList = new ArrayList<HashMap>();
				
				for (Sys_table table : tables) {
					HashMap childMap = new HashMap();
					if (templet.getId().equals(table.getTempletid())) {
						childMap.put("id",table.getId());
						childMap.put("name", table.getTablelabel());
						childMap.put("tabletype", table.getTabletype());
						childMap.put("isleaf", true);
					}
					if (childMap.size() > 0) {
						childList.add(childMap);
					}
				}
				
				if (childList.size() >0) {
					map.put("children", childList);
				}
				result.add(map);
			}
		}
		else {
			//非模版档案类型。与模版档案类型显示不一样，单独处理。
			HashMap map = new HashMap();
			map.put("id", "0");
			map.put("name", "档案库");
			map.put("templettype", "T");
			map.put("isParent", true);
			map.put("isleaf", false);
			map.put("open", true);
			List<HashMap> childList = createList("0",templets,tables);
			map.put("children", childList);
			result.add(map);
			
		}
		
		String resultsString = JSON.toJSONString(result);
		System.out.println(resultsString);
		modelMap.put("result", resultsString);
		modelMap.put("m", m);
		modelMap.put("selectid", selectid);
		
		//获取field列表
		if (null != selectid && !selectid.equals("")) {
			List<Sys_templetfield> templetfields = tableService.geTempletfields(selectid);
			modelMap.put("templetfields", templetfields);
		}
		
		return new ModelAndView("/view/system/templetfield/list",modelMap);
	}
	
	private List<HashMap> createList(String parentid,List<Sys_templet> templets,List<Sys_table> tables) {
		
		List<HashMap> result = new ArrayList<HashMap>();
		
		for (Sys_templet templet : templets) {
			if (templet.getParentid().equals(parentid)) {
				HashMap map = new HashMap();
	//			map.put("id", templet.getId());
				map.put("name", templet.getTempletname());
				map.put("templettype", templet.getTemplettype());
				map.put("isParent", true);
				map.put("isleaf", false);
				
				if (!templet.getTemplettype().equals("T")) {
					List<HashMap> childList = new ArrayList<HashMap>();
					
					for (Sys_table table : tables) {
						HashMap childMap = new HashMap();
						if (templet.getId().equals(table.getTempletid())) {
							childMap.put("id",table.getId());
							childMap.put("name", table.getTablelabel());
							childMap.put("tabletype", table.getTabletype());
							childMap.put("isleaf", true);
						}
						if (childMap.size() > 0) {
							childList.add(childMap);
						}
					}
					
					if (childList.size() >0) {
						map.put("children", childList);
					}
					
					result.add(map);
				}
				else {
					map.put("children", this.createList(templet.getId(), templets, tables));
					result.add(map);
				}
			}
			
		}
		return result;
	}
	
	
	
	
	/**
	 * 打开添加页面
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(String tableid,ModelMap modelMap) {
		modelMap.put("tableid", tableid);
		return "/view/system/templetfield/add";
	}
	
	/**
	 * 保存字段
	 * @param org
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(Sys_templetfield templetfield,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (templetfield == null || null == templetfield.getEnglishname() || templetfield.getEnglishname().equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		templetfield.setId(UUID.randomUUID().toString());
		result = templetfieldService.insert(templetfield);
		
		out.print(result);
	}
	
	/**
	 * 打开编辑字段页面
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
		Sys_templetfield templetfield = templetfieldService.getById(id);
		modelMap.put("field", templetfield);
		return new ModelAndView("/view/system/templetfield/edit",modelMap);
	}
	/**
	 * 执行更新
	 * @param templetfield
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public void update(Sys_templetfield templetfield,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (templetfield == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = templetfieldService.update(templetfield);
		if (num <= 0 ) {
			result = "failure";
		}
		out.print(result);
	}
	/**
	 * 删除字段
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void delete(String id,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (null == id || id.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = templetfieldService.delete(id);
		if (num <= 0 ) {
			result = "failure";
		}
		out.print(result);
	}
	/**
	 * 设置排序
	 * @param id
	 * @param type
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/sort",method=RequestMethod.POST)
	public void sort(String id,String type,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (null == id || id.equals("") || null == type || type.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		Boolean b = templetfieldService.sort(id, type);
		if (!b ) {
			result = "failure";
		}
		out.print(result);
	} 
	
	/**
	 * 修改字段的一些属性，例如检索字段、列表显示的快捷修改
	 * @param templetfield
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/updateOtherInfo",method=RequestMethod.POST)
	public void updateOtherInfo(String id,String type,Integer value,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		Boolean b = templetfieldService.updateOtherInfo(id, type, value);
		if (!b) {
			result = "failure";
		}
		out.print(result);
	}
	/**
	 * 复制字段。将字段id存入session。粘贴时读取
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/fieldcopy",method=RequestMethod.POST)
	public void fieldcopy(String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (null == id || id.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		//将copy的id存入session
		CommonUtils.setSessionAttribute(request, Constants.field_copy_session, id);
		out.print(result);
	}
	/**
	 * 粘贴字段代码，复制的字段id已经在session里了，参数targetid为粘贴的字段id
	 * @param targetid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/fieldpaste",method=RequestMethod.POST)
	public void fieldpaste(String tableid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (null == tableid || tableid.equals("")) {
			out.print(result);
			return;
		}
		
		//获取session里的复制字段id
		String fieldid = (String) CommonUtils.getSessionAttribute(request, Constants.field_copy_session);
		
		if (null == fieldid || fieldid.equals("")) {
			out.print(result);
			return;
		}
		Boolean b = templetfieldService.fieldpaste(fieldid, tableid);
		
		if (b) {
			result = "success";
		}
		out.print(result);
	}
	
	
	
	/**
	 * 打开字段代码页
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/fieldcode",method=RequestMethod.GET)
	public ModelAndView fieldcode(String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
		//获取对象
		Sys_templetfield templetfield = templetfieldService.getById(id);
		modelMap.put("field", templetfield);
		
		//获取sys_code
		List<Object> values = new ArrayList<Object>();
		values.add(id);
		List<Sys_code> codes = codeService.list("where templetfieldid=? ", values, " order by codeorder asc");
		modelMap.put("codes", codes);
		return new ModelAndView("/view/system/templetfield/code",modelMap);
	}
	/**
	 * 保存代码
	 * @param id
	 * @param columndata
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/savecode",method=RequestMethod.POST)
	public void savecode(String id,String columndata,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		Boolean b = templetfieldService.insertFieldCode(id, columndata);
		if (!b) {
			result = "failure";
		}
		out.print(result);
	}
	
	/**
	 * 删除代码
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delcode",method=RequestMethod.POST)
	public void delcode(String id,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		Boolean b = templetfieldService.delCode(id);
		if (!b) {
			result = "failure";
		}
		out.print(result);
	}
	/**
	 * 执行排序字段代码code
	 * @param id
	 * @param type
	 * @param response
	 * @throws IOException
	 */
	
	@RequestMapping(value="/sortcode",method=RequestMethod.POST)
	public void sortcode(String id,String type,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (null == id || id.equals("") || null == type || type.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		Boolean b = templetfieldService.sortcode(id, type);
		if (!b ) {
			result = "failure";
		}
		out.print(result);
	}
	/**
	 * copy代码id，用于其他字段粘贴,将copy的id放入session，粘贴时读取
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/copycode",method=RequestMethod.POST)
	public void copycode(String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (null == id || id.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		//将copy的id存入session
		CommonUtils.setSessionAttribute(request, Constants.code_copy_session, id);
		out.print(result);
	}
	/**
	 * 粘贴字段代码，复制的字段id已经在session里了，参数targetid为粘贴的字段id
	 * @param targetid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/pastecode",method=RequestMethod.POST)
	public void pastecode(String targetid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (null == targetid || targetid.equals("")) {
			out.print(result);
			return;
		}
		
		//获取session里的复制codeid
		String fieldid = (String) CommonUtils.getSessionAttribute(request, Constants.code_copy_session);
		
		if (null == fieldid || fieldid.equals("")) {
			out.print(result);
			return;
		}
		Boolean b = templetfieldService.pastecode(fieldid, targetid);
		
		if (b) {
			result = "success";
		}
		out.print(result);
	}
	
}
