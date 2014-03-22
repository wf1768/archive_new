<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/view/common/header_2.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table1.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>
<!-- 分页插件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/pagination/pagination.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pagination/jquery.pagination.js"></script>

<script>
	$.blockUI({
		message:"正在进行加载，请稍候...",
		css: {
	        padding: '15px',
	        width:"300px"
	    } 
	}); 
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
			window.location.href = "${pageContext.request.contextPath}/archive/list.do?selectid=" + nodes.id;
		}
	};
	
	function createData() {
		var fieldjson = ${fieldjson};
		var archivejson = ${archivejson};
		/* //var tabletitle = $("#tabletitle");
		$("#tabletitle").html("");
		$("#tabletitle").append("<td width='30px'><input type='checkbox' id='checkall'></td>");
		$("#tabletitle").append("<td>序号11</td>");
		if (archivejson.length > 0) {
			
			for (var i=0;i<fieldjson.length;i++) {
				if (fieldjson[i].sort > 0) {
					var field = "<td>" + fieldjson[i].chinesename + "</td>";
					$("#tabletitle").append(field);
				}
			}
		}11
		$("#tabletitle").append("<td width=\"180px\">操作</td>"); */
		
		$("#tabledata").html("");
		if (archivejson.length > 0) {
			for (var i=0;i<archivejson.length;i++) {
				var row = "<tr class=\"table-SbgList\">";
				row += "<td><input type=\"checkbox\" name=\"checkbox\" value=\""+archivejson[i].id+"\" class=\"shiftCheckbox\"></td>";
				row += "<td>${pagebean.pageSize*(pagebean.pageNo-1) } + 1</td>";
				for (var j=0;j<fieldjson.length;j++) {
					if (fieldjson[j].sort > 0) {
						var data = "<td>" + archivejson[i][fieldjson[j].englishname] + "</td>";
						row += data;
					}
				}
				row += "<td></td>";
				row += "</tr>";
				$("#tabledata").append(row);
			}
		}
	}
	$(function() {
		createData();
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
		
		$('#checkall').click(function(){
		    $('input[name="checkbox"]').attr("checked",this.checked);
		});
		
		$('input[type="checkbox"]').removeAttr("checked");
		
		$('.shiftCheckbox').shiftcheckbox();
		
		$.unblockUI();
		
	});
	
	function pageselectCallback(page_index, jq){
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			window.location.href="${pageContext.request.contextPath }/archive/list.do?selectid=${selectid}&page="+page_index+"";
		}
	}; 

	function refresh() {
		window.location.reload(true);
	}

	function add(treetype) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (typeof (nodes[0]) == "undefined") {
			alert("请选择左侧父档案夹，再创建档案类型夹。");
			return;
		}
		if (nodes[0].treetype != 'F' && nodes[0].treetype != 'FT') {
			alert("请选择左侧档案类型下，再创建档案树或档案夹。");
			return;
		}
		var url = "${pageContext.request.contextPath}/tree/add.do?parentid=" + nodes[0].id + "&treetype="+treetype+"&time=" + Date.parse(new Date());
		var whObj = {
			width : 550,
			height : 300
		};
		var result = openShowModalDialog(url, window, whObj);
	}
	
	function edit(id) {
		var url = "${pageContext.request.contextPath}/tree/edit.do?id=" + id + "&time=" + Date.parse(new Date());
		var whObj = {
			width : 550,
			height : 300
		};
		var result = openShowModalDialog(url, window, whObj);
	}
	
	function del(id,type) {
		if (id == "") {
			alert("没有获得要删除的数据，请重新尝试，或与管理员联系。");
			return;
		}
		var str = "";
		if (type == "FT") {
			str = "确定要删除选择的档案节点夹吗？将同时删除该档案节点夹下包含的所有档案，及档案数据、电子全文。请谨慎操作。"
		}
		else {
			str = "确定要删除选择的档案节点吗？将同时删除该档案节点下包含的所有档案数据、电子全文。请谨慎操作。"
		}
		
		if (confirm(str)) {
			$.blockUI({
				message:"正在进行删除，请稍候...",
				css: {
                    padding: '15px',
                    width:"300px"
                } 
            }); 
			setTimeout(function () {
				$.ajax({
					async : false,
					url : "${pageContext.request.contextPath}/tree/delete.do",
					type : 'post',
					data : {
						id : id
					},
					dataType : 'text',
					success : function(data) {
						$.unblockUI();
						if (data == "success") {
							alert("删除完毕。");
						} else if(data == "error") {
							alert("当前删除的档案节点夹下，还有档案节点夹或档案节点，请先删除下级。");
						} else {
							alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
						}
						
						window.location.reload(true);
					},
					error: function (err) {  
						$.unblockUI();
	                    alert("error:" + err);  
	                    return false;  
	                }
				});
			},100)
		};
	}
	
	function sort(id) {
		if (id=="") {
			alert("请先选择要排序的档案节点。");
			return;
		}
		var url = "sort.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 300 };
		var result = openShowModalDialog(url,window,whObj);
	}
	
	function move(id) {
		if (id=="") {
			alert("请先选择要移动的档案档案节点。");
			return;
		}
		var url = "move.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
	}
</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img
					src="${pageContext.request.contextPath}/images/i1_03.png"
					width="29" height="22" class="tubiao" /> <span>档案管理</span> </a>
			</dt>
			<dd>
				<ul id="treeDemo" class="ztree"></ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="top_dd" style="margin-bottom: 10px">
			<div class="dqwz_l">当前位置：档案管理－档案管理</div>
			<div class="caozuoan">
				<input type="button" value="添加档案树夹" class="btn" onClick="add('FT')" />
				<input type="button" value="添加档案树节点" class="btn" onClick="add('W')" />
				<input type="button" value="刷新" class="btn" onClick="refresh()" />
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			<table id="data_table" class="data_table table-Kang" aline="left" width="98%" 
				border=0 cellspacing="1" cellpadding="4">
				<thead>
					<tr id="tabletitle" class="tableTopTitle-bg">
						<td width="30px"><input type="checkbox" id="checkall"></td>
						<td>序号</td>
						
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${item.sort > 0 }">
								<td>${item.chinesename }</td>
							</c:if>
						</c:forEach>
						<td width="180px">操作</td>
					</tr>
				</thead>
				<tbody id="tabledata">
					
				</tbody>
			</table>
		</div>
		<div class="aa" style="margin-left:5px" >
			<table class=" " aline="left" width="100%" border=0 cellspacing="0" cellpadding="0" >
				<tr class="table-botton" id="fanye" >
					<td><p>当前第 ${pagebean.priorNo } 页，共 ${pagebean.pageCount } 页，每页 ${pagebean.pageSize } 行，共 ${pagebean.rowCount } 行</p></td>
					<td id="pagination" class="fenye pagination" ></td>
				</tr>
			</table>
		</div>

	</div>
	<div style="clear: both"></div>
</div>

<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>