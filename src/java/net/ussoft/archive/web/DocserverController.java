package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.service.IDocserverService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 文件服务器
 * @author wangf
 *
 */

@Controller
@RequestMapping(value="docserver")
public class DocserverController extends BaseConstroller {
	
	
	@Resource
	private IDocserverService docserverService;
	
	/**
	 * 系统维护页面打开系统配置列表
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelMap modelMap) {
		modelMap = super.getModelMap("SYSTEM","CONFIG");
		
		//获取数据
		List<Sys_docserver> docserverList = docserverService.list();
		modelMap.put("docserverList", docserverList);
		
		return new ModelAndView("/view/system/docserver/list",modelMap);
	}
	/**
	 * 打开添加服务器页面
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add() {
		return "/view/system/docserver/add";
	}
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public ModelAndView save(Sys_docserver docserver,ModelMap modelMap) {
		
		if (docserver != null ) {
			docserver.setId(UUID.randomUUID().toString());
		}
//		docserver.setServerpath("");
		docserver.setServerstate(0);
		docserver = docserverService.insert(docserver);
		String result = "添加完成。";
		if (docserver == null ) {
			result = "更新出现错误，请重新尝试，或与管理员联系。";
		}
		modelMap.put("docserver", docserver);
		modelMap.put("result", result);
		return new ModelAndView("/view/system/docserver/add",modelMap);
	}
	
	/**
	 * 启用服务器
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/status")
	public void status(String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "failure";
		
		//更新所有服务器的status为0.
		int s_num = docserverService.updateState();
		
		if (s_num <= 0) {
			out.print(result);
			return;
		}
		
		//根据id获取服务器对象
		Sys_docserver docserver = docserverService.selectById(id);
		if (docserver == null) {
			out.print(result);
			return;
		}
		docserver.setServerstate(1);
		int num = docserverService.update(docserver);
		
		if (num > 0 ) {
			result = "success";
		}
		
		out.print(result);
	}
	
	/**
	 * 删除服务器
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delete")
	public void delete(String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		int num = docserverService.delete(id);
		String result = "failure";
		if (num > 0 ) {
			result = "success";
		}
		
		out.print(result);
	}
	
	/**
	 * 打开服务器编辑页面
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
		Sys_docserver docserver = docserverService.selectById(id);
		modelMap.put("docserver", docserver);
		return new ModelAndView("/view/system/docserver/edit",modelMap);
	}
	
	/**
	 * 执行更新
	 * @param id
	 * @param value
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public ModelAndView update(Sys_docserver docserver,ModelMap modelMap) {
		
		int num = docserverService.update(docserver);
		String result = "更新完成。";
		if (num <= 0 ) {
			result = "更新出现错误，请重新尝试，或与管理员联系。";
		}
		modelMap.put("docserver", docserver);
		modelMap.put("result", result);
		return new ModelAndView("/view/system/docserver/edit",modelMap);
	}
}
