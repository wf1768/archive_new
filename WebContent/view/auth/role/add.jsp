<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/easyvalidator/css/validate.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/easy_validator.pack.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/jquery.bgiframe.min.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	$(function() {
		//获取传来的值
		var result = '${result }';
		//如果返回值不为空，说明保存了，弹出提示，刷新父页面
		if (result != "") {
			alert(result);
		}
	})
	
	
</script>
<title>添加角色</title>
</head>
<body>
	<form action="save.do" method="post">
		<table width="90%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
			<tbody>
				<tr >
	                <td colspan="2" align="center">添加角色</td>
	            </tr>
				<tr>
					<td>角色名称 :</td>
					<td><input type="text" name="rolename" value="${role.rolename }" reg="^.+$" tip="角色名称[必须填写]"></td>
				</tr>
				<tr >
					<td>角色描述 :</td>
					<td>
						<input name="rolememo" type="text" id="rolememo" value="${role.rolememo }" tip="角色描述[不必须填写] " />
					</td>
				</tr>
				<tr >
					<td colspan="2" align="center">
						<button type="submit"><span class="ok">保存</span></button>
						<button type="button" onclick="closepage()">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>