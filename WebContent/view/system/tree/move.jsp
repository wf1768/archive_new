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
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dialog_util.js"></script>
<base target="_self">

<script>
	
	var selectTreeid = 0;
	var setting = {
			data: {
				key: {
					name:"treename"
				},
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "parentid"
				}
			}
	};
	var nodes = ${result};
	
	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id", 0, null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			treeObj.expandNode(node);
		}
	});
	
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function save() {
		var id = $("#id").val();
		
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (typeof (nodes[0]) == "undefined") {
			alert("请选择移动到目标档案夹。");
			return;
		}
		if (nodes[0].treetype == 'W' || nodes[0].id == '0') {
			alert("请选择移动目标档案夹，再移动档案类型。");
			return;
		}
		
		var targetid = nodes[0].id;
		
		if (id == "" || targetid == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		var d = {};
		d.id = id;
		d.targetid = targetid;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/tree/movesave.do",
	        type : 'post',
	        data:d,
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新完毕。");
	            	reload();
	            } else if (data == "error") {
	            	alert("不能移动到本身或下级，请重新选择.");
	            	return;
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!");
	            	return;
	            }
	            
	        }
	    });
	}
</script>
<title>移动档案节点</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">
                	移动档案节点
                	<input type="hidden" id="id" name="id" value="${id }">
                </td>
            </tr>
            <tr>
				<td width="100px">选择移动到目标 :</td>
				<td>
					<ul id="treeDemo" class="ztree"></ul>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="save()">移动</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>