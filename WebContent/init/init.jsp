<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统初始化设置</title>
<script type="text/javascript">
var result = "${result}";
if (result != "") {
	alert(result);
	result = "";
}
</script>
</head>
<body>
<form action="update.do" method="post">
	<table>
		<tr>
			<td style="width: 190px">数据库连接类型:</td>
			<td><input type="text" style="width: 300px" name="initvalue" value="${database.initvalue }"/><input type="hidden" name="id" value="${database.id }"></td>
			<td><input type="submit" value="提交"></td>
			<td>${database_result }</td>
		</tr>
	</table>
</form>

<form action="update.do" method="post">
	<table>
		<tr>
			<td style="width: 190px">索引文件地址:</td>
			<td><input type="text" style="width: 300px" name="initvalue" value="${luceneurl.initvalue }" /><input type="hidden" name="id" value="${luceneurl.id }"></td>
			<td><input type="submit" value="提交"></td>
			<td>${luceneurl_result }</td>
		</tr>
	</table>
</form>

<form action="update.do" method="post">
	<table>
		<tr>
			<td style="width: 190px">openoffice安装地址:</td>
			<td><input type="text" style="width: 300px" name="initvalue" value="${openofficeurl.initvalue }" /><input type="hidden" name="id" value="${openofficeurl.id }"></td>
			<td><input type="submit" value="提交"></td>
			<td>${openofficeurl_result }</td>
		</tr>
	</table>
</form>

<form action="update.do" method="post">
	<table>
		<tr>
			<td style="width: 190px">文件转换bat路径:</td>
			<td><input type="text" style="width: 300px" name="initvalue" value="${serviceurl.initvalue }" /><input type="hidden" name="id" value="${serviceurl.id }"></td>
			<td><input type="submit" value="提交"></td>
			<td>${serviceurl_result }</td>
		</tr>
	</table>
</form>

<form action="update.do" method="post">
	<table>
		<tr>
			<td style="width: 190px">系统类型:</td>
			<td><input type="text" style="width: 300px" name="initvalue" value="${systemtype.initvalue }" /><input type="hidden" name="id" value="${systemtype.id }"></td>
			<td><input type="submit" value="提交"></td>
			<td>${systemtype_result }</td>
		</tr>
	</table>
</form>

<form action="update.do" method="post">
	<table>
		<tr>
			<td style="width: 190px">注册码:</td>
			<td><input type="text" style="width: 300px" name="initvalue"  value="${registcode.initvalue }"/><input type="hidden" name="id" value="${registcode.id }"></td>
			<td><input type="submit" value="提交"></td>
			<td>${registcode_result }</td>
		</tr>
	</table>
</form>

</body>
</html>