package net.ussoft.archive.web;

import java.awt.image.BufferedImage;
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
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;
import net.ussoft.archive.util.Logger;

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
	
	@Autowired
	private Producer captchaProducer = null;
	
	@RequestMapping(value="/login")
	public String login (ModelMap modelMap) {
		HashMap<String, Object> configMap = getConfig();
		modelMap.put("sysname", configMap.get("SYSNAME"));
		return "login";
	}
	
	@RequestMapping(value="/onlogin",method=RequestMethod.POST)
	public ModelAndView onLogin(Sys_account account,String kaptchafield,HttpServletRequest request,ModelMap modelMap) {
		
		log.debug("进入登录。。");
		HashMap<String, Object> configMap = getConfig();
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
			//用户登录成功，将用户实体存入session
			CommonUtils.setSessionAttribute(request, Constants.user_in_session, res);
			//获取帐户角色能访问的功能
			String roleid = super.getAuthRole(res.getId());
			List<Sys_function> functions = super.getFunctions(roleid);
			modelMap.put("functions", functions);
			//设置哪个功能为焦点功能
			modelMap.put("focus_first", "index");
//			modelMap.put("focus_second", "");
			return new ModelAndView("/view/main", modelMap);
		}else {
			modelMap.put("result", "输入的帐户名 或密码错误，请重新输入。");
			return new ModelAndView("login",modelMap);
		}
	}
	@RequestMapping(value="/main",method=RequestMethod.GET)
	public ModelAndView main() {
		ModelMap modelMap = super.getModelMap("index","");
		return new ModelAndView("/view/main", modelMap);
	}
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request,ModelMap modelMap) {
		request.getSession().removeAttribute(Constants.user_in_session);
		request.getSession().invalidate();
		HashMap<String, Object> configMap = getConfig();
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
