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
 * 系统维护
 * @author wangf
 *
 */

@Controller
@RequestMapping(value="system")
public class SystemController extends BaseConstroller {
	
	
	@Resource
	private IFunctionService functionService;
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView index(ModelMap modelMap) {
		modelMap = super.getModelMap("SYSTEM","");
		return new ModelAndView("/view/system/index",modelMap);
	}
	
}
