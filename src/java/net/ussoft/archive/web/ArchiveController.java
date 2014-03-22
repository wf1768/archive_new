package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.ITreeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * 档案管理
 * @author wangf
 *
 */

@Controller
@RequestMapping(value="archive")
public class ArchiveController extends BaseConstroller {
	
	@Resource
	private ITreeService treeService;
	@Resource
	private IDynamicService dynamicService;
	
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView index(ModelMap modelMap) {
		modelMap = super.getModelMap("ARCHIVEMANAGE","");
		return new ModelAndView("/view/archive/index",modelMap);
	}
	
	
	/**
	 * 档案管理
	 * @param selectid		前台选择的treeid
	 * @param tabletype		表类型  01 or 02
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String selectid,String tabletype,Integer page,ModelMap modelMap) {
		
		if (null == page) {
			page = 1;
		}
		
		if (null == tabletype || tabletype.equals("")) {
			tabletype = "01";
		}
        
		modelMap = super.getModelMap("ARCHIVEMANAGE","ARCHIVE");
		
//		String where = "";
//		String order = " order by sort asc";
//		List<Object> values = new ArrayList<Object>();
		//获取当前session登录帐户
		Sys_account account = getSessionAccount();
		//根据当前帐户id，获取帐户的档案树节点，用来档案管理里画档案树
		List<Sys_tree> trees = treeService.getAuthTree(account.getId());
		String treeJson = "";
		if (null != trees && trees.size() >0 ) {
			treeJson = JSON.toJSONString(trees);
		}
		
		treeJson = treeService.createTreeJson(treeJson, getProjectPath());
		
		modelMap.put("result", treeJson);
		modelMap.put("selectid", selectid);
		
		
		
//		values.clear();
//		values.add(selectid);
//		List<Sys_tree> treeList = treeService.list(" where parentid =?", values, order);
//		modelMap.put("trees", treeList);
		
		//获取点击后页面右侧档案类型列表
		PageBean<Map<String, Object>> pageBean = new PageBean<Map<String,Object>>();
		if (null == selectid || selectid.equals("")) {
			selectid = "0";
		}
		else {
			HashMap<String, Object> configMap = getConfig();
			Integer pageSize = Integer.parseInt(configMap.get("PAGE").toString());
			
			//获取档案字段
			List<Sys_templetfield> fieldList = treeService.geTempletfields(selectid, tabletype);
			
			if (pageSize == 0) {
				pageBean.setIsPage(false);
			}
			else {
				pageBean.setIsPage(true);
				pageBean.setPageSize(pageSize);
				pageBean.setPageNo(page);
			}
			
			//获取当前treeid下数据
			pageBean = dynamicService.archiveList(selectid, tabletype, pageBean);
			modelMap.put("fields", fieldList);
		}
		modelMap.put("pagebean", pageBean);
		
		modelMap.put("selectid", selectid);
		
		return new ModelAndView("/view/archive/archive/list",modelMap);
	}
	
}
