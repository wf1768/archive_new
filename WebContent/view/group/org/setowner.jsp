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
		//window.location.href="${pageContext.request.contextPath}/org/list.do?type=group&orgid="+nodes.id;
	};


	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function move() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (nodes.length != 1) {
			alert("请选择移动的目标组织机构，再尝试保存移动结果。");
			return;
		}
		
		var id = $("#id").val();
		var treenode = $("#treenode").val();
		
		if(nodes[0].treenode.indexOf(treenode) == 0 ) {
			alert("不能将机构移动到本机构或子机构中，重新尝试，或与管理员联系。");
			return;
		}
		var targetid = nodes[0].id;
		
		if (id == "" || targetid == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要移动组吗？移动组，该组下的帐户和管理者将不受变化。")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/org/movesave.do",
		        type : 'post',
		        data:{id:id,targetid:targetid},
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
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		var treeid = "${orgid}";
		selectTreeid = treeid;
		selectNode(treeid);
		
		//获取传来的值
		var result = '${result }';
		//如果返回值不为空，说明保存了，弹出提示，刷新父页面
		if (result != "") {
			alert(result);
			window.dialogArguments.location.reload();
		}
	})
	
	
</script>
<title>移动组织机构</title>
</head>
<body>
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
						<a href="#" onclick="updateAccount('${role.id}','${item.id}')" class="juse">移除</a>
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
	</div>
</body>
</html>