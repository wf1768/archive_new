<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
		
		var id = '${id }';
		//var id = $("#id").val();
		//var treenode = $("#treenode").val();
		
		/* if(nodes[0].treenode.indexOf(treenode) == 0 ) {
			alert("不能将机构移动到本机构或子机构中，重新尝试，或与管理员联系。");
			return;
		} */
		var targetid = nodes[0].id;
		
		if (id == "" || targetid == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要将帐户移动选择的组下吗？")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/account/movesave.do",
		        type : 'post',
		        data:{id:id,targetid:targetid},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("更新完毕。");
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            }
		            reload();
		        }
		    });
		}
	}
	
	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
	})
	
	
</script>
<title>移动帐户</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">移动帐户</td>
            </tr>
			<tr>
				<td>机构名称 :</td>
				<td><ul id="treeDemo" class="ztree"></ul></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="move()">保存</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>