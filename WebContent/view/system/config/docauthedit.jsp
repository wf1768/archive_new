<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/form.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function updatedocauth() {
		var id = $("#id").val();
		var columnname = $("#columnname").val();
		var columndata = $("#columndata").val();
		
		if (id=="" || columnname == "" || columndata == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/config/updatedocauth.do",
	        type : 'post',
	        data:{id:id,columnname:columnname,columndata:columndata},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新完毕。");
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	        }
	    });
	}
	
</script>
<title>修改电子文件权限代码</title>
</head>
<body>
	<form action="" method="post">
		<table id="testTable" cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
	                <td class="biaoti" colspan="2">修改电子文件权限代码</td>
	                <td><input type="hidden" id="id" name="id" value="${docauth.id }"/></td>
	            </tr>
				<tr class="tr1">
					<td class="txt1">代码名称 :</td>
					<td><input type="text" id="columnname" name="columnname" value="${docauth.columnname }"></td>
				</tr>
				<tr class="tr1">
					<td class="txt1">代码值 :</td>
					<td><input type="text" id="columndata" name="columndata" value="${docauth.columndata }"></td>
				</tr>
				<tr>
					<td class="caozuo" colspan="2">
						<button type="button" onclick="updatedocauth()">保存</button>
						<button type="button" onclick="closepage()">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>