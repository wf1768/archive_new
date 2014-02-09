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
	
	function saveOrg() {
		var parentid = $("#parentid").val();
		var orgname = $("#orgname").val();
		
		if (parentid == "" || orgname == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/org/save.do",
	        type : 'post',
	        data:{parentid:parentid,orgname:orgname},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	            window.dialogArguments.location.reload();
	        }
	    });
	}
	
	$(function() {
		//获取传来的值
		var result = '${result }';
		//如果返回值不为空，说明保存了，弹出提示，刷新父页面
		if (result != "") {
			alert(result);
			window.dialogArguments.location.reload();
		}
	})
	
	
</script>
<title>添加组织机构</title>
</head>
<body>
	<form action="" method="post">
		<table id="testTable" cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
	                <td class="biaoti" colspan="2">添加组织机构</td>
	                <td>&nbsp;<input type="hidden" id="parentid" name="parentid" value="${parentid }"></td>
	            </tr>
				<tr class="tr1">
					<td class="txt1">机构名称 :</td>
					<td><input type="text" id="orgname" name="orgname" value="${org.orgname }"></td>
				</tr>
				<tr>
					<td class="caozuo" colspan="2">
						<button type="button" onclick="saveOrg()">保存</button>
						<button type="button" onclick="closepage()">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>