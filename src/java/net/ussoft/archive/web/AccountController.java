package net.ussoft.archive.web;

import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.MD5;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="account")
public class AccountController {
	
	@Resource
	private IAccountService accountService;
	@Resource
	private ITreeService treeService;
	@Resource
	private IOrgService orgService;
	
	
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView edit(String id,ModelMap modelMap) {
		Sys_account account = accountService.getById(id);
		modelMap.put("account", account);
		
		return new ModelAndView("/view/user/edit", modelMap);
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public String update( Sys_account account,ModelMap modelMap) {
		String pass = account.getPassword();
		if (CommonUtils.isNotNullAndEmpty(pass)) {
			pass = MD5.encode(pass);
			account.setPassword(pass);
			accountService.update(account);
		}
		return "redirect:/user/list.do";
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.GET)
	public String delete(String id,Integer page) {
		accountService.delete(id);
		return "redirect:/user/list.do?page=" + page;
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(String treeid,Integer page) {
		
		if (null == page) {
			page = 1;
		}
		String orgid = treeid;
		if (null == treeid || treeid.equals("")) {
			orgid = null;
		}
		else if (treeid.equals("1")) {
			//如果是根节点，显示全部帐户
			orgid = null;
		}
		
		PageBean<Sys_account> p = accountService.list(orgid,page);
		ModelMap modelMap = new ModelMap();
		modelMap.put("pagebean", p);
		modelMap.put("treeid", treeid);
		
		return new ModelAndView("/view/user/list", modelMap);
	}
	
	@RequestMapping(value="/toinsert",method=RequestMethod.POST)
	public ModelAndView insert(Sys_account account,ModelMap modelMap) {
		
		account.setId(UUID.randomUUID().toString());
		account.setPassword(MD5.encode(account.getPassword()));
		
		accountService.insertOne(account);
		
		modelMap.put("account", account);
        return new ModelAndView("/view/user/show", modelMap);
	}
	
	@RequestMapping(value="/show",method=RequestMethod.GET)
	public ModelAndView show(String id,ModelMap modelMap) {
		Sys_account account = accountService.getById(id);
		modelMap.put("row",account);
		return new ModelAndView("/account/account_show", modelMap);
	}

	
	
}
