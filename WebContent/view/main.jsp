<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/public.js"></script>
<script type="text/javascript">
function callback() {
}
</script>
<title>${sysname }</title>
<!--[if IE 6]>
<script src="${pageContext.request.contextPath}/js/DD_belatedPNG.js"></script>
<script>
  DD_belatedPNG.fix('.login_bottom,.logo,.l_m_b, tubiao, tubiao1,');
</script>
<![endif]-->
</head>
<body>
<%@ include file="/view/common/top_menu.jsp"%>

	<div id="bodyer2">
        <div id="icon">
              <ul>
                 <li><img src="images/icon_01.png" width="125" height="118"/></li>
                 <li><img src="images/icon_02.png" width="125" height="118"/></li>
                 <li><img src="images/icon_03.png" width="125" height="118"/></li>
                 <li><img src="images/icon_04.png" width="125" height="118"/></li>
                 <li><img src="images/icon_05.png" width="125" height="118"/></li>
                 <li><img src="images/icon_06.png" width="125" height="118"/></li>
              </ul>
        </div>
    </div>
<%-- <div class="container ">
	<div class="row">
		<div class="span12">
			你好:<%=request.getAttribute("username")%>，密码：<%=request.getAttribute("pass") %>
	
			${sessionScope.CURRENT_USER_IN_SESSION.accountcode } <a
				href="${pageContext.request.contextPath}/account/list.do ">帐户管理</a>
		</div>
	</div>
</div> --%>


    </body>
</html>