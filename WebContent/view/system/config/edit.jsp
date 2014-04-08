<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<base target="_self">
<title>修改属性值</title>
<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	$(function(){
		var result = '${result}';
		if (result != "") {
			alert(result);
		}
	})
</script>
</head>
<body>
	<form action="update.do" method="post">
		<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
			<tbody>
				<tr>
	                <td colspan="2" align="center">修改系统属性值</td>
	            </tr>
				<tr>
					<td>属性值 :</td>
					<td><input type="text" name="configvalue" value="${config.configvalue }"></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="submit" value="保存" />
						<button type="button" onclick="closepage()">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
		<input type="hidden" name="id" value="${config.id }">
	</form>
</body>
</html>