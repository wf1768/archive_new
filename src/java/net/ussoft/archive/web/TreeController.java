package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.service.ITreeService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping(value="tree")
public class TreeController extends BaseConstroller {
	
	@Resource
	private ITreeService treeService;
	
	/**
	 * 根据treeid，获取tree对应的templet模版的实体
	 * @param treeid
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/getTempletType",method=RequestMethod.POST)
	public void getTempletType(String treeid,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (treeid == null || treeid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Sys_templet templet = treeService.getTemplet(treeid);
		
		if (null == templet) {
			result = "failure";
		}
		
		result = JSON.toJSONString(templet);
		
		out.print(result);
	}
	
	
}
