package net.ussoft.archive.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
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
		
		String version = "no";
		
		//判断版本,是否是集团版本
		if (encryService.getInit(4)) {
			version = "group";
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
			
//			PageBean<Sys_org> pageBean = new PageBean<Sys_org>();
			List<HashMap<String, String>> childList = new ArrayList<HashMap<String,String>>();
			//获取数据
			if (null == orgid || orgid.equals("")) {
				
			}
			else {
				//获取当前帐户作为owner的组对象
				List<Sys_org> ownerList = orgService.getorgowner(account.getId());
				//获取当前组的treenode，来判断，点击的组是否应该显示子组。例如如果当前帐户作为三级树的owner，为了画树，它的父节点也都画出来了，点击父节点
				//就不应该显示子节点,因为当前帐户不是父节点的owner
				Sys_org tmpOrg = orgService.getById(orgid);
				
				//判断前台选择的组是否在当前帐户的owner范围内
				Boolean isowner = false;
				for (Sys_org sys_org : ownerList) {
					String orgTreeNodeString = sys_org.getTreenode();
					if (orgTreeNodeString.equals(tmpOrg.getTreenode()) || !orgTreeNodeString.contains(tmpOrg.getTreenode())) {
						isowner = true;
						break;
					}
				}
				if (isowner) {
					childList = orgService.getChildList(orgid);
				}
				
			}
			
//			modelMap.put("childList", pageBean.getList());
			modelMap.put("childList", childList);
			
			modelMap.put("version", version);
			
			modelMap.put("orgid", orgid);
			return new ModelAndView(urlString,modelMap);
		}
		
		//如果是非集团版本(网络、单机)
		//获取数据
		modelMap = super.getModelMap("AUTH","ORG");
		List<Sys_org> orgList = orgService.list();
		String orgListString = JSON.toJSONString(orgList);
		modelMap.put("orgList", orgListString);
		List<HashMap<String, String>> childList = new ArrayList<HashMap<String,String>>();
		if (null != orgid && !orgid.equals("")) {
			childList = orgService.getChildList(orgid);
		}
		modelMap.put("orgid", orgid);
		modelMap.put("childList", childList);
		
		modelMap.put("version", version);
		
		return new ModelAndView("/view/auth/org/list",modelMap);
	}
	
	/**
	 * 打开添加页面
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(String parentid,ModelMap modelMap) {
		modelMap.put("parentid", parentid);
		return "/view/group/org/add";
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(Sys_org org,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (org == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		org.setId(UUID.randomUUID().toString());
		org = orgService.insert(org);
		
		if (org == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		//重新获取org实体，因为自增长的orgindex没有获得。
		org = orgService.getById(org.getId());
		//更新treenode
		//获取父节点的treenode
		Sys_org parOrg = orgService.getById(org.getParentid());
		if (parOrg != null) {
			String treenodeString = parOrg.getTreenode() + "#" + org.getOrgindex();
			org.setTreenode(treenodeString);
			orgService.update(org);
		}
		out.print(result);
	}
	
	/**
	 * 打开编辑页面
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
		Sys_org org = orgService.getById(id);
		modelMap.put("org", org);
		return new ModelAndView("/view/group/org/edit",modelMap);
	}
	
	/**
	 * 执行更新
	 * @param id
	 * @param value
	 * @param modelMap
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public void update(Sys_org org,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (org == null ) {
			result = "failure";
			out.print(result);
			return;
		}
		int num = orgService.update(org);
		if (num <= 0 ) {
			result = "failure";
		}
		out.print(result);
	}
	
	/**
	 * 打开移动组页面
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/move",method=RequestMethod.GET)
	public ModelAndView move(String id,ModelMap modelMap) {
		Sys_account account = super.getSessionAccount();
		List<Sys_org> orgList = orgService.orgownerList(account.getId());
		
		String orgListString = JSON.toJSONString(orgList);
		modelMap.put("orgList", orgListString);
		
		Sys_org org = orgService.getById(id);
		
		modelMap.put("org", org);
		return new ModelAndView("/view/group/org/move",modelMap);
	}
	/**
	 * 保存移动组
	 * @param id
	 * @param targetid
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping(value="/movesave",method=RequestMethod.POST)
	public void movesave(String id,String targetid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (id == null || id.equals("") || targetid == null || targetid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.move(id, targetid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 集团版打开设置组的管理者页面
	 * @param orgid
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/setowner",method=RequestMethod.GET)
	public ModelAndView setowner(String orgid,String selectOrgid,ModelMap modelMap) {
		//获取选中组的所有者,供页面实现和删除
		List<Sys_account> owners = orgService.getowner(orgid);
		modelMap.put("owners", owners);
		//获取当前帐户作为owner的组及子组.供页面实现组树，点击时，显示组下的帐户
		Sys_account account = super.getSessionAccount();
		List<Sys_org> orgList = orgService.orgownerList(account.getId());
		
		String orgListString = JSON.toJSONString(orgList);
		modelMap.put("orgList", orgListString);
		
		Sys_org org = orgService.getById(orgid);
		modelMap.put("org", org);
		
		
		//页面上点击了组节点，获取该组下所有帐户数据，供选择为owner
		if (null != selectOrgid && !selectOrgid.equals("")) {
			List<Sys_account> accounts = orgService.getAccounts(selectOrgid);
			modelMap.put("accounts", accounts);
			modelMap.put("selectOrgid", selectOrgid);
		}
		return new ModelAndView("/view/group/org/setowner",modelMap);
	}
	
	/**
	 * 移除组的所有者
	 * @param orgid
	 * @param accountid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/removeowner",method=RequestMethod.POST)
	public void removeowner(String orgid,String accountid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") || accountid == null || accountid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.removeowner(orgid, accountid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	@RequestMapping(value="/saveowner",method=RequestMethod.POST)
	public void saveowner(String orgid,String accountid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") || accountid == null || accountid.equals("")) {
			result = "failure";
			out.print(result);
			return;
		}
		
		Boolean b = orgService.setowner(orgid, accountid);
		
		if (!b) {
			result = "failure";
		}
		
		out.print(result);
	}
	
	/**
	 * 删除组。
	 * @param orgid
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void delete(String orgid,HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (orgid == null || orgid.equals("") ) {
			result = "failure";
			out.print(result);
			return;
		}
		
		int num = orgService.delete(orgid);
		
		
		if (num <= 0) {
			result = "failure";
		}
		
		out.print(result);
	}

}
