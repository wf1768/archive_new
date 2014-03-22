<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>

<script type="text/javascript">
	var setting = {
		check: {
			enable: true,
		},
		view: {
			dblClickExpand: false
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
		zTree.checkNode(treeNode, !treeNode.checked, null, true);
		return false;
	}
	
	function onCheck(e, treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("searchTreeM"),
		nodes = zTree.getCheckedNodes(true),
		v = "",scht = "";
		for (var i=0, l=nodes.length; i<l; i++) {
			v += nodes[i].treename + ",";
			scht += nodes[i].id + ",";
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
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
	});
</script>

<!--智能检索-内容部分开始-->
  	
    <div id="bodyer">
        <div id="bodyer_left">
			<dl>
				<dt>
					<a href="#" class="blue"><img src="${pageContext.request.contextPath}/images/i1_03.png" width="29" height="22" class="tubiao" />
						<span>
							智能检索
						</span>
					</a>
				</dt>
				<dd>
					<ul id="treeDemo" class="ztree"></ul>
				</dd>
			</dl>
		</div>
		<div id="bodyer_right">
			<div style="margin-top: 20px;" align="center">
	        	<form action="search.do" method="get">
	        		<table>
	        			<tr>
	        				<td>
	        					<input type="hidden" id="schTreeid" name="treeid" value="" />
	        					<input id="treeSel" type="text" readonly value="全部分类" style="width:120px;" onclick="showMenu();" />
				        	</td>
		        			<td><input type="text" name="searchText" value="" /></td>
		        			<td><input type="submit" value="检 索"/></td>
	        			</tr>
	        		</table>
	        	</form>
	        </div>
			<div id="sj" class="shuju">
				<table id="class_table">
					<tr>
					
					</tr>
					<c:forEach items="${resultList}" var="item">
	        				<c:forEach items="${item}" var="itm">
	        					<tr>
	        						<td> <c:out value="${itm[\"AJH\"]}"/> </td>
	        						<td> <c:out value="${itm[\"TM\"]}"/> </td>
	        					</tr>
	        				</c:forEach>
	        			</c:forEach>
				</table>
			</div>
			<!-- 分类查询树 -->
			<div id="menuContent" class="menuContent" style="display:none; position: absolute;">
				<ul id="searchTreeM" class="ztree" style="margin-top:0; width:180px; height: 300px;"></ul>
			</div>
			
		</div>
	</div>

<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>