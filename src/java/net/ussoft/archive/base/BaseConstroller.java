package net.ussoft.archive.base;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.model.Sys_function;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IConfigService;
import net.ussoft.archive.service.IFunctionService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.IRoleService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

/**
 * 控制器父类
 * 一些系统通用的操作
 * @author wangf
 *
 */
public class BaseConstroller {
	
	
	@Resource
	private IConfigService configService;
	@Resource
	private IRoleService roleService;
	@Resource
	private IAccountService accountService;
	@Resource
	private IOrgService orgService;
	@Resource
	private IFunctionService functionService;
	
	@Autowired  
	private  HttpServletRequest request; 
	
	/**
	 * 获取session里的帐户实体类
	 * @return
	 */
	public Sys_account getSessionAccount() {
		Sys_account accountSession = (Sys_account) CommonUtils.getSessionAttribute(request, Constants.user_in_session);
		return accountSession;
	}
	
	/**
	 * 获取系统配置表（sys_config）内容，形成map类型。供子类
	 */
	public HashMap<String, Object> getConfig() {
		List<Sys_config> configList = configService.list();
		
		HashMap<String, Object> configMap = new HashMap<String, Object>();
		for (Sys_config config : configList) {
			configMap.put(config.getConfigkey(), config.getConfigvalue());
		}
		return configMap;
	}
	
	/**
	 * 获取当前session帐户能管理的功能列表。每个模块要画菜单用
	 * @return
	 */
	public ModelMap getModelMap(String focus_first,String focus_second) {
		ModelMap modelMap = new ModelMap();
		//获取session帐户
		Sys_account account = getSessionAccount();
		//获取帐户本身或者所属组的角色
		String roleid = getAuthRole(account.getId());
		//获取帐户角色能访问的功能
		List<Sys_function> functions = getFunctions(roleid);
		modelMap.put("functions", functions);
		
		HashMap<String, Object> configMap = getConfig();
		modelMap.put("sysname", configMap.get("SYSNAME"));
		
		
		//设置哪个功能为焦点功能
		modelMap.put("focus_first", focus_first);
		modelMap.put("focus_second", focus_second);
		
		//获取当前一级节点功能id，用来显示二级菜单
		Sys_function function = new Sys_function();
		function.setFunenglishname(focus_first);
		function = functionService.getFunction(function);
		modelMap.put("focus_first_id", function.getId());
		
		return modelMap;
	}
	/**
	 * 获取当前帐户的角色，如果帐户没有角色，获取所属组的角色
	 * @param accountid		帐户id
	 * @return
	 */
	public String getAuthRole(String accountid) {
		//获取帐户对象
		Sys_account account = accountService.getById(accountid);
		String roleid = account.getRoleid();
		//如果帐户自己的角色为空
		if (roleid == null || roleid.equals("")) {
			//获取帐户所属组的角色
			Sys_org org = orgService.getById(account.getOrgid());
			roleid = org.getRoleid();
		}
		//如果还是为空，返回null
//		if (roleid == null || roleid.equals("")) {
//			return null;
//		}
		
		return roleid;
	}
	
	/**
	 * 根据角色id，获取角色能控制的功能list
	 * @param roleid
	 * @return
	 */
	public List<Sys_function> getFunctions(String roleid) {
		return roleService.searchFunctions(roleid);
	}
	
}
