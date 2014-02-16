<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	var selectTreeid = 0;
	var setting = {
		data: {
			key: {
				name:"funchinesename"
			},
			simpleData: {
				enable: true,
				idKey: "id",
				pIdKey: "funparent"
			}
		},
		check: {
			enable: true
		},
		view:{
			dblClickExpand:false,
			showLine: true,
			selectedMulti: false
		},
		callback: {
			onClick: onClick
		}
	};
	
	function onClick(e,treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		zTree.expandNode(treeNode);
	}

	var nodes =  ${funList};
	
	function setchecknodes() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getCheckedNodes(true);
		
		var arrayObj = new Array();
		for(var i=0;i<nodes.length;i++) {
			arrayObj.push(nodes[i].id);
		}
		
		var b = JSON.stringify(arrayObj);
		
		var par = "funListString=" + b + "";
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/role/setAuth.do",
	        type : 'post',
	        data:{funListString:b,roleid:"${roleid}"},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("角色授权完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	        }
	    });
	}
	
	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		var authfunlist = ${authfunList};
		
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		if (authfunlist != null) {
			for (var i=0;i<authfunlist.length;i++) {
				var node = treeObj.getNodeByParam("id", authfunlist[i].id, null);
				if (node != null) {
					treeObj.checkNode(node, true, false);
				}
			}
		}
	})
	
	
</script>
<title>为角色赋权</title>
</head>
<body>
	<table id="testTable" cellpadding="0" cellspacing="0">
		<tbody>
			<tr >
                <td class="biaoti" colspan="2">为角色赋予功能使用权</td>
                <td>&nbsp;</td>
            </tr>
			<tr class="tr1">
				<td class="txt1" colspan="2"><ul id="treeDemo" class="ztree"></ul></td>
			</tr>
			<tr >
				<td class="caozuo" colspan="2">
					<button type="button" onclick="setchecknodes()">授权</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>