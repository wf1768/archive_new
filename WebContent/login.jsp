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
<script type="text/javascript">
	$(document).ready(
		function (){
		  $('#kaptcha').click(
			  function (){
				$(this).attr('src', 'kaptcha.do?' + Math.floor(Math.random() * 100));  
			  }
		  );
		}
	);
</script>
 
</head>
<body>
	<div id="bodyer1">
    	<div id="con1">
        	<div id="login">
            	<div class="login_top">
                	
                </div>
                <div class="login_middle">
                	<div class="l_m_t">
                    	<img src="images/logo_03.png" width="44" height="35" />
                    	<span class="logo1">${sysname }</span>
                    </div>	
                    <div class="l_m_m">
                        <form action="onlogin.do" method="post">
                        	<table>
                        		<tr height="47px">
                        			<td>用户名:</td>
                        			<td colspan="2"><input type="text" value="admin" name="accountcode" id="" class="inp"/></td>
                        		</tr>
                        		<tr height="40px">
                        			<td>密码:</td>
                        			<td colspan="2"><input type="password" value="password" name="password" id="" class="inp"/></td>
                        		</tr>
                        		<tr height="40px">
                        			<td>验证码:</td>
                        			<td><input type="text" style="width: 130px;" class="inp" name="kaptchafield"></td>
                        			<td><img class="kaptcha" style="cursor:pointer" id="kaptcha" src="kaptcha.do"/></td>
                        		</tr>
                        		<tr height="40px">
                        			<td colspan="3"><input type="submit" class="login_btn" value="登录" style="cursor:pointer;" /></td>
                        		</tr>
                        		<tr height="30px">
                        			<td colspan="3"><p style="color: red;">${result }</p></td>
                        		</tr>
                        	</table>
                        	
                        	<!-- <p>用户名:<input type="text" value="admin" name="accountcode" id="" class="inp"/></p>
                            <p>密码:&nbsp;&nbsp;&nbsp;<input type="password" value="password" name="password" id="" class="inp"/></p>
                            <p>验证码:&nbsp;<input type="text" style="width: 130px" class="inp" name="kaptchafield"><img class="kaptcha" style="cursor:pointer" id="kaptcha" src="kaptcha.do"/></p> -->
                            <%-- <div style=" margin-top:-5px"><input type="submit" class="login_btn" value="登录" style="cursor:pointer;" /></div>
                            <div style=" margin-top:-15px"><p style="color: red;">${result }</p></div> --%>
                        </form>
                    
                    </div>
                    <div class="l_m_b">
                    	<img src="images/lock.png" width="61" height="70" />
                    </div>
                </div>
                <div class="login_bottom">
                </div>
            </div>
        </div>
    </div>
    <div style="clear:both"></div>

</body>
</html>