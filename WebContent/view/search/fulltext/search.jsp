<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/search.css" type="text/css">

<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<!-- 分页插件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/pagination/pagination.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pagination/jquery.pagination.js"></script>

<script type="text/javascript">

	//创建页面显示
	function showResultList() {
		var list = ${result}.DATA;
		var doc = "";
		if(list.length>0){
			for (var i=0;i<list.length;i++) {
				doc += "<table cellpadding=0 cellspacing=0 class=\"result_tab\" width=\"100%\">";
				doc += "<tr><td width=\"10%\">文件名</td><td width=\"45%\">"+list[i].docoldname+"</td><td width=\"15%\">所属档案库</td><td width=\"30%\">"+list[i].treename+"</td></tr>";
				doc += "<tr><td>文件类型</td><td>"+list[i].docext+"</td><td>文件长度</td><td>"+list[i].doclength+"</td></tr>";
				doc += "<tr><td>上传人</td><td>"+list[i].creater+"</td><td>上传日期</td><td>"+list[i].createtime+"</td></tr>";
				doc += "<tr><td>摘要</td><td colspan=\"3\">"+list[i].summary+"</td></tr>";
				doc += "<tr><td colspan=\"4\"><button  class=\"btn btn-info btn-small\" onClick=\"openContentDialog('"+list[i].docid+"','"+list[i].treeid+"')\">查看预览</button><button  class=\"btn btn-info btn-small\" onClick=\"fileDown('"+list[i].docid+"','"+list[i].treeid+"')\">下载全文</button> </td></tr>";
				doc += "</table>";
			}
		}else{
			doc="该关键词没有检索到相应的内容。。。";
		}
		$('#cont').html(doc);

	}
</script>
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
		view: {
			nameIsHTML: true
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
			onClick: onClick
		}
	};
	//树节点
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
			window.location.href = "${pageContext.request.contextPath }/fulltext/search.do?schTreeid=${schTreeid}&currentPage=0&searchText=${searchText }&treeids=${treeids}&treeid=" + nodes.id;
		}
	};
	
	//检索结果条数
	var searchNum = ${searchNum};
	//修改节点
	function updateNode(treeid,searchNum) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id", treeid, null);
		if (node.treetype == 'F' || node.treetype == 'W') {
			node.treename = node.treename.replace(/_[\d]*$/g, "") + "[<span style='color:red;margin-right:0px;'>" + searchNum + "</span>]";
		}
		treeObj.updateNode(node);
	}
	
	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting_s, nodes_s);
		
		showResultList();
		var treeid = "${treeid}";
		selectNode(treeid);
		//树节点检索条数
		for(var j=0;j<searchNum.length;j++){
			for(var key in searchNum[j]){  
			   updateNode(key,searchNum[j][key]); 
			} 
		}
	});

</script>

<script type="text/javascript">

$(function(){
	var total = ${result}.ROWCOUNT;
	var pagesize = ${result}.PAGESIZE;
	var pageno = ${result}.CURRENTPAGE;
	$("#pagination").pagination(total, {
        'items_per_page'      : pagesize,
        'num_display_entries' : 5,
        'num_edge_entries'    : 2,
        'current_page'	      : pageno,
        'prev_text'           : "上一页",
        'next_text'           : "下一页",
        /* 'link_to'			  : url, */
        'callback'            : pageselectCallback
    });
});

function pageselectCallback(page_index, jq){
	var searchTxt = "${searchText }";
	var pageno = ${pageBean["CURRENTPAGE"]};//${pagebean.pageNo };
	if (page_index != pageno) {
		window.location.href="${pageContext.request.contextPath }/fulltext/search.do?schTreeid=${schTreeid}&treeids=${treeids}&treeid=${treeid}&currentPage="+page_index+"&searchText="+searchTxt;
	}
}; 

//预览
function openContentDialog(selectid,treeid) {
	$.ajax({
		async : false,
		url : "../doc/preview.do",
		type : 'post',
		dataType : 'text',
		data:"treeid="+treeid+"&docid="+selectid,
		success : function(data) {
			if (data == "0") {
				alert("对不起，您没有权限预览此文件！");
			} else {
				var a=document.createElement("a");  
				a.target="_blank"; 
				a.href="../readFile.html?selectid="+selectid+"&treeid="+treeid;
				document.body.appendChild(a);  
				a.click()
			}
		}
	});
	
}
//下载
function fileDown(docId,treeid){
	$.ajax({
		async : false,
		url : "../doc/fileDown.do",
		type : 'post',
		dataType : 'text',
		data:"treeid="+treeid,
		success : function(data) {
			if (data == "1") {
				window.location.href="../doc/download.do?id="+docId+"&treeid="+treeid;
			} else {
				alert("对不起，您没有权限下载此文件！");
			}
		}
	});
}

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
					<ul id="treeDemo" class="ztree" style="background:none;border: none;"></ul>
				</dd>
			</dl>
  		</div>
  		<div id="bodyer_right">
  			<!-- 搜索 -->
  			<div align="center"><div style="width: 43%">
	        	<div id="nav-bar-middle">
	            	<form class="nav-searchbar-inner" method="get" action="search.do" id="nav-searchbar">
	            		<input type="hidden" id="schTreeid" name="schTreeid" value="all" />
	       				<input type="hidden" name="currentPage" value="0" />
			            <div class="nav-submit-button nav-sprite">
			              <input type="submit" title="搜索" class="nav-submit-input" value="搜 索">
			            </div>
			            <span class="nav-sprite nav-facade-active" id="nav-search-in" style="width: auto;">
			              <span id="nav-search-in-content" style="width: auto; overflow: visible;">
			                <input id="treeSel" type="text" readonly value="全部分类" onclick="showMenu();" />
			              </span>
			              <span class="nav-down-arrow"></span>
			            </span>
			            <div class="nav-searchfield-width">
			              <div id="nav-iss-attach">
			                <input type="text" name="searchText" value="${searchText }" title="输入要搜索的内容..." id="twotabsearchtextbox">
			              </div>
			            </div>
		        	</form>
		        </div>
       		</div></div>
       		<!-- 检索结果 -->
       		<div align="center">
       			<div id="cont"></div>
       		</div>
       		<div class="aa" style="margin-left:5px" >
				<table class=" " aline="left" width="100%" border=0 cellspacing="0" cellpadding="0" >
					<tr id="fanye" >
						<c:choose>
							<c:when test="${pageBean[\"PAGES\"] > 1}">
								<td><p style="color: #3366CC;font-weight: bold;">当前第 ${pageBean["CURRENTPAGE"]} 页，共 ${pageBean["PAGES"]} 页，每页${pageBean["PAGESIZE"]} 行，共${pageBean["ROWCOUNT"]} 行</p></td>
								<td id="pagination" class="fenye pagination" ></td>
							</c:when>
							<c:otherwise>
								<c:if test="${pageBean[\"ROWCOUNT\"] != 0}">
									<td><p>当前第 1 页，共 1 页，每页${pageBean["PAGESIZE"]} 行，共${pageBean["ROWCOUNT"]}  行</p></td>
								</c:if>
							</c:otherwise>
						</c:choose>
					</tr>
				</table>
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