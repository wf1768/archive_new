<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function update() {
		var accountcode = $("#accountcode").val();
		var accountmemo = $("#accountmemo").val();
		var accountstate = 0;
		if ($("#accountstate").is(":checked")) {
			accountstate = 1;
		}
		//var id = $("#id").val();
		var id = '${account.id }';
		if (accountcode == "" || id == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		var account = {};
		account.id = id;
		account.accountcode = accountcode;
		account.accountmemo = accountmemo;
		account.accountstate = accountstate;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/account/update.do",
	        type : 'post',
	        data:account,
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
<title>修改帐户</title>
</head>
<body>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">修改帐户</td>
            </tr>
            <tr>
				<td>帐户名称 :</td>
				<td><input type="text" id="accountcode" name="accountcode" value="${account.accountcode }"></td>
			</tr>
			<tr>
				<td>帐户状态 :</td>
				<td><input type="checkbox" id="accountstate" name="accountstate" ${account.accountstate==1?'checked':'' }>启用</td>
			</tr>
			<tr>
				<td>帐户描述 :</td>
				<td><input type="text" id="accountmemo" name="accountmemo" value="${account.accountmemo }"></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="update()">保存</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>