<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css" type="text/css">

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


<!--全文检索-内容部分开始-->
  	
    <div id="bodyer">
   		<div id="bodyer_left" style="width: auto;"></div>
    	<div id="bodyer_right">
    		<div class="xtgl">
	        	<form action="search.do" id="searchform" method="get">
	        		<input type="hidden" id="schTreeid" name="schTreeid" value="all" />
	       			<input type="hidden" name="currentPage" value="0" />
			        <fieldset>
			       		<input id="treeSel" type="text" readonly value="全部分类" onclick="showMenu();" />
			          	<input name="searchText"><input type="submit" value="搜索">
			        </fieldset>
		      	</form>
		      	<div class="alert alert-info fade in">
					<strong>提示！</strong>
					欢迎使用档案查询模块。本系统对中文智能分词，您在模糊检索时，请尽量输入词组，例如"北京"、"南京"。对于英文，请输入完整单词。
				</div>
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