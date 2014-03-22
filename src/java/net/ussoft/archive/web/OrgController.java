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
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_role;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ICodeService;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.IRoleService;
import net.ussoft.archive.service.ITreeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value="org")
public class OrgController extends BaseConstroller {
	
	@Resource
	private IOrgService orgService;
	@Resource
	private IEncryService encryService;
	@Resource
	private IRoleService roleService;
	@Resource
	private ITreeService treeService;
	@Resource
	private ICodeService codeService;
	
	/**
	 * 组列表
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String orgid,String type,ModelMap modelMap) throws Exception {
		
		String version = "no";
		
		//判断版本,是否是集团版本
		if (encryService.getInit(4)) {
			version = "group";
			String urlString = "";
			if (type != null && type.equals("group")) {
				modelMap = super.getModelMap("GROUP","GROUPORG");
				urlString = "/view/group/org/list";
			}
			else {
				modelMap = super.getModelMap("AUTH","ORG");
				urlString = "/view/auth/org/list";
			}
			
			//获取组
//			List<Sys_org> orgList = orgService.list();
			//得到当前帐户为所有者的org节点
			Sys_account account = super.getSessionAccount();
			
			List<Sys_org> orgList = orgService.orgownerList(account.getId());
			
			
			String orgListString = JSON.toJSONString(orgList);
			modelMap.put("orgList", orgListString);
			
//			PageBean<Sys_org> pageBean = new PageBean<Sys_org>();
			List<HashMap<String, String>> childList = new ArrayList<HashMap<String,String>>();
			//获取数据
			if (null == orgid || orgid.equals("")) {
				
			}
			else {
				//获取当前帐户作为owner的组对象
				//List<Sys_org> ownerList = orgService.getorgowner(account.getId());
				//获取当前组的treenode，来判断，点击的组是否应该显示子组。例如如果当前帐户作为三级树的owner，为了画树，它的父节点也都画出来了，点击父节点
				//就不应该显示子节点,因为当前帐户不是父节点的owner
				//Sys_org tmpOrg = orgService.getById(orgid);
				
				//判断前台选择的组是否在当前帐户的owner范围内
//				Boolean isowner = false;
//				for (Sys_org sys_org : ownerList) {
//					String orgTreeNodeString = sys_org.getTreenode();
//					if (orgTreeNodeString.equals(tmpOrg.getTreenode()) || !orgTreeNodeString.contains(tmpOrg.getTreenode())) {
//						isowner = true;
//						break;
//					}
//				}
//				if (isowner) {
//					childList = orgService.getChildList(orgid);
//				}
				childList = orgService.getChildList(orgid);
			}
			
//			modelMap.put("childList", pageBean.getList());
			modelMap.put("childList", childList);
			
			modelMap.put("version", version);
			
			modelMap.put("orgid", orgid);
			return new ModelAndView(urlString,modelMap);
		}
		
		//如果是非集团版本(网络、单机)
		//获取数据
		modelMap = super.getModelMap("AUTH","ORG");
		List<Sys_org> orgList = orgService.list();
		String orgListString = JSON.toJSONString(orgList);
		modelMap.put("orgList", orgListString);
		List<HashMap<String, String>> childList = new ArrayList<HashMap<String,String>>();
		if (null != orgid && !orgid.equals("")) {
			childList = orgService.getChildList(orgid);
		}
		modelMap.put("orgid", orgid);
		modelMap.put("childList", childList);
		
		modelMap.put("version", version);
		
		return new ModelAndView("/view/auth/org/list",modelMap);
	}
	
	/**
	 * 打开添加页面
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(String parentid,ModelMap modelMap) {
		modelMap.put("parentid", parentid);
		return "/view/group/org/add";
	}
	/**
	 * 保持新建组
	 * @param org
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(Sys_org org,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (org == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		org.setId(UUID.randomUUID().toString());
		org = orgService.insert(org);
		
		if (org == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		//重新获取org实体，因为自增长的orgindex没有获得。
		org = orgService.getById(org.getId());
		//更新treenode
		//获取父节点的treenode
		Sys_org parOrg = orgService.getById(org.getParentid());
		if (parOrg != null) {
			String treenodeString = parOrg.getTreenode() + "#" + org.getOrgindex();
			org.setTreenode(treenodeString);
			orgService.update(org);
		}
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
		Sys_org org = orgService.getById(id);
		modelMap.put("org", org);
		return new ModelAndView("/view/group/org/edit",modelMap);
	}
	
	/**
	 * 执行更新
	 * @param id
	 * @param value
	 * @param modelMap
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public void update(Sys_org org,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (org == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = orgService.update(org);
		if (num <= 0 ) {
			result = "failure";
		}
		out.print(result);
	}
	
	/**
	 * 删除组。
	 * @param orgid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void delete(String orgid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") ) {
			result = "failure";
			out.print(result);
			return;
		}
		
		int num = orgService.delete(orgid);
		
		if (num <= 0) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 打开移动组页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/move",method=RequestMethod.GET)
	public ModelAndView move(String id,ModelMap modelMap) {
		Sys_account account = super.getSessionAccount();
		List<Sys_org> orgList = orgService.orgownerList(account.getId());
		
		String orgListString = JSON.toJSONString(orgList);
		modelMap.put("orgList", orgListString);
		
		Sys_org org = orgService.getById(id);
		
		modelMap.put("org", org);
		return new ModelAndView("/view/group/org/move",modelMap);
	}
	/**
	 * 保存移动组
	 * @param id
	 * @param targetid
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping(value="/movesave",method=RequestMethod.POST)
	public void movesave(String id,String targetid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("") || targetid == null || targetid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.move(id, targetid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 集团版打开设置组的管理者页面
	 * @param orgid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/setowner",method=RequestMethod.GET)
	public ModelAndView setowner(String orgid,String selectOrgid,ModelMap modelMap) {
		//获取选中组的所有者,供页面实现和删除
		List<Sys_account> owners = orgService.getowner(orgid);
		modelMap.put("owners", owners);
		//获取当前帐户作为owner的组及子组.供页面实现组树，点击时，显示组下的帐户
		Sys_account account = super.getSessionAccount();
		List<Sys_org> orgList = orgService.orgownerList(account.getId());
		
		String orgListString = JSON.toJSONString(orgList);
		modelMap.put("orgList", orgListString);
		
		Sys_org org = orgService.getById(orgid);
		modelMap.put("org", org);
		
		
		//页面上点击了组节点，获取该组下所有帐户数据，供选择为owner
		if (null != selectOrgid && !selectOrgid.equals("")) {
			List<Sys_account> accounts = orgService.getAccounts(selectOrgid);
			modelMap.put("accounts", accounts);
			modelMap.put("selectOrgid", selectOrgid);
		}
		return new ModelAndView("/view/group/org/setowner",modelMap);
	}
	
	/**
	 * 移除组的所有者
	 * @param orgid
	 * @param accountid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/removeowner",method=RequestMethod.POST)
	public void removeowner(String orgid,String accountid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") || accountid == null || accountid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.removeowner(orgid, accountid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	/**
	 * 保存组的所有者（集团版本需要）
	 * @param orgid
	 * @param accountid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saveowner",method=RequestMethod.POST)
	public void saveowner(String orgid,String accountid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") || accountid == null || accountid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.saveowner(orgid, accountid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 打开设置组角色页面
	 * @param orgid
	 * @param selectOrgid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/setrole",method=RequestMethod.GET)
	public ModelAndView setrole(String orgid,ModelMap modelMap) {
		//获取组对象
		Sys_org org = orgService.getById(orgid);
		//获取选中组的角色,供页面实现和删除
		Sys_role role = null;
		if (null != org.getRoleid() && !org.getRoleid().equals("")) {
			role = roleService.getById(org.getRoleid());
		}
		modelMap.put("role", role);
		modelMap.put("org", org);
		
		//获取角色供选择
		List<Sys_role> sys_roles = roleService.list();
		
		modelMap.put("sys_roles", sys_roles);
		
		return new ModelAndView("/view/auth/org/setrole",modelMap);
	}
	/**
	 * 保存组的角色
	 * @param orgid
	 * @param roleid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saverole",method=RequestMethod.POST)
	public void saverole(String orgid,String roleid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") || roleid == null || roleid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.saverole(orgid, roleid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	/**
	 * 移除组的角色
	 * @param orgid
	 * @param roleid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/removerole",method=RequestMethod.POST)
	public void removerole(String orgid,String roleid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") || roleid == null || roleid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.removerole(orgid, roleid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 打开设置组的树节点访问权限页面
	 * @param orgid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/setauth",method=RequestMethod.GET)
	public ModelAndView setauth(String orgid,HttpServletRequest request,ModelMap modelMap) {
		//获取组对象
		Sys_org org = orgService.getById(orgid);
		modelMap.put("org", org);
		
		//获取树节点，用来画树
		List<Sys_tree> treeList = treeService.list();
		String treeString = JSON.toJSONString(treeList);
		
		String path = request.getContextPath();
    	String basePath = request.getScheme() + "://"
    			+ request.getServerName() + ":" + request.getServerPort()
    			+ path + "/";
    	
		//通过json对象，插入isparent
		JSONArray jsonArray = JSON.parseArray(treeString);
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
		
		modelMap.put("treeList", jsonString);
		
		//获取当前组能访问的树节点，用来checkbox勾选
		List<Sys_tree> orgTrees = orgService.getOrgTree(orgid);
		String orgTreesString = JSON.toJSONString(orgTrees);
		modelMap.put("orgTrees", orgTreesString);
		
		//获取电子全文浏览范围代码，供页面填充select
		Sys_code code = new Sys_code();
		code.setTempletfieldid("DOCAUTH");
		List<Sys_code> codes = codeService.selectByWhere(code);
		modelMap.put("codes", codes);
		
		return new ModelAndView("/view/auth/org/setauth",modelMap);
	}
	/**
	 * 保存组的树节点赋权
	 * @param orgid
	 * @param treeids
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saveOrgAuth")
	public void saveOrgAuth(String orgid,String treeids,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		@SuppressWarnings("unchecked")
		List<String> treeList = (List<String>) JSON.parse(treeids);
		
		Boolean b = orgService.saveorgtree(orgid, treeList);
		String result = "failure";
		if (b) {
			result = "success";
		}
		out.print(result);
	}
	/**
	 * 点击树节点，显示当前组的树节点辅助权限（全文浏览权、全文下载权、全文打印权、节点下数据访问权、电子全文浏览范围代码）
	 * @param orgid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/getTreeAuth")
	public void getTreeAuth(String orgid,String treeid,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		Sys_org_tree org_tree = orgService.getTreeAuth(orgid, treeid);
		
		String result = "failure";
		if (null == org_tree) {
			out.print(result);
			return;
		}
		
		result = JSON.toJSONString(org_tree);
		//获取组和树的对应
		out.print(result);
	}
	/**
	 * 保存当前组的树节点辅助权限（全文浏览权、全文下载权、全文打印权、节点下数据访问权）
	 * @param org_tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saveTreeAuth")
	public void saveTreeAuth(Sys_org_tree org_tree,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		Boolean b = orgService.saveTreeAuth(org_tree);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	
	/**
	 * 打开组针对树节点的数据访问权添加页面
	 * @param orgid
	 * @param treeid
	 * @param tabletype
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/showSetDataAuthWindow",method=RequestMethod.GET)
	public ModelAndView showSetDataAuthWindow(String orgid,String treeid,String tabletype,ModelMap modelMap) {
		//获取组对象
		Sys_org org = orgService.getById(orgid);
		modelMap.put("org", org);
		
		//获取树对象
		Sys_tree tree = treeService.getById(treeid);
		modelMap.put("tree", tree);
		
		modelMap.put("tabletype", tabletype);
		
		//获取tree对应的模版字段列表
		List<Sys_templetfield> templetfields = treeService.geTempletfields(treeid, tabletype);
		modelMap.put("templetfields", templetfields);
		
		return new ModelAndView("/view/auth/org/setdataauth",modelMap);
	}
	/**
	 * 保存组的数据访问权限
	 * @param org_tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="saveDataAuth",method=RequestMethod.POST)
	public void saveDataAuth(Sys_org_tree org_tree,String tabletype,String filter,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		if (null == org_tree || tabletype.equals("") || filter.equals("")) {
			out.print(result);
			return;
		}
		//去掉org_tree的filter，可能前台传入的时候会自动加到类里，这样查询不出来
		org_tree.setFilter(null);
		Boolean b = orgService.saveDataAuth(org_tree,tabletype,filter);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	/**
	 * 删除组与树关联的数据访问权限
	 * @param orgtreeid
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="removeDataAuth",method=RequestMethod.POST)
	public void removeDataAuth(String orgtreeid,String id,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		if (null == orgtreeid || orgtreeid.equals("") || null == id || id.equals("")) {
			out.print(result);
			return;
		}
		Boolean b = orgService.removeDataAuth(orgtreeid, id);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	
	/**
	 * 保存组的树节点下电子全文浏览范围权限
	 * @param org_tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saveDocAuth")
	public void saveDocAuth(Sys_org_tree org_tree,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		Boolean b = orgService.saveDocAuth(org_tree);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	
	
}
