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
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dialog_util.js"></script>
<base target="_self">

<script>

	var selectTreeid = 0;
	var setting = {
			data: {
				keep: {
					parent: true
				},
				key:{
					name:"orgname"
				},
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "parentid"
				}
			},
			callback: {
				onClick: onClick
			}
	};
	var nodes = ${orgList};
	
	for (var i=0;i<nodes.length;i++) {
		
		if (nodes[i].parentid == '0') {
			nodes[i].open = true;
		}
		nodes[i].isParent = true;
		nodes[i].iconOpen = "${pageContext.request.contextPath}/images/folder-open.gif";
		nodes[i].iconClose = "${pageContext.request.contextPath}/images/folder.gif";
	}
	
	function selectNode(treeid) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id",treeid,null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			treeObj.expandNode(node);
		}
		
	}
	
	function onClick(event, treeId, nodes) {
		window.location.href="setowner.do?orgid=${org.id}&selectOrgid="+nodes.id+"&time="+Date.parse(new Date());
	};


	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function removeowner(orgid,accountid) {
		
		if (orgid == "" || accountid == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要移除选择的 ${org.orgname} 管理者吗？移除后，取消该帐户管理该机构的能力。")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/org/removeowner.do",
		        type : 'post',
		        data:{orgid:orgid,accountid:accountid},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("更新完毕。");
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            }
		            //window.dialogArguments.location.reload();
		            reload();
		    });
		}
	}
	
	function saveowner(orgid,accountid) {
		
		if (orgid == "" || accountid == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要将选择的帐户作为 ${org.orgname} 管理者吗？")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/org/saveowner.do",
		        type : 'post',
		        data:{orgid:orgid,accountid:accountid},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("更新完毕。");
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            }
		            //window.dialogArguments.location.reload();
		            reload();
		        }
		    });
		}
	}
	
	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		var treeid = "${selectOrgid}";
		selectTreeid = treeid;
		selectNode(treeid);
		
	})
	
	
</script>
<title>组织机构管理者</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<div style="width: 90%;margin: 0 auto;">
		<table id="cssz_table" border="1" cellpadding="1" cellspacing="0" width="100%">
			<caption>作为 [${org.orgname }] 组织机构管理者的帐户列表。</caption>
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
			<c:forEach items="${owners}" varStatus="i" var="item">
				<tr >
					<td>${i.index+1 }</td>
					<th>${item.accountcode}</th>
					<td>${item.accountstate == 1?'启用':'禁用'}</td>
					<td>${item.accountmemo}</td>
					<td>
						<a href="#" onclick="removeowner('${org.id}','${item.id}')" class="juse">移除</a>
					</td>
				</tr>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th scope="row">Total</th>
					<td colspan="4">共 ${fn:length(owners) } 条记录</td>
				</tr>
			</tfoot>
		</table>
	</div>
	<div>
		<ul id="treeDemo" class="ztree"></ul>
		<table id="cssz_table" border="1" cellpadding="1" cellspacing="0" width="100%">
			<caption>选择以下帐户作为 [${org.orgname }] 的管理者。</caption>
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
						<a href="#" onclick="saveowner('${org.id}','${item.id}')" class="juse">添加管理者</a>
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