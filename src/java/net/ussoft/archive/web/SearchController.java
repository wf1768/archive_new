package net.ussoft.archive.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.ussoft.archive.base.BaseConstroller;

/**
 * 检索利用
 * @author guodh
 * 
 * */

@Controller
@RequestMapping(value="search")
public class SearchController extends BaseConstroller{
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView index(ModelMap modelMap){
		modelMap = super.getModelMap("SEARCHMANAGE","");
		return new ModelAndView("/view/search/index",modelMap);
	}
}
