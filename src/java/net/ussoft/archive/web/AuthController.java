package net.ussoft.archive.web;

import javax.annotation.Resource;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.service.IFunctionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 权限管理
 * @author wangf
 *
 */

@Controller
@RequestMapping(value="auth")
public class AuthController extends BaseConstroller {
	
	
	@Resource
	private IFunctionService functionService;
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView index(ModelMap modelMap) {
		modelMap = super.getModelMap("AUTH","");
		
		return new ModelAndView("/view/auth/index",modelMap);
	}
	
}
