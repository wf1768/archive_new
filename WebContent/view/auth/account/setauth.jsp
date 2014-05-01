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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/util.js"></script>
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
		//处理节点的附属权限
		var treetype = treeNode.treetype;
		$('input[type="checkbox"]').removeAttr("checked");
		$('input[type="checkbox"]').removeAttr("disabled");
		$("#docauth").removeAttr("disabled");
		$("#ajAuth").html('');
		$("#wjAuth").html('');
		
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/account/getTreeAuth.do",
	        type : 'post',
	        data:{id:"${account.id }",treeid:treeNode.id},
	        dataType : 'text',
	        success : function(data) {
	            if (data != "failure") {
	            	var myData = JSON.parse(data);
	            	$('input[type="checkbox"]').removeAttr("checked");
		            if (myData.filescan == 1) {
		            	$('#filescan').attr("checked",true);
		            }
		            if (myData.filedown == 1) {
		            	$('#filedown').attr("checked",true);
		            }
		            if (myData.fileprint == 1) {
		            	$('#fileprint').attr("checked",true);
		            }
		            $('.btn').removeAttr("disabled");
		            if (myData.filter != null && myData.filter != "") {
		            	showDataAuth(myData.id,myData.filter,treetype);
		            }
		            else {
		            	intoTbody("ajAuth",4);
		            	intoTbody("wjAuth",4);
		            }
		            $("#docauth").val(myData.docauth);
	            }
	            else {
            		$('input[type="checkbox"]').attr('disabled', 'disabled');
            		$('.btn').attr('disabled', 'disabled');
            		$("#docauth").attr("disabled","disabled");
            		$("#ajAuth").html('');
            		$("#wjAuth").html('');
            		intoTbody("ajAuth",4);
            		intoTbody("wjAuth",4);
	            }
	            
	        }
	    });
		//获取选择的树节点档案类型，是A还是F／P
		$.ajax({
	        async: false,
	        url: "${pageContext.request.contextPath}/tree/getTempletType.do",
	        type: 'post',
	        dataType: 'text',
	        data: {treeid:treeNode.id},
	        success: function (data) {
	            if (data != "failure") {
	            	var myData = JSON.parse(data);
	            	if (myData.templettype == 'F') {
	            		$("#ajDataAuth").hide();
	            	}
	            	else {
	            		$("#ajDataAuth").show();
	            	}
	            } else {
	                openalert('读取数据时出错，请尝试重新操作或与管理员联系!');
	            }
	        }
	    });
	}
	
	function saveTreeAuth() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (nodes.length != 1) {
			alert("请选择档案节点，再设置附属权限。");
			return;
		}
		node = nodes[0];
		
		var filescan = 0;
		var filedown = 0;
		var fileprint = 0;
		
		if ($("#filescan").is(":checked")) { 
			filescan = 1;
		}
		if ($("#filedown").is(":checked")) { 
			filedown = 1;
		}
		if ($("#fileprint").is(":checked")) { 
			fileprint = 1;
		}
		var b = true;
		if (node.treetype != "W") {
			if (!confirm("选择的是档案夹，将对档案夹下的所有档案节点赋予相同的电子文件权限，是否继续？")) {
				b = false;
			}
		}

		if (b) {
			$.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/account/saveTreeAuth.do",
		        type : 'post',
		        data:{accountid:"${account.id }",treeid:node.id,filescan:filescan,filedown:filedown,fileprint:fileprint},
		        dataType : 'text',
		        success : function(data) {
		        	if (data == "success") {
		            	alert("授权完毕。");
		            	
		            } else {
		            	alert("授权出现问题，请重试或与管理员联系");
		            }
		        }
		    });
		}
	}
	
	function saveDocAuth() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (nodes.length != 1) {
			alert("请选择档案节点，再设置附属权限。");
			return;
		}
		node = nodes[0];
		
		var docauth = $("#docauth").val();
		
		var b = true;
		if (node.treetype != "W") {
			if (!confirm("选择的是档案夹，将对档案夹下的所有档案节点赋予相同的电子文件浏览范围权限，是否继续？")) {
				b = false;
			}
		}

		if (b) {
			$.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/account/saveDocAuth.do",
		        type : 'post',
		        data:{accountid:"${account.id }",treeid:node.id,docauth:docauth},
		        dataType : 'text',
		        success : function(data) {
		        	if (data == "success") {
		            	alert("授权完毕。");
		            	
		            } else {
		            	alert("授权出现问题，请重试或与管理员联系");
		            }
		        }
		    });
		}
	}
	
	var nodes = ${treeList};
	
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function setchecknodes() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getCheckedNodes(true);
		
		var arrayObj = new Array();
		for(var i=0;i<nodes.length;i++) {
			arrayObj.push(nodes[i].id);
		}
		
		var treeids = JSON.stringify(arrayObj);
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/account/saveAccountAuth.do",
	        type : 'post',
	        data:{id:"${account.id }",treeids:treeids},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("授权完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            };
	        }
	    });
	}
	
	function showSetDataAuthWindow(id) {
		
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (nodes.length != 1) {
			alert("请选择档案节点，再设置附属权限。");
			return;
		}
		node = nodes[0];
		
		if (node.id == '0') {
			alert("记录权限只能按档案类型来创建，请重新选择。");
			return;
		}
		
		if (node.treetype == "T") {
			alert("记录权限只能按档案类型来创建，请重新选择。");
			return;
		}
		
		var b = true;
		if (node.treetype == "F" || node.treetype == "FT") {
			if (!confirm("选择的是档案夹，将对档案夹下的所有档案节点赋予相同的记录权限，是否继续？")) {
				b = false;
			};
		}

		if (b) {
			var url = "showSetDataAuthWindow.do?id=${account.id }&treeid="+node.id+"&tabletype="+id+"&time="+Date.parse(new Date());
			var whObj = { width: 550, height: 300 };
			var result = openShowModalDialog(url,window,whObj);
			//关闭窗口后，刷新页面
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = treeObj.getSelectedNodes();
			if (nodes.length != 1) {
				return;
			}
			node = nodes[0];
			onClick(null,null,node);
		};
	}
	
	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		var accountTrees = ${accountTrees};
		
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		if (accountTrees != null) {
			for (var i=0;i<accountTrees.length;i++) {
				var node = treeObj.getNodeByParam("id", accountTrees[i].id, null);
				if (node != null) {
					treeObj.checkNode(node, true, false);
				}
			}
		}
		
		//禁用各种保存按钮
		$(".btn").attr('disabled', 'disabled');
		$('input[type="checkbox"]').attr('disabled', 'disabled');
		$("#docauth").attr("disabled","disabled");
	})
	
	
	//显示数据访问权限
	function showDataAuth(accounttreeid,filter,templettype) {
		$("#ajAuth").html('');
		$("#wjAuth").html('');
		//如果当前树节点的档案类型是标准档案或图片档案
		if (templettype != "F") {
			//获取条件对象
			var array = JSON.parse(filter);
			
			for(var i=0;i<array.length;i++) {
				if (array[i].tableType == '01') {
					createDataAuthTable(accounttreeid,'ajAuth',JSON.stringify(array[i]));
				}
				else {
					createDataAuthTable(accounttreeid,'wjAuth',JSON.stringify(array[i]));
				}
			}
			
		}
		else {
			//获取条件对象
			var array = JSON.parse(filter);
			for(var i=0;i<array.length;i++) {
				createDataAuthTable(accounttreeid,'wjAuth',JSON.stringify(array[i]));
			}
		}
		
		//获取html内容，如果为空，要填充空白
		var ajhtml = $("#ajAuth").html();
		var wjhtml = $("#wjAuth").html();
		if (ajhtml == '') {
			intoTbody("ajAuth",4);
		}
		
		if (wjhtml == '') {
			intoTbody("wjAuth",4);
		}
	}
	
	function intoTbody(who,colspanNum) {
		var tbody = "<tr>";
		tbody += "<td colspan=\""+colspanNum+"\" align=\"center\"></td>";
		tbody += "</tr>";
		
		$("#"+who).html(tbody);
	}
	
	//创建数据访问权限页面显示
	function createDataAuthTable(accounttreeid,who,dataAuth) {
		
		if (dataAuth == "") {
			intoTbody(who,4);
			return;
		}
		var html = $("#"+who).html();
		var auth = JSON.parse(dataAuth);
		var tmp = '<tr id="'+auth.id+'">';
		tmp += '<td>'+auth.fieldname+'</td>';
		if (auth.oper == 'equal') {
			tmp += '<td>等于</td>';
		}
		else if (auth.oper == 'like'){
			tmp += '<td>包含</td>';
		}
		tmp += '<td>'+auth.dataAuthValue+'</td>';
		tmp += '<td><a href="javascript:;" onclick="removeDataAuth(\''+accounttreeid+'\',\''+auth.id+'\')" >删除</a></td>';
		tmp += '</tr>';
		html += tmp;
		$("#"+who).html(html);
	}
	//删除组的数据访问权限
	function removeDataAuth(accounttreeid,id) {
		if (id == "") {
			alert("请选择要删除的数据！");
		}
		
		if (confirm("确定要删除选择的数据访问权限吗，是否继续？")) {
			$.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/account/removeDataAuth.do",
		        type : 'post',
		        data:{accounttreeid:accounttreeid,id:id},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("删除完毕。");
		            	//删除后刷新
		            	var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		    			var nodes = treeObj.getSelectedNodes();
		    			if (nodes.length != 1) {
		    				return;
		    			}
		    			node = nodes[0];
		    			onClick(null,null,node);
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            };
		        }
		    });
		}
	}
	
</script>
<title>为帐户赋权</title>
</head>
<body>
	<div style="float: left;overflow-x: auto; overflow-y: auto; height: 480px; width:400px;">
	<table width="350" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
		<tbody>
			<tr >
                <td colspan="2" align="center">为帐户 [${account.accountcode }] 赋予档案使用权</td>
            </tr>
			<tr>
				<td colspan="2"><ul id="treeDemo" class="ztree"></ul></td>
			</tr>
			<tr >
				<td colspan="2" align="center">
					<button type="button" onclick="setchecknodes()">授权</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
	<div style="float:right;margin-right: 20px;overflow:auto;width: 340px;">
		<div id="ajDataAuth">
			<table width="100%" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
				<thead>
					<tr >
		                <td colspan="4" align="center">设置帐户对档案节点数据［案卷］记录的访问权限</td>
		            </tr>
					<tr>
						<th>字段</th>
						<th>关系符</th>
						<th>值</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody id="ajAuth">
						<tr>
							<td colspan="4" align="center">
							</td>
						</tr>
				</tbody>
				<tfoot>
					<tr >
						<td colspan="4" align="center">
							<button class="btn" type="button" disabled onclick="showSetDataAuthWindow('01')">添加</button>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
		<div id="wjDataAuth" style="margin-top: 20px;">
			<table width="100%" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
				<thead>
					<tr >
		                <td colspan="4" align="center">设置帐户对档案节点数据［文件］记录的访问权限</td>
		            </tr>
					<tr>
						<th>字段</th>
						<th>关系符</th>
						<th>值</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody id="wjAuth">
						<tr>
							<td colspan="4" align="center">
							</td>
						</tr>
				</tbody>
				<tfoot>
					<tr >
						<td colspan="4" align="center">
							<button class="btn" disabled type="button" onclick="showSetDataAuthWindow('02')">添加</button>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
		<div style="margin-top: 20px;">
			<table width="100%" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
				<thead>
					<tr >
						<td>
		                <input type="checkbox" id="filescan" value="1">查看电子全文
						<input type="checkbox" id="filedown" value="1">下载电子全文
						<input type="checkbox" id="fileprint" value="1">打印电子全文
						</td>
		            </tr>
				</thead>
				<tfoot>
					<tr >
						<td colspan="4" align="center">
							<button class="btn" type="button" onclick="saveTreeAuth()">保存</button>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
		<div style="margin-top: 20px;">
			<table width="100%" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
				<thead>
					<tr >
						<td>
		                <select id="docauth">
							<c:forEach  items="${codes}" var="item">
								<option value="${item.id }">${item.columndata }</option>
						    </c:forEach>
						</select>
						</td>
		            </tr>
				</thead>
				<tfoot>
					<tr >
						<td colspan="4" align="center">
							<button class="btn" type="button" onclick="saveDocAuth()">保存</button>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</body>
</html>