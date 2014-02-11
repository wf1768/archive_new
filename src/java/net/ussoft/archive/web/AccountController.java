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
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.MD5;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping(value="account")
public class AccountController extends BaseConstroller {
	
	@Resource
	private IAccountService accountService;
	@Resource
	private ITreeService treeService;
	@Resource
	private IOrgService orgService;
	@Resource
	private IEncryService encryService;
	
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
		if (id == null || id.equals("") || targetid == null || targetid.equals("")) {
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
			
			List<Sys_account> accounts = new ArrayList<Sys_account>();
			//获取选择的组下的帐户
			if (null != orgid && !orgid.equals("")) {
				accounts = accountService.list(orgid);
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
	
}
