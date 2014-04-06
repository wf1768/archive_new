package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_config;
import net.ussoft.archive.service.ICodeService;
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
	@Resource
	private ICodeService codeService;
	
	/**
	 * 系统维护页面打开系统配置列表
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelMap modelMap) {
		modelMap = super.getModelMap("SYSTEM","CONFIG");
		
		//获取config数据
		List<Sys_config> configList = configService.list("SYSTEM");
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
			return null;
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
	
	@RequestMapping(value="/updateSetShow",method=RequestMethod.POST)
	public void updateSetShow(Sys_config config,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		if (config == null ) {
			out.print(result);
			return;
		}
		int num = configService.update(config);
		if (num > 0 ) {
			result = "success";
		}
		out.print(result);
	}
	
	/**
	 * 打开电子文件权限代码设置页
	 * @return
	 */
	@RequestMapping(value="/docauthlist",method=RequestMethod.GET)
	public ModelAndView docauthlist() {
			
		ModelMap modelMap = super.getModelMap("SYSTEM","CONFIG");
		
		//获取电子全文代码
		Sys_code code = new Sys_code();
		code.setTempletfieldid("DOCAUTH");
		List<Sys_code> codes = codeService.selectByWhere(code);
		modelMap.put("docauth", codes);
		
		return new ModelAndView("/view/system/config/docauthlist",modelMap);
	}
	/**
	 * 打开电子文件权限代码添加页
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/docauthadd",method=RequestMethod.GET)
	public String docauthadd(ModelMap modelMap) {
		return "/view/system/config/docauthadd";
	}
	/**
	 * 保存新代码
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping(value="/savedocauth",method=RequestMethod.POST)
	public void savedocauth(Sys_code code,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (code == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		code.setId(UUID.randomUUID().toString());
		code.setTempletfieldid("DOCAUTH");
		code.setCodeorder(1);
		code = codeService.insert(code);
		
		if (code == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		out.print(result);
	}
	/**
	 * 打开更新电子文件浏览权限代码
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/docauthedit",method=RequestMethod.GET)
	public ModelAndView docauthedit(String id,ModelMap modelMap) {
		//判断id是否存在
		if (id == null || id.equals("")) {
			return null;
		}
		//获取对象
		Sys_code docauth = codeService.selectById(id);
		modelMap.put("docauth", docauth);
		return new ModelAndView("/view/system/config/docauthedit",modelMap);
	}
	/**
	 * 执行更新电子文件浏览权限代码
	 * @param code
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/updatedocauth",method=RequestMethod.POST)
	public void updatedocauth(Sys_code code,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (code == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		
		int num = codeService.update(code);
		
		if (num <= 0 ) {
			result = "failure";
			out.print(result);
			return;
		}
		out.print(result);
	}
	/**
	 * 删除电子文件浏览权限代码
	 * @param orgid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/docauthdelete",method=RequestMethod.POST)
	public void docauthdelete(String id,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("") ) {
			result = "failure";
			out.print(result);
			return;
		}
		
		int num = configService.deleteDocAuth(id);
		
		if (num <= 0) {
			result = "failure";
		}
		
		out.print(result);
	}
}
