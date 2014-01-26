<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base target="_self">
<title>修改属性值</title>
<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
</script>
</head>
<body>
	<form action="update.do" method="post">
		<input type="hidden" name="id" value="${config.id }">
		属性值:<input type="text" name="configvalue" value="${config.configvalue }">
		<input type="submit" value="保存" />
	</form>
	<input type="button" value="关闭" onclick="closepage()">
	<font color="blue">${result }</font>
</body>
</html>