<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%-- <%@ include file="/view/common/top_second_menu.jsp"%> --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropmenu/style.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<!-- 分页插件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/pagination/pagination.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pagination/jquery.pagination.js"></script>

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
			callback: {
				onClick: onClick
			}
	};
	var nodes = ${result };
	
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
			//window.location.href = "${pageContext.request.contextPath}/archive/list.do?treeid=" + nodes.id;
		}
	};
	$(function() {
		var total = ${pagebean.rowCount };
		var pagesize = ${pagebean.pageSize };
		var pageno = ${pagebean.pageNo };
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
		
		$.fn.zTree.init($("#treeDemo"), setting, nodes);

		var treeid = "${selectid}";
		selectTreeid = treeid;
		selectNode(treeid);
	});

	function callback() {
		var n = $(".scrollTable").height()-$(".aa").height();
		$('.data_table').fixHeader({
			height : n
		});
		
		$('#checkall').click(function(){
		    $('input[name="checkbox"]').attr("checked",this.checked);
		    if (this.checked) {
		    	$('input[name="checkbox"]').parents('tr').addClass('selected');
		    }
		    else {
		    	$('input[name="checkbox"]').parents('tr').removeClass("selected");
		    }
		});
		$('input[type="checkbox"]').removeAttr("checked");
		
		$('input[name="checkbox"]').click(function(){
		    if (this.checked) {
		    	$(this).parents('tr').addClass('selected');
            }  
            else {  
            	$(this).parents('tr').removeClass("selected");
            }
		});
		$('.shiftCheckbox').shiftcheckbox();
		
		var jscroll = getCookie('jscroll');
		$('.body-wrapper').scrollTop(jscroll);
		delCookie('jscroll');//删除cookie
		
	}
	
	function pageselectCallback(page_index, jq){
		var searchTxt = "${searchTxt }";
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&page="+page_index+"&searchTxt="+searchTxt;
		}
	}; 

</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img
					src="${pageContext.request.contextPath}/images/i1_03.png"
					width="29" height="22" class="tubiao" /> <span>高级检索</span> </a>
			</dt>
			<dd>
				<ul id="treeDemo" class="ztree"></ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:999; ">
			<div class="caozuoan">
					
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			
		</div>
		<div class="aa" style="margin-left:5px" >
			<table class=" " aline="left" width="100%" border=0 cellspacing="0" cellpadding="0" >
				<tr id="fanye" >
					<c:choose>
						<c:when test="${pagebean.isPage == true }">
							<td><p style="color: #3366CC;font-weight: bold;">当前第 ${pagebean.pageNo } 页，共 ${pagebean.pageCount } 页，每页 ${pagebean.pageSize } 行，共 ${pagebean.rowCount } 行</p></td>
							<td id="pagination" class="fenye pagination" ></td>
						</c:when>
						<c:otherwise>
							<td><p>当前第 1 页，共 1 页，每页 ${pagebean.rowCount } 行，共 ${pagebean.rowCount } 行</p></td>
							<td  ></td>
						</c:otherwise>
					</c:choose>
					
				</tr>
			</table>
		</div>
	</div>
	<div style="clear: both"></div>
</div>

<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>
