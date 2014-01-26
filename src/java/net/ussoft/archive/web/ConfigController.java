package net.ussoft.archive.web;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.service.IConfigService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 参数设置
 * @author wangf
 *
 */

@Controller
@RequestMapping(value="config")
public class ConfigController extends BaseConstroller {
	
	
	@Resource
	private IConfigService configService;
	
	/**
	 * 系统维护页面打开系统配置列表
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelMap modelMap) {
		modelMap = super.getModelMap("SYSTEM","CONFIG");
		
		//获取config数据
		List<Sys_config> configList = configService.list();
		modelMap.put("configList", configList);
		
		return new ModelAndView("/view/system/config/list",modelMap);
	}
	
	/**
	 * 打开系统配置编辑页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView edit(String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			
		}
		//获取对象
		Sys_config config = configService.selectById(id);
		modelMap.put("config", config);
		return new ModelAndView("/view/system/config/edit",modelMap);
	}
	
	/**
	 * 执行更新系统配置
	 * @param id
	 * @param value
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public ModelAndView update(Sys_config config,ModelMap modelMap) {
		//获取对象
		String value = config.getConfigvalue();
		config = configService.selectById(config.getId());
		config.setConfigvalue(value);
		
		int num = configService.update(config);
		String result = "更新完成。";
		if (num <= 0 ) {
			result = "更新出现错误，请重新尝试，或与管理员联系。";
		}
		modelMap.put("config", config);
		modelMap.put("result", result);
		return new ModelAndView("/view/system/config/edit",modelMap);
	}
}
