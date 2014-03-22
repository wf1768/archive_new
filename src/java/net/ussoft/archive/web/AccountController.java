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
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.model.Sys_role;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.ICodeService;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.IRoleService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.MD5;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value="account")
public class AccountController extends BaseConstroller {
	
	@Resource
	private IAccountService accountService;
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
	 * 打开帐户编辑页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView edit(String id,ModelMap modelMap) {
		Sys_account account = accountService.getById(id);
		modelMap.put("account", account);
		
		return new ModelAndView("/view/auth/account/edit", modelMap);
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public void update(Sys_account account,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (account == null ) {
			out.print(result);
			return;
		}
		int num = accountService.update(account);
		if (num > 0 ) {
			result = "success";
		}
		out.print(result);
	}
	
	/**
	 * 打开修改帐户密码页
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/updatepass",method=RequestMethod.GET)
	public ModelAndView updatepass(String id,ModelMap modelMap) {
		Sys_account account = accountService.getById(id);
		modelMap.put("account", account);
		
		return new ModelAndView("/view/auth/account/updatepass", modelMap);
	}
	/**
	 * 执行更新帐户密码
	 * @param id
	 * @param password
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/toupdatepass",method=RequestMethod.POST)
	public void toupdatepass(String id,String password,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		Sys_account account = accountService.getById(id);
		
		if (account == null || password.equals("")) {
			out.print(result);
			return;
		}
		
		account.setPassword(MD5.encode(password).toString());
		
		int num = accountService.update(account);
		if (num > 0 ) {
			result = "success";
		}
		out.print(result);
	}
	/**
	 * 打开移动帐户页面
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
		
		modelMap.put("id", id);
		return new ModelAndView("/view/auth/account/move",modelMap);
	}
	/**
	 * 保存帐户移动
	 * @param id
	 * @param targetid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/movesave",method=RequestMethod.POST)
	public void movesave(String id,String targetid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (null == id || id.equals("") || null == targetid || targetid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = accountService.move(id, targetid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 删除。
	 * @param orgid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void delete(String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (id == null || id.equals("") ) {
			out.print(result);
			return;
		}
		
		int num = accountService.delete(id);
		
		
		if (num > 0) {
			result = "success";
		}
		
		out.print(result);
	}
	
	/**
	 * 更改帐户状态
	 * @param id
	 * @param state
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/updatestate",method=RequestMethod.POST)
	public void updatestate(String id,Integer state,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (id == null || id.equals("") || state == null || state.equals("")) {
			out.print(result);
			return;
		}
		
		//获取帐户实体
		Sys_account account = accountService.getById(id);
		if (account == null) {
			out.print(result);
			return;
		}
		account.setAccountstate(state);
		int num = accountService.update(account);
		
		if (num > 0) {
			result = "success";
		}
		
		out.print(result);
	}
	
	/**
	 * 帐户管理列表。要适应各种版本的管理（集团版，网络版）不分页
	 * @param orgid
	 * @param type
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(String orgid,String type,ModelMap modelMap) throws Exception {
		
		//判断版本,是否是集团版本
//		if (encryService.getInit(4)) {
			String urlString = "";
			if (type != null && type.equals("group")) {
				modelMap = super.getModelMap("GROUP","GROUPACCOUNT");
				urlString = "/view/group/account/list";
			}
			else {
				modelMap = super.getModelMap("AUTH","ACCOUNT");
				urlString = "/view/auth/account/list";
			}
			//获取当前帐户能管理的组织机构
			Sys_account account = super.getSessionAccount();
			
			List<Sys_org> orgList = orgService.orgownerList(account.getId());
			String orgListString = JSON.toJSONString(orgList);
			modelMap.put("orgList", orgListString);
			
			List<HashMap<String, String>> accounts = new ArrayList<HashMap<String,String>>();
			
			//获取选择的组下的帐户
			if (null != orgid && !orgid.equals("")) {
				accounts = accountService.getChildList(orgid);
			}
			modelMap.put("accounts", accounts);
			modelMap.put("orgid", orgid);			
			return new ModelAndView(urlString,modelMap);
//		}
	}
	
	/**
	 * 打开添加页面
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(String orgid,ModelMap modelMap) {
		modelMap.put("orgid", orgid);
		return "/view/auth/account/add";
	}
	/**
	 * 保存新帐户
	 * @param account
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(Sys_account account,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (account == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		account.setId(UUID.randomUUID().toString());
		//生成默认密码md5
		HashMap<String, Object> configMap = getConfig();
		String pass = configMap.get("PASSWORD").toString();
		if (pass != null && !pass.equals("")) {
			pass = MD5.encode(pass);
		}
		else {
			pass = MD5.encode("password");
		}
		account.setPassword(pass);
		account = accountService.insert(account);
		
		if (account == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		out.print(result);
	}
	
	
	/**
	 * 打开设置帐户角色页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/setrole",method=RequestMethod.GET)
	public ModelAndView setrole(String id,ModelMap modelMap) {
		//获取帐户对象
		Sys_account account = accountService.getById(id);
		//获取选中帐户的角色,供页面实现和删除
		Sys_role role = null;
		if (null != account.getRoleid() && !account.getRoleid().equals("")) {
			role = roleService.getById(account.getRoleid());
		}
		modelMap.put("role", role);
		modelMap.put("account", account);
		
		//获取角色供选择
		List<Sys_role> sys_roles = roleService.list();
		
		modelMap.put("sys_roles", sys_roles);
		
		return new ModelAndView("/view/auth/account/setrole",modelMap);
	}
	/**
	 * 保存组的角色
	 * @param orgid
	 * @param roleid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saverole",method=RequestMethod.POST)
	public void saverole(String id,String roleid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("") || roleid == null || roleid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = accountService.saverole(id, roleid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	/**
	 * 移除帐户的角色
	 * @param id
	 * @param roleid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/removerole",method=RequestMethod.POST)
	public void removerole(String id,String roleid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("") || roleid == null || roleid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = accountService.removerole(id, roleid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 打开设置帐户的树节点访问权限页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/setauth",method=RequestMethod.GET)
	public ModelAndView setauth(String id,HttpServletRequest request,ModelMap modelMap) {
		//获取帐户对象
		Sys_account account = accountService.getById(id);
		modelMap.put("account", account);
		
		//获取树节点，用来画树
		List<Sys_tree> trees = treeService.list();
		
		String treeJson = "";
		if (null != trees && trees.size() >0 ) {
			treeJson = JSON.toJSONString(trees);
		}
		treeJson = JSON.toJSONString(trees);
		treeJson = treeService.createTreeJson(treeJson, getProjectPath());
		
//		//通过json对象，插入isparent
//		JSONArray jsonArray = JSON.parseArray(treeString);
//		for (int i=0;i<jsonArray.size();i++) {
//			String typeString = ((JSONObject) jsonArray.get(i)).get("treetype").toString();
//			if (typeString.equals("F") || typeString.equals("T") || typeString.equals("FT")) {
//				((JSONObject) jsonArray.get(i)).put("isParent", true);
//			}
//			if (typeString.equals("F")) {
//				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"images/icons/1.gif");
//				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"images/icons/2.gif");
//			}
//			if (typeString.equals("T") || typeString.equals("FT")) {
//				((JSONObject) jsonArray.get(i)).put("iconClose", basePath+"images/folder.gif");
//				((JSONObject) jsonArray.get(i)).put("iconOpen", basePath+"images/folder-open.gif");
//			}
//		}
//		String jsonString = JSON.toJSONString(jsonArray);
		
		modelMap.put("treeList", treeJson);
		
		//获取当前帐户能访问的树节点，用来checkbox勾选
		List<Sys_tree> accountTrees = accountService.getAccountTree(id);
		
		String accountTreesString = JSON.toJSONString(accountTrees);
		
		modelMap.put("accountTrees", accountTreesString);
		
		//获取电子全文浏览范围代码，供页面填充select
		Sys_code code = new Sys_code();
		code.setTempletfieldid("DOCAUTH");
		List<Sys_code> codes = codeService.selectByWhere(code);
		modelMap.put("codes", codes);
		
		return new ModelAndView("/view/auth/account/setauth",modelMap);
	}
	
	/**
	 * 保存组的树节点赋权
	 * @param orgid
	 * @param treeids
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saveAccountAuth")
	public void saveAccountAuth(String id,String treeids,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		@SuppressWarnings("unchecked")
		List<String> treeList = (List<String>) JSON.parse(treeids);
		
		Boolean b = accountService.saveaccounttree(id, treeList);
		String result = "failure";
		if (b) {
			result = "success";
		}
		out.print(result);
	}
	
	
	/**
	 * 点击树节点，显示当前帐户的树节点辅助权限（全文浏览权、全文下载权、全文打印权、节点下数据访问权、电子全文浏览范围代码）
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/getTreeAuth")
	public void getTreeAuth(String id,String treeid,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		Sys_account_tree account_tree = accountService.getTreeAuth(id, treeid);
		
		String result = "failure";
		if (null == account_tree) {
			out.print(result);
			return;
		}
		
		result = JSON.toJSONString(account_tree);
		//获取组和树的对应
		out.print(result);
	}
	/**
	 * 保存当前帐户的树节点辅助权限（全文浏览权、全文下载权、全文打印权、节点下数据访问权）
	 * @param org_tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saveTreeAuth")
	public void saveTreeAuth(Sys_account_tree account_tree,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		Boolean b = accountService.saveTreeAuth(account_tree);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	
	/**
	 * 打开帐户针对树节点的数据访问权添加页面
	 * @param id
	 * @param treeid
	 * @param tabletype
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/showSetDataAuthWindow",method=RequestMethod.GET)
	public ModelAndView showSetDataAuthWindow(String id,String treeid,String tabletype,ModelMap modelMap) {
		//获取组对象
		Sys_account account = accountService.getById(id);
		modelMap.put("account", account);
		
		//获取树对象
		Sys_tree tree = treeService.getById(treeid);
		modelMap.put("tree", tree);
		
		modelMap.put("tabletype", tabletype);
		
		//获取tree对应的模版字段列表
		List<Sys_templetfield> templetfields = treeService.geTempletfields(treeid, tabletype);
		modelMap.put("templetfields", templetfields);
		
		return new ModelAndView("/view/auth/account/setdataauth",modelMap);
	}
	/**
	 * 保存帐户的数据访问权限
	 * @param account_tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="saveDataAuth",method=RequestMethod.POST)
	public void saveDataAuth(Sys_account_tree account_tree,String tabletype,String filter,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		if (null == account_tree || tabletype.equals("") || filter.equals("")) {
			out.print(result);
			return;
		}
		//去掉org_tree的filter，可能前台传入的时候会自动加到类里，这样查询不出来
		account_tree.setFilter(null);
		Boolean b = accountService.saveDataAuth(account_tree,tabletype,filter);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	/**
	 * 删除帐户与树关联的数据访问权限
	 * @param accounttreeid
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="removeDataAuth",method=RequestMethod.POST)
	public void removeDataAuth(String accounttreeid,String id,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		if (null == accounttreeid || accounttreeid.equals("") || null == id || id.equals("")) {
			out.print(result);
			return;
		}
		Boolean b = accountService.removeDataAuth(accounttreeid, id);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	
	/**
	 * 保存帐户的树节点下电子全文浏览范围权限
	 * @param account_tree
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/saveDocAuth")
	public void saveDocAuth(Sys_account_tree account_tree,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		Boolean b = accountService.saveDocAuth(account_tree);
		
		if (b) {
			result = "success";
		}
		//获取组和树的对应
		out.print(result);
	}
	
	
}
