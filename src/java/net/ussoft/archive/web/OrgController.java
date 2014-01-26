package net.ussoft.archive.web;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_org;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IOrgService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping(value="org")
public class OrgController extends BaseConstroller {
	
	@Resource
	private IOrgService orgService;
	@Resource
	private IEncryService encryService;
	
	
	
	/**
	 * 组列表
	 * @param modelMap
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(String orgid,String type,ModelMap modelMap) throws Exception {
		
		
		//判断版本,是否是集团版本
		if (encryService.getInit(4)) {
			String urlString = "";
			if (type != null && type.equals("group")) {
				modelMap = super.getModelMap("GROUP","GROUPORG");
				urlString = "/view/group/org/list";
			}
			else {
				modelMap = super.getModelMap("AUTH","ORG");
				urlString = "/view/auth/org/list";
			}
			
			//获取组
//			List<Sys_org> orgList = orgService.list();
			//得到当前帐户为所有者的org节点
			Sys_account account = super.getSessionAccount();
			
			List<Sys_org> orgList = orgService.orgownerList(account.getId());
			
			
			String orgListString = JSON.toJSONString(orgList);
			modelMap.put("orgList", orgListString);
			
			PageBean<Sys_org> pageBean = new PageBean<Sys_org>();
			//获取数据
			if (null == orgid || orgid.equals("")) {
				
			}
			else {
				Sys_org org = new Sys_org();
				if (orgid != null) {
					org.setParentid(orgid);
				}
				
				pageBean.setIsPage(false);
				pageBean.setOrderBy("CONVERT(orgname USING gbk)");
				pageBean = orgService.list(org,pageBean);
			}
			
			modelMap.put("childList", pageBean.getList());
			modelMap.put("orgid", orgid);
			return new ModelAndView(urlString,modelMap);
		}
		
		//获取数据
		List<Sys_org> orgList = orgService.list();
		modelMap.put("orgList", orgList);
		
		return new ModelAndView("/view/auth/org/list",modelMap);
	}

}
