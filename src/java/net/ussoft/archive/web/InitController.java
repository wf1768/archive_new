package net.ussoft.archive.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.model.Sys_init;
import net.ussoft.archive.service.IInitService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value="init")
public class InitController {
	
	@Resource
	private IInitService initService;
	
	@RequestMapping(value="/login")
	public String login () {
		return "/init/login";
	}
	
	@RequestMapping(value="/onlogin",method=RequestMethod.POST)
	public ModelAndView onlogin(HttpServletRequest request,ModelMap modelMap) {
		
		String username = request.getParameter("username");
		String pass = request.getParameter("password");
		
		
		Date tmp = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(tmp);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		tmp = calendar.getTime();
		
		SimpleDateFormat df3 = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		String pass2 = df3.format(tmp);
		
		if (username.equals("admin") && pass.equals(pass2)) {
			List<Sys_init> initList = initService.list();
			for (Sys_init sys_init : initList) {
				modelMap.put(sys_init.getInitkey(), sys_init);
			}
			return new ModelAndView("/init/init", modelMap);
		}
		else {
			modelMap.put("result", "输入的账户名 或密码错误，请重新输入。");
			return new ModelAndView("/init/login",modelMap);
		}
	}
	
	/**
	 * 2种方式返回提示。update方法返回result，前台通过javascript获取result，alert提示。问题就是刷新时，会继续提示。
	 * update1方式，返回result，页面直接显示在页面上。
	 * 
	 * 选一种方式，删除另一种
	 * 
	 * @param initvalue
	 * @param id
	 * @param resp
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(String initvalue,String id,HttpServletResponse resp,ModelMap modelMap) {
		//获取对象
		Sys_init init = initService.selectById(id);
		init.setInitvalue(initvalue);
		int num = initService.update(init);
		if (num >0) {
			modelMap.put("result", "修改成功"); 
		}
		else {
			modelMap.put("result", "修改时发生问题，请重新尝试或与管理员联系。");
		}
		List<Sys_init> initList = initService.list();
		for (Sys_init sys_init : initList) {
			modelMap.put(sys_init.getInitkey(), sys_init);
		}
		return "/init/init";
	}

	@RequestMapping(value="/update1", method=RequestMethod.POST)
	public String update1(String initvalue,String id,HttpServletResponse resp,ModelMap modelMap) throws IOException {
//		resp.getWriter().print("<script>alert('添加失败，工号已经存在！');</script>");
		//获取对象
		Sys_init init = initService.selectById(id);
		init.setInitvalue(initvalue);
		int num = initService.update(init);
		if (num >0) {
			modelMap.put(init.getInitkey()+"_result", "修改成功"); 
		}
		else {
			modelMap.put(init.getInitkey()+"_result", "修改时发生问题，请重新尝试或与管理员联系。"); 
		}
		List<Sys_init> initList = initService.list();
		for (Sys_init sys_init : initList) {
			modelMap.put(sys_init.getInitkey(), sys_init);
		}
		
		return "/init/init";
		
		
	}
}
