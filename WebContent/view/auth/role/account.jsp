<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table/table_design.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<base target="_self">
<style type="text/css">
caption {
    background: none repeat scroll 0 0 rgba(0, 0, 0, 0);
    color: #333333;
    font-size: 1.4em;
    font-weight: bold;
    padding: 0 0 0.5em;
    text-align: left;
    text-transform: uppercase;
}
* {
    list-style: none outside none;
    margin: 0;
    padding: 0;
}
</style>

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
	
	function updateAccount(roleid,accountid) {
		if (accountid == "" || roleid == "") {
			alert("没有获得要移除角色的帐户，请重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要移除选择的帐户角色吗？")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/role/updateAccount.do",
		        type : 'post',
		        data:{roleid:roleid,accountid:accountid},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("更新完毕。");
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            }
		            window.location.reload(true);
		        }
		    });
		}
	}
	
</script>
<title>查看被赋予[${role.rolename }]角色的帐户</title>
</head>
<body>
	<div style="width: 90%;margin: 0 auto;">
		<table id="cssz_table" width="100%">
			<caption>拥有 [${role.rolename }] 角色的全部帐户，可以移除帐户的角色。</caption>
			<thead>
			<tr>
				<th>#</th>
				<th>帐户名称</th>
				<th>帐户状态</th>
				<th>帐户描述</th>
				<th>操作</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${accounts}" varStatus="i" var="item">
				<tr >
					<td>${i.index+1 }</td>
					<th>${item.accountcode}</th>
					<td>${item.accountstate == 1?'启用':'禁用'}</td>
					<td>${item.accountmemo}</td>
					<td>
						<a href="#" onclick="updateAccount('${role.id}','${item.id}')" class="juse">移除</a>
					</td>
				</tr>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th scope="row">Total</th>
					<td colspan="4">共 ${fn:length(accounts) } 条记录</td>
				</tr>
			</tfoot>
		</table>
	</div>
</body>
</html>