package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_function;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.model.Sys_role;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IFunctionService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.IRoleService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

/**
 * 角色管理
 * @author wangf
 *
 */

@Controller
@RequestMapping(value="role")
public class RoleController extends BaseConstroller {
	
	
	@Resource
	private IRoleService roleService;
	@Resource
	private IFunctionService functionService;
	@Resource
	private IEncryService encryService;
	@Resource
	private IOrgService orgService;
	@Resource
	private IAccountService accountService;
	
	/**
	 * 系统维护页面打开列表
	 * @param modelMap
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String orgid,String type,ModelMap modelMap) throws Exception {
		
		//判断版本
		if (encryService.getInit(4)) {
			String urlString = "";
			if (type != null && type.equals("group")) {
				modelMap = super.getModelMap("GROUP","GROUPROLE");
				urlString = "/view/group/role/list";
			}
			else {
				modelMap = super.getModelMap("AUTH","ROLE");
				urlString = "/view/auth/role/list";
			}
			
			//获取当前帐户能管理的组织机构
			Sys_account account = super.getSessionAccount();
			
			List<Sys_org> orgList = orgService.orgownerList(account.getId());
			String orgListString = JSON.toJSONString(orgList);
			modelMap.put("orgList", orgListString);
			
//			//获取角色组
//			List<Sys_org> orgList = orgService.list();
//			String orgListString = JSON.toJSONString(orgList);
//			modelMap.put("orgList", orgListString);
			//获取数据
			List<Sys_role> roleList = new ArrayList<Sys_role>();
			
			//获取选择的组下的帐户
			if (null != orgid && !orgid.equals("")) {
				Sys_role role = new Sys_role();
				if (orgid != null) {
					role.setOrgid(orgid);
				}
				roleList = roleService.list(role);
			}
			
			modelMap.put("roleList", roleList);
			modelMap.put("orgid", orgid);
			return new ModelAndView(urlString,modelMap);
		}
		modelMap = super.getModelMap("AUTH","ROLE");
		
		//获取数据
		List<Sys_role> roleList = roleService.list();
		modelMap.put("roleList", roleList);
		
		return new ModelAndView("/view/auth/role/list",modelMap);
	}
	/**
	 * 打开添加页面
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add() {
		return "/view/auth/role/add";
	}
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public ModelAndView save(Sys_role role,ModelMap modelMap) {
		
		if (role != null ) {
			role.setId(UUID.randomUUID().toString());
		}
		
		role = roleService.insert(role);
		String result = "添加完成。";
		if (role == null ) {
			result = "更新出现错误，请重新尝试，或与管理员联系。";
		}
		modelMap.put("role", role);
		modelMap.put("result", result);
		return new ModelAndView("/view/auth/role/add",modelMap);
	}
	
	/**
	 * 删除
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delete")
	public void delete(String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		int num = roleService.delete(id);
		String result = "failure";
		if (num > 0 ) {
			result = "success";
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
		Sys_role role = roleService.getById(id);
		modelMap.put("role", role);
		return new ModelAndView("/view/auth/role/edit",modelMap);
	}
	
	/**
	 * 执行更新
	 * @param id
	 * @param value
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public ModelAndView update(Sys_role role,ModelMap modelMap) {
		
		int num = roleService.update(role);
		String result = "更新完成。";
		if (num <= 0 ) {
			result = "更新出现错误，请重新尝试，或与管理员联系。";
		}
		modelMap.put("role", role);
		modelMap.put("result", result);
		return new ModelAndView("/view/auth/role/edit",modelMap);
	}
	
	
	/**
	 * 打开角色赋权页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/auth",method=RequestMethod.GET)
	public ModelAndView auth(String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
		//获取功能列表
		List<Sys_function> funList = functionService.list();
		String funString = JSON.toJSONString(funList);
		modelMap.put("funList", funString);
		//获取角色对应的功能对象
		List<Sys_function> authfunList = roleService.searchFunctions(id);
		String authfunString = JSON.toJSONString(authfunList);
		modelMap.put("authfunList", authfunString);
		modelMap.put("roleid", id);
		return new ModelAndView("/view/auth/role/auth",modelMap);
	}
	
	/**
	 * 为角色赋权，赋予功能操作权
	 * @param funListString
	 * @param roleid
	 * @param response
	 * @param modelMap
	 * @throws IOException
	 */
	@RequestMapping(value="/setAuth")
	public void setAuth(String funListString,String roleid,HttpServletResponse response,ModelMap modelMap) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		@SuppressWarnings("unchecked")
		List<String> funList = (List<String>) JSON.parse(funListString);
		
		roleService.setFunctions(funList, roleid);
		String result = "failure";
		result = "success";
		
		out.print(result);
	}
	
	/**
	 * 打开角色与帐户页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/searchAccount",method=RequestMethod.GET)
	public ModelAndView searchAccount(String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
		modelMap = getAccountMap(id,modelMap);

		return new ModelAndView("/view/auth/role/account",modelMap);
	}
	
	private ModelMap getAccountMap(String roleid,ModelMap modelMap) {
		//获取当前角色
		Sys_role role = roleService.getById(roleid);
		modelMap.put("role", role);
		//获取对象
		Sys_account account = new Sys_account();
		account.setRoleid(roleid);
		
		PageBean<Sys_account> pageBean = new PageBean<Sys_account>();
		pageBean.setIsPage(false);
		pageBean.setOrderBy("accountcode");
		pageBean.setOrderType("asc");
		
		List<Sys_account> accounts = accountService.selectByWhere(pageBean,account);
		modelMap.put("accounts", accounts);
		
		return modelMap;
	}
	
	/**
	 * 取消角色与帐户的关联
	 * @param accountid
	 * @param modelMap
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/updateAccount")
	public void updateAccount(String roleid,String accountid,HttpServletResponse response,ModelMap modelMap) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		//判断id是否存在
		if (accountid == null || accountid.equals("")) {
			out.print(result);
		}
		
		//获取帐户
		Sys_account account = accountService.getById(accountid);
		if (account == null) {
			out.print(result);
		}
		
		//去掉关联
		account.setRoleid("");
		int num = accountService.update(account);
		
		if (num > 0 ) {
			result = "success";
		}
		out.print(result);
	}
}
