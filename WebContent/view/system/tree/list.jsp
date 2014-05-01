<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>


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
		if (nodes.treetype != 'F' && nodes.treetype != 'FT') {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			treeObj.expandNode(nodes);
		} else {
			window.location.href = "${pageContext.request.contextPath}/tree/list.do?selectid=" + nodes.id;
		}
	};

	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting, nodes);

		var treeid = "${selectid}";
		selectTreeid = treeid;
		selectNode(treeid);
	});
	
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
		refresh();
	}
	
	function edit(id) {
		var url = "${pageContext.request.contextPath}/tree/edit.do?id=" + id + "&time=" + Date.parse(new Date());
		var whObj = {
			width : 550,
			height : 300
		};
		var result = openShowModalDialog(url, window, whObj);
		refresh();
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
		refresh();
	}
	
	function move(id) {
		if (id=="") {
			alert("请先选择要移动的档案档案节点。");
			return;
		}
		var url = "move.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
		refresh();
	}
</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img
					src="${pageContext.request.contextPath}/images/i1_03.png"
					width="29" height="22" class="tubiao" /> <span>档案树管理</span> </a>
			</dt>
			<dd>
				<ul id="treeDemo" class="ztree"></ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:999; ">
			<div class="dqwz_l">当前位置：系统配置－档案树管理</div>
			<div class="caozuoan">
				<input type="button" value="添加档案树夹" class="btn" onClick="add('FT')" />
				<input type="button" value="添加档案树节点" class="btn" onClick="add('W')" />
				<input type="button" value="刷新" class="btn" onClick="refresh()" />
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			<table id="data_table" class="data_table table-Kang" id="tb1"
				align="center" width="96%" border=0 cellspacing="1" cellpadding="4">
				<thead>
					<tr class="tableTopTitle-bg">
						<td>序号</td>
						<td>档案树名称</td>
						<td>类型</td>
						<td>排序</td>
						<td>操作</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${trees}" varStatus="i" var="item">
						<tr class="table-SbgList">
							<td>${i.index+1 }</td>
							<td>${item.treename}</td>
							<td><c:choose>
									<c:when test="${item.treetype=='W' }">
										<font color="blue">档案树节点</font>
									</c:when>
									<c:when test="${item.treetype=='FT' }">
										档案节点夹
									</c:when>
								</c:choose>
							</td>
							<td><a href="javascript:;" onclick="sort('${item.id}')">${item.sort}</a></td>
							<td>
								<a href="javascript:;" onclick="sort('${item.id}')">
									<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/up.png" />
									排序
								</a>
								<a href="javascript:;" onclick="edit('${item.id}')"
									class="juse"> <img style="margin-bottom: -3px"
										src="${pageContext.request.contextPath}/images/icons/application_form_edit.png" />
										修改
								</a>
								<a href="javascript:;" onclick="del('${item.id}','${item.treetype }')"
									class="juse"> <img style="margin-bottom: -3px"
										src="${pageContext.request.contextPath}/images/icons/application_form_delete.png" />
										删除
								</a>
								<a href="javascript:;" onclick="move('${item.id}')">
								<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/application_view_list.png" />
								移动
						    </a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

		</div>
		<div class="aa" style="margin-left:5px" >
			<table class=" " aline="left" width="100%" 
				 border=0 cellspacing="0" cellpadding="0" >
				<tr class="table-botton" id="fanye" >
					<td colspan="14"><p>当前第 1 页，共 1 页，共 ${fn:length(trees) } 行</p></td>
					<td colspan="14" class="fenye" ></td>
				</tr>
			</table>
		</div>
	</div>
	<div style="clear: both"></div>
</div>

<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>