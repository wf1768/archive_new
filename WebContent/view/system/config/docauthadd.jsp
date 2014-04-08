<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function savedocauth() {
		var columnname = $("#columnname").val();
		var columndata = $("#columndata").val();
		
		if (columnname == "" || columndata == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/config/savedocauth.do",
	        type : 'post',
	        data:{columnname:columnname,columndata:columndata},
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
<title>添加电子文件权限代码</title>
</head>
<body>
	<table width="90%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">添加电子文件权限代码</td>
            </tr>
			<tr>
				<td>代码名称 :</td>
				<td><input type="text" id="columnname" name="columnname"></td>
			</tr>
			<tr>
				<td class="txt1">代码值 :</td>
				<td><input type="text" id="columndata" name="columndata"></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="savedocauth()">保存</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>