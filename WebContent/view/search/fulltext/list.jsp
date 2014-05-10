<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css" type="text/css">

<style>
table.tab_data{width:70%;border:1px solid #b6c3d3;border-collapse:collapse;margin:10px 0;text-align: left;}
table.tab_data th{border:1px solid #d8e4f2;background:#e4f4fd;color:#3a6fa5;height:28px;vertical-align:middle;}
table.tab_data td{border:1px solid #d8e4f2;background:#fff;color:#333;padding:3px;vertical-align:middle;text-align:center;}

table.t_data {width:70%;border-collapse: collapse; text-align: left;}
table.t_data th{color: black; font-size: 10pt; font-weight: bold;border: 1pt solid windowtext;height:28px;background:#538ed5 }
table.t_data tr td{border-style: none solid solid; border-width: medium 1pt 1pt;padding:3px;vertical-align:middle;text-align:center;}
</style>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>


<script type="text/javascript">
	var setting = {
		check: {
			enable: true,
		},
		view: {
			dblClickExpand:false,
			showLine: true,
			selectedMulti: false
		},
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
		callback: {
			beforeClick: beforeClick,
			onCheck: onCheck
		}
	};
	
	function beforeClick(treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("searchTreeM");
		//zTree.checkNode(treeNode, !treeNode.checked, null, true);
		zTree.expandNode(treeNode);
		return false;
	}
	
	function onCheck(e, treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("searchTreeM");
		nodes = zTree.getCheckedNodes(true);
		v = "",scht = "";
		for (var i=0, l=nodes.length; i<l; i++) {
			if(nodes[i].treetype == 'W'){
				v += nodes[i].treename + ",";
				scht += nodes[i].id + ",";
			}
		}
		if (v.length > 0 ) v = v.substring(0, v.length-1);
		if (scht.length > 0 ) scht = scht.substring(0, scht.length-1);
		var treeObj = $("#treeSel");
		var schTreeid = $("#schTreeid");
		treeObj.attr("value", v);
		schTreeid.attr("value",scht);
	}

	function showMenu() {
		var treeObj = $("#treeSel");
		var cityOffset = $("#treeSel").offset();
		$("#menuContent").css({left:cityOffset.left + "px", top:cityOffset.top + treeObj.outerHeight() + "px"}).slideDown("fast");

		$("body").bind("mousedown", onBodyDown);
	}
	function hideMenu() {
		$("#menuContent").fadeOut("fast");
		$("body").unbind("mousedown", onBodyDown);
	}
	function onBodyDown(event) {
		if (!(event.target.id == "menuBtn" || event.target.id == "treeSel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
			hideMenu();
		}
	}
	var nodes =  ${treeList};
	$(function() {
		$.fn.zTree.init($("#searchTreeM"), setting,nodes);
	});
</script>

<script type="text/javascript">
	var setting_s = {
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
			callback: {
				onClick: onClick
			}
	};
	var nodes_s = ${searchTrees };

	function selectNode(treeid) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id", treeid, null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			treeObj.expandNode(node);
		}
	}

	function onClick(event, treeId, nodes) {
		if (nodes.treetype != 'W') {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			treeObj.expandNode(nodes);
		} else {
			window.location.href = "${pageContext.request.contextPath}/archive/list.do?treeid=" + nodes.id;
		}
	};
	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting_s, nodes_s);
	});
	
</script>
<!--全文检索-内容部分开始-->
  	<div id="bodyer">
  		<div id="bodyer_left">
  			<dl>
				<dt>
					<a href="#" class="blue"><img
						src="${pageContext.request.contextPath}/images/i1_03.png"
						width="29" height="22" class="tubiao" /> <span>全文检索</span> </a>
				</dt>
				<dd>
					<ul id="treeDemo" class="ztree"></ul>
				</dd>
			</dl>
  		</div>
  		<div id="bodyer_right">
  			<form action="search.do" method="post">
       			<input type="hidden" id="schTreeid" name="schTreeid" value="all" />
       			<input type="hidden" name="currentPage" value="0" />
        		<table>
        			<tr>
        				<td><input id="treeSel" type="text" readonly value="全部分类" onclick="showMenu();" /></td>
	        			<td><input type="text" name="searchText" value="" /></td>
	        			
	        			<td><input type="submit" value="检 索"/></td>
        			</tr>
        		</table>
        	</form>
        	
        	<div style="font-size: 12px;color: black;float: right; width: 70%">
				结果：
				<c:forEach items="${result}" varStatus="i" var="item">
	   				<c:forEach items="${item.DATA}" var="itm">
		   				<c:if test="${i.index/2 == 0}">
						<table cellspacing="0" cellpadding="0" border="0" class="tab_data">
						</c:if>
						<c:if test="${i.index/2 !=0}">
						<table cellspacing="0" cellpadding="0" border="0" class="t_data">
						</c:if>
		   					<tr>
		   						<td>文件名</td><td>${itm['docoldname'] }</td>
		   						<td>所属档案库</td><td>${itm['treename'] }</td>
		   					</tr>
		   					<tr>
		   						<td>文件类型</td><td>${itm['docext']}</td>
		   						<td>文件长度</td><td>${itm['doclength']}</td>
		   					</tr>
		   					<tr>
		   						<td>上传人</td><td>${itm['creater']}</td>
		   						<td>上传日期</td><td>${itm['createtime'] }</td>
		   					</tr>
		   					<tr>
		   						<td>摘要</td><td colspan="3">${fn:substring(itm['content'], 0, 200)}</td>
		   					</tr>
		   					<tr><td colspan="4"><input type="button" value="查看预览" />&nbsp;&nbsp;<input type="button" value="下载全文" /></td></tr>
	   					</table>
	   				</c:forEach>
	   			</c:forEach>
			</div>
  		</div>
  		<div style="clear: both"></div>
  	</div>
	
<!--内容部分结束-->
	
<!-- 分类查询树 -->
	<div id="menuContent" class="menuContent" style="display:none; position: absolute;">
		<ul id="searchTreeM" class="ztree" style="margin-top:0; width:180px; height: 200px;"></ul>
	</div>
	
<%@ include file="/view/common/footer.jsp"%>