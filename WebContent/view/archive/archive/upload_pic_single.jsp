<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<base target="_self">

<style type="text/css">
.notcss {
	border: 0
}

</style>

<script>
	
	$(function() {
		var result = '${result }';
		if (result != "") {
			alert(result);
		}
	})

	function closepage() {
		window.returnValue = "ok";
		window.close();
	}

</script>
<title>上传多媒体文件</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<div align="center">
		<h4>请上传多媒体文件</h4>
		<form method="post" action="upload_pic_single.do" enctype="multipart/form-data">
			<table width="95%" border="0" cellpadding="4" cellspacing="1">
			<tr>
				<td>选择多媒体文件:
				
					<input type="file" name="file" />
					<input type="hidden" value="${treeid }" id="treeid" name="treeid" />
					<input type="hidden" value="${tabletype }" id="tabletype" name="tabletype" />
					<input type="hidden" value="${id }" id="id" name="id" />
				</td>
			</tr>
			<tr>
				<td>
					<input type="radio" value="IMAGE" name="slttype" checked />图片格式
            		<input type="radio" value="VIDEO" name="slttype"/>视频格式
            		<input type="radio" value="OTHER" name="slttype"/>其他类型
				</td>
			</tr>
			<tr>
				<td>
					<button type="submit">上传</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</table>
		</form>
	</div>
</body>
</html>