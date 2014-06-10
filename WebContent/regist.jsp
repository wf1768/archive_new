<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="css/common.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<title>${sysname }</title>
<!--[if IE 6]>
<script src="js/DD_belatedPNG.js"></script>
<script>
  DD_belatedPNG.fix('.login_bottom,.logo,.l_m_b,');
</script>
<![endif]-->
 
</head>
<body>
	<div id="bodyer1">
    	<div id="con1">
        	<div id="login">
            	<div class="login_top">
                	
                </div>
                <div class="login_middle">
                	<form action="onRegist.do" method="post">
						<table>
							<tr>
								<td style="width: 70px" colspan="3">系统需要注册，请与开发商联系。</td>
							</tr>
							<tr>
								<td style="width: 70px" colspan="3">机器码: ${registcode }</td>
							</tr>
							<tr>
								<td style="width: 70px">注册码:</td>
								<td><input type="text" style="width: 300px" name="initvalue"  value=""/><input type="hidden" name="id" value="6"></td>
								<td><input type="submit" value="提交"></td>
							</tr>
						</table>
					</form>
                </div>
            </div>
        </div>
    </div>
    <div style="clear:both"></div>

</body>
</html>