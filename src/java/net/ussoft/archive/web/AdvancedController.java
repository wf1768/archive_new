package net.ussoft.archive.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.ussoft.archive.base.BaseConstroller;

/**
 * 高级检索
 * @author guodh
 * 
 * */

@Controller
@RequestMapping(value="advanced")
public class AdvancedController extends BaseConstroller{
	
	/**
	 * 高级检索-检索结果集
	 * @param modelMap
	 * @return
	 * */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","SEARCHADVANCED");
		
		modelMap.put("pageName", "高级检索");
		return new ModelAndView("/view/search/advanced/list",modelMap);
	}
}
