package net.ussoft.archive.web;

import javax.annotation.Resource;

import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.model.Sys_init;
import net.ussoft.archive.service.IConfigService;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IInitService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PageController {
	
	@Resource
	private IInitService initService;
	@Resource
	private IConfigService configService;
	@Resource
	private IEncryService encryService;
	
	@RequestMapping("/index.jsp")
    public String index(ModelMap modelMap) throws Exception {
		//获取当前系统类型。
		Sys_init tmpInit = new Sys_init();
		tmpInit.setInitkey("systemtype");
		Sys_init init = initService.selectByWhere(tmpInit);
		if (null != init) {
			//判断是否包含前端页面
			if (encryService.getInit(0)) {
				return "/page/info";
			}
		}
		Sys_config config = new Sys_config();
		config.setConfigkey("SYSNAME");
		config = configService.selectByWhere(config);
		modelMap.put("sysname", config.getConfigvalue());
		return "login";
    }
	@RequestMapping("/page/content")
	public String content() {
		return "/page/content";
	}

}
