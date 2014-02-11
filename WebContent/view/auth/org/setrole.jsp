<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
	
	function removerole(orgid,roleid) {
		
		if (orgid == "" || roleid == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要移除选择的 ${org.orgname} 管理者吗？移除后，取消该帐户管理该机构的能力。")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/org/removerole.do",
		        type : 'post',
		        data:{orgid:orgid,roleid:roleid},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("更新完毕。");
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            }
		            window.dialogArguments.location.reload();
		            window.location.reload();
		        }
		    });
		}
	}
	
	function saverole(orgid,roleid) {
		
		if (orgid == "" || roleid == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要将选择的角色赋予 ${org.orgname} 组吗？")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/org/saverole.do",
		        type : 'post',
		        data:{orgid:orgid,roleid:roleid},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("更新完毕。");
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            }
		            window.dialogArguments.location.reload();
		            window.location.reload();
		        }
		    });
		}
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
<title>设置组角色</title>
</head>
<body>
	<div style="width: 90%;margin: 0 auto;">
		<table id="cssz_table" border="1" cellpadding="1" cellspacing="0" width="100%">
			<caption>设置 [${org.orgname }] 组的角色。</caption>
			<thead>
			<tr>
				<th>#</th>
				<th>角色名称</th>
				<th>角色描述</th>
				<th>操作</th>
			</tr>
			</thead>
			<tbody>
				<c:if test="${role != null }">
				<tr >
					<td>1</td>
					<th>${role.rolename}</th>
					<td>${role.rolememo}</td>
					<td>
						<a href="#" onclick="removerole('${org.id}','${role.id}')" class="juse">移除</a>
					</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>
	<div>
		<ul id="treeDemo" class="ztree"></ul>
		<table id="cssz_table" border="1" cellpadding="1" cellspacing="0" width="100%">
			<caption>选择作为 [${org.orgname }] 的角色。</caption>
			<thead>
			<tr>
				<th>#</th>
				<th>角色名称</th>
				<th>角色描述</th>
				<th>操作</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${sys_roles}" varStatus="i" var="item">
				<tr >
					<td>${i.index+1 }</td>
					<th>${item.rolename}</th>
					<td>${item.rolememo}</td>
					<td>
						<a href="#" onclick="saverole('${org.id}','${item.id}')" class="juse">添加角色</a>
					</td>
				</tr>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th scope="row">Total</th>
					<td colspan="4">共 ${fn:length(sys_roles) } 条记录</td>
				</tr>
			</tfoot>
		</table>
	</div>
</body>
</html>