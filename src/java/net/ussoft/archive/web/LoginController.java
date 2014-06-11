package net.ussoft.archive.web;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_function;
import net.ussoft.archive.model.Sys_init;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.IInitService;
import net.ussoft.archive.service.IRoleService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;
import net.ussoft.archive.util.EncryptionDecryption;
import net.ussoft.archive.util.Logger;
import net.ussoft.archive.util.SystemTool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.kaptcha.Producer;


@Controller
//@RequestMapping("login")
public class LoginController extends BaseConstroller {
	
	private Logger log = new Logger(LoginController.class);
	
	@Resource
	private IAccountService accountService;
	@Resource
	private IRoleService roleService;
	@Resource
	private IInitService initService;
	
	@Autowired
	private Producer captchaProducer = null;
	
	@RequestMapping(value="/login")
	public String login (ModelMap modelMap) throws Exception {
		
		HashMap<String, Object> configMap = getConfig("SYSTEM");
		modelMap.put("sysname", configMap.get("SYSNAME"));
		
		//系统注册验证
		Boolean regist = regist();
		if (regist) {
			return "login";
		}
		else {
			String mac = getLocalMacAddress();
			String createRegistCode = getRegistCode(mac);
			modelMap.put("registcode", createRegistCode);
			return "regist";
		}
		
	}
	
	//系统注册验证
	public Boolean regist() throws Exception {
		
		Boolean result = false;
		String mac = getLocalMacAddress();
		
		String tmp = getRegistCode(mac);
		
		EncryptionDecryption des = new EncryptionDecryption();
		String createRegistCode = des.encrypt(tmp);
		
		//检查系统注册，如果注册不对，转到注册页.
		Sys_init init = new Sys_init();
		init.setInitkey("registcode");
		init = initService.selectByWhere(init);
		
		if (null != init) {
			String registcode = init.getInitvalue();
			
			//如果注册码为空
			if (!init.getInitvalue().equals("") && createRegistCode.equals(registcode)) {
				result = true;
			}
		}
				
		return result;
	}
	//获取本机mac地址
	public String getLocalMacAddress() throws Exception {
		String os = SystemTool.getOSName();
		
		String macAddress = "";
		if (os.startsWith("windows")) {
//			//获取windows的ip对象
//			InetAddress idAddress = SystemTool.getInetAddress();
//			macAddress = SystemTool.getMACAddress(idAddress);
			macAddress = SystemTool.getWindowsMACAddress();
			
		} else if (os.startsWith("linux")) {
//			//获取linux的ip对象
//			InetAddress idAddress = SystemTool.getInetAddressOnLinux();
//			macAddress = SystemTool.getMACAddress(idAddress);
			
			macAddress = SystemTool.getLinuxMACAddress();
		} else {
//			//如果是mac os
//			InetAddress idAddress = SystemTool.getInetAddress();
//			macAddress = SystemTool.getMACAddress(idAddress);
			
			macAddress = SystemTool.getMacosMACAddress();
			
		}
		return macAddress;
	}
	
	//生成本机注册码
	public String getRegistCode(String str) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("regist");
		
		String registCode = des.encrypt(str);
		return registCode;
//        System.out.println("加密前的字符：" + test);
//        System.out.println("加密后的字符：" + des.encrypt(test));
//        System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));

	}
	@RequestMapping(value="/onRegist", method=RequestMethod.POST)
	public String onRegist(String initvalue,String id,HttpServletResponse resp,ModelMap modelMap) throws Exception {
		//获取对象
		Sys_init init = initService.selectById(id);
		init.setInitvalue(initvalue);
		int num = initService.update(init);
		if (num >0) {
			//modelMap.put("result", "修改成功");
		}
		else {
			//modelMap.put("result", "修改时发生问题，请重新尝试或与管理员联系。");
		}
		List<Sys_init> initList = initService.list();
		for (Sys_init sys_init : initList) {
			modelMap.put(sys_init.getInitkey(), sys_init);
		}
		
		//系统注册验证
		Boolean regist = regist();
		if (regist) {
			return "login";
		}
		else {
			String mac = getLocalMacAddress();
			String createRegistCode = getRegistCode(mac);
			modelMap.put("registcode", createRegistCode);
			return "regist";
		}
				
	}
	
	@RequestMapping(value="/onlogin",method=RequestMethod.POST)
	public ModelAndView onLogin(Sys_account account,String kaptchafield,HttpServletRequest request,ModelMap modelMap) {
		
		log.debug("进入登录。。");
		HashMap<String, Object> configMap = getConfig("SYSTEM");
		modelMap.put("sysname", configMap.get("SYSNAME"));
		
		//判断验证码
//		String kaptchaCode = (String)session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		String kaptchaCode = (String) CommonUtils.getSessionAttribute(request, com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		if (kaptchaCode == null || !kaptchaCode.equals(kaptchafield)) {
			modelMap.put("result", "输入的验证码错误，请重新输入。");
			return new ModelAndView("login",modelMap);
		}
		
		//登录时先判断session里是否有该账户,防止同一台机器有2个session登录
//		Sys_account accountSession = (Sys_account) CommonUtils.getSessionAttribute(request, Constants.user_in_session);
		Sys_account accountSession = super.getSessionAccount();
		if (accountSession != null){
			CommonUtils.removeSessionAttribute(request, Constants.user_in_session);
//			request.getSession().removeAttribute(Constants.user_in_session);
		}
		
		if(!CommonUtils.isNotNullAndEmpty(account.getAccountcode(),account.getPassword())){
			//帐号或密码为""或NULL
			modelMap.put("result", "输入的帐户名 或密码错误，请重新输入。");
			return new ModelAndView("login",modelMap);
		}
		
		
		Sys_account res = accountService.login(account);
		
		if (null != res) {
			//这里要判断帐户的状态，如果为不启用，就拒绝访问
			if (res.getAccountstate() == 0) {
				modelMap.put("result", "您的帐户已被禁用，请与管理员联系。");
				return new ModelAndView("login",modelMap);
			}
		
			//用户登录成功，将用户实体存入session
			CommonUtils.setSessionAttribute(request, Constants.user_in_session, res);
			//获取帐户角色能访问的功能
			String roleid = super.getAuthRole(res.getId());
			List<Sys_function> functions = super.getFunctions(roleid);
			modelMap.put("functions", functions);
			//设置哪个功能为焦点功能
			modelMap.put("focus_first", "index");
//			modelMap.put("focus_second", "");
			modelMap.put("CONFIG", main_fun("2"));
			modelMap.put("ACCOUNT", main_fun("18"));
			modelMap.put("ARCHIVE", main_fun("6"));
			modelMap.put("SEARCH", main_fun("10"));
			modelMap.put("SEARCHFILE", main_fun("12"));
			return new ModelAndView("/view/main", modelMap);
		}else {
			modelMap.put("result", "输入的帐户名 或密码错误，请重新输入。");
			return new ModelAndView("login",modelMap);
		}
	}
	/**
	 * 根据functionid 获取当前帐户对功能是否有权限。主要用于主页的快捷方式显示
	 * @param funid
	 * @return
	 */
	private Boolean main_fun(String funid) {
		return roleService.getRoleFun(funid);
	}
	
	@RequestMapping(value="/main",method=RequestMethod.GET)
	public ModelAndView main() {
		ModelMap modelMap = super.getModelMap("index","");
		modelMap.put("CONFIG", main_fun("2"));
		modelMap.put("ACCOUNT", main_fun("18"));
		modelMap.put("ARCHIVE", main_fun("6"));
		modelMap.put("SEARCH", main_fun("10"));
		modelMap.put("SEARCHFILE", main_fun("12"));
		
		return new ModelAndView("/view/main", modelMap);
	}
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request,ModelMap modelMap) {
		request.getSession().removeAttribute(Constants.user_in_session);
		request.getSession().invalidate();
		HashMap<String, Object> configMap = getConfig("SYSTEM");
		modelMap.put("sysname", configMap.get("SYSNAME"));
		return "login";
	}
	
	@RequestMapping("/kaptcha")  
	public void initCaptcha(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");
		String capText = captchaProducer.createText();
		request.getSession().setAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY, capText);  
		BufferedImage bi = captchaProducer.createImage(capText);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(bi, "jpg", out);

		try{
			out.flush();
			}finally{
				out.close();
			}
		}
}
