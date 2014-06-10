package net.ussoft.archive.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.Constants;
import net.ussoft.archive.util.Logger;
  
/** 
 * User: springMVC拦截器 判断session中用户是否过期
 * Date: 13-10-27
 * Time: 下午7:31 
 * @author wangf
 */
  
public class SystemFilter implements Filter {
	
	private Logger log = new Logger(SystemFilter.class);
  
  
    @Override  
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {  
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {  
            throw new ServletException("OncePerRequestFilter just supports HTTP requests");  
        }
        log.debug("session filter 启动.");
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;  
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//        HttpSession session = httpRequest.getSession(true);

        String path = httpRequest.getContextPath();
    	String basePath = httpRequest.getScheme() + "://"
    			+ httpRequest.getServerName() + ":" + httpRequest.getServerPort()
    			+ path + "/";
    	
        StringBuffer url = httpRequest.getRequestURL();
        log.debug(url);
        
//        if (url.indexOf("kaptcha") != -1 || url.indexOf("init") != -1 || url.indexOf("page") != -1 || url.indexOf("login") != -1 || url.toString().equals(basePath)) {
//        	filterChain.doFilter(servletRequest, servletResponse);
//        	return;
//        }
//        
//        String servletPath = httpRequest.getServletPath();
//        if (servletPath.indexOf("css") >=0 ||  servletPath.indexOf("js") >=0 || servletPath.indexOf("image") >=0){
//        	filterChain.doFilter(servletRequest, servletResponse);
//        	return;
//        }
        
//        String[] strs = ProsReader.getString("INDICATION_APP_NAME").split("\\|");
        String[] strs = {"kaptcha","init","page","login","css","js","image","onRegist"};
        if (strs != null && strs.length > 0) {  
            for (String str : strs) {
                if (url.indexOf(str) >= 0) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }  
            }  
        }
        
        if (url.toString().equals(basePath)) {
        	filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        
        
//        Object object = session.getAttribute(Constants.user_in_session);
        Object object = CommonUtils.getSessionAttribute(httpRequest, Constants.user_in_session);
        Sys_account account = object == null ? null : (Sys_account) object;  
        if (account == null) {
            boolean isAjaxRequest = isAjaxRequest(httpRequest);
            if (isAjaxRequest) {
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(),"您已经太长时间没有操作,请刷新页面");  
            }
            httpResponse.sendRedirect(path + "/login.do");
            return;  
        }
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }  
  
    /** 
     * 判断是否为Ajax请求 
     * 
     * @param request HttpServletRequest 
     * @return 是true, 否false 
     */  
    public static boolean isAjaxRequest(HttpServletRequest request) {  
//        return request.getRequestURI().startsWith("/api");
        String requestType = request.getHeader("X-Requested-With");  
        return requestType != null && requestType.equals("XMLHttpRequest");  
    }  
  
    @Override  
    public void init(FilterConfig filterConfig) throws ServletException {  
           
    }  
  
    @Override  
    public void destroy() {  
        //To change body of implemented methods use File | Settings | File Templates.  
    }  
  
  
}  