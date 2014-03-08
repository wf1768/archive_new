package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_templetfield;
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
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String selectid,ModelMap modelMap) {
		
		String where = "";
		String order = " order by sort asc";
		
		modelMap = super.getModelMap("SYSTEM","TEMPLET");
		where = " where templettype not like 'C%'";
		
		List<Object> values = new ArrayList<Object>();
		List<Sys_templet> templets = templetService.list(where, values, order);
		
		//获取全部table表内容
		List<Sys_table> tables = tableService.list();
		
		//生成档案类型页面需要的templet tree的格式list
		List<HashMap> result = new ArrayList<HashMap>();
		
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
			
		
		String resultsString = JSON.toJSONString(result);
		System.out.println(resultsString);
		modelMap.put("result", resultsString);
		modelMap.put("selectid", selectid);
		
		//获取field列表
		if (null != selectid && !selectid.equals("")) {
			List<Sys_templetfield> templetfields = tableService.geTempletfields(selectid);
			modelMap.put("templetfields", templetfields);
		}
		
		return new ModelAndView("/view/system/templet/list",modelMap);
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
	
	
	
	@RequestMapping(value="/list1",method=RequestMethod.GET)
	public ModelAndView list1(String m,String selectid,ModelMap modelMap) {
		
		String where = "";
		String order = " order by sort asc";
		//m在url里，作为判断是基础模版还是用户档案。如果是c表示基础模版
		if (null != m && m.equals("c")) {
			modelMap = super.getModelMap("SYSTEM","CTEMPLET");
			where = " where templettype like 'C%'";
		}
		else {
			modelMap = super.getModelMap("SYSTEM","TEMPLET");
			where = " where templettype not like 'C%'";
			
		}
		
		List<Object> values = new ArrayList<Object>();
		List<Sys_templet> templets = templetService.list(where, values, order);
		
		//获取全部table表内容
		List<Sys_table> tables = tableService.list();
		
		//生成档案类型页面需要的templet tree的格式list
		List<HashMap> result = new ArrayList<HashMap>();
		for (Sys_templet templet : templets) {
			HashMap map = new HashMap();
//			map.put("id", templet.getId());
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
		
		return new ModelAndView("/view/system/templet/list",modelMap);
	}
	

}
