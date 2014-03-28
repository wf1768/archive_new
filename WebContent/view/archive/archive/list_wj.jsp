<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%-- <%@ include file="/view/common/top_second_menu.jsp"%> --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
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
		
		$.unblockUI();
	});

	function callback() {
		$(".scrollTable").height($(".scrollTable").height() - $("#aj").height());
		
		var n = $(".scrollTable").height()-$(".aa").height();
		$('.data_table').fixHeader({
			height : n
			
		});
		
		$('#checkall').click(function(){
		    $('input[name="checkbox"]').attr("checked",this.checked);
		});
		
		$('input[type="checkbox"]').removeAttr("checked");
		
		$('.shiftCheckbox').shiftcheckbox();
	}
	function pageselectCallback(page_index, jq){
		var searchTxt = "${searchTxt }";
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			window.location.href="${pageContext.request.contextPath }/archive/list.do?selectid=${selectid}&parentid=${parentid}&page_aj=${page_aj}&searchTxt_aj=${searchTxt_aj }&page="+page_index+"&searchTxt="+searchTxt+"&tabletype=02";
		}
	}; 

	function refresh() {
		window.location.reload(true);
	}
	
	function setshow(templetid,tabletype) {
		if (templetid == "") {
			alert("请选择左侧父档案树节点，再设置显示设置。");
			return;
		}
		var url = "${pageContext.request.contextPath}/archive/setshow.do?templetid=" + templetid + "&tabletype="+tabletype+"&time=" + Date.parse(new Date());
		var whObj = {
			width : 850,
			height : 600
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	
	
	//=========以下是其他页面，完事时删除

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
	
	function search() {
		var pageno = ${pagebean.pageNo };
		var searchTxt = $("#searchTxt").val();
		
		window.location.href="${pageContext.request.contextPath }/archive/list.do?selectid=${selectid}&parentid=${parentid}&page_aj=${page_aj}&searchTxt_aj=${searchTxt_aj }&page="+pageno+"&searchTxt="+searchTxt+"&tabletype=02";
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
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:5555; ">
			<div class="dqwz_l">当前位置：档案管理
			<c:if test="${not empty treename}">
				-${treename }-文件级
			</c:if>
			</div>
			<div class="caozuoan">
				<input type="button" value="添加" class="btn" onClick="add('FT')" />
				<input type="button" value="删除" class="btn" onClick="add('W')" />
				<input type="button" value="显示设置" class="btn" onClick="setshow('${templet.id}','02')" />
				<input type="button" value="刷新" class="btn" onClick="refresh()" />
				<input type="text" id="searchTxt" value="${searchTxt }" onKeyDown="javascript:if (event.keyCode==13) {search();}" />
				<input type="button" value="查询" class="btn" onClick="search()" />
			</div>
			<div style="clear: both"></div>
		</div>
		<table id="aj" class="table-Kang" style="margin-left: 5px;position:relative;z-index:5555;" aline="left" width="98%" border=0 cellspacing="1" cellpadding="4">
			<thead>
				<tr class="tableTopTitle-bg">
					<td width="40px">行号</td>
					<c:forEach items="${ajFieldList}" varStatus="i" var="item">
						<c:if test="${(item.sort > 0) and (item.isgridshow == 1)}">
							<td>${item.chinesename }</td>
						</c:if>
					</c:forEach>
					<td>操作</td>
				</tr>
			</thead>
			<tbody>
				<tr class="table-SbgList">
					<c:forEach items="${maps}" varStatus="i" var="archiveitem">
							<td>${i.index+1 }</td>
							<c:forEach items="${ajFieldList}" varStatus="j" var="fielditem">
								<c:if test="${(fielditem.sort > 0) and (fielditem.isgridshow == 1)}">
								<td title="${archiveitem[fielditem.englishname] }">
								<c:choose>
									<c:when test="${fielditem.fieldtype =='VARCHAR' }">
										<c:set var="subStr" value="${archiveitem[fielditem.englishname]}"></c:set>
										<c:choose>
											<c:when test="${fn:length(subStr) > subString }">
												${fn:substring(archiveitem[fielditem.englishname], 0, subString)}..
											</c:when>
											<c:otherwise>
										      	${archiveitem[fielditem.englishname]}
										    </c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										${archiveitem[fielditem.englishname]}
									</c:otherwise>
								</c:choose>
									
								</td>
								</c:if>
							</c:forEach>
					</c:forEach>
					<td>
						<a href="javascript:;" onclick="window.location.href='${pageContext.request.contextPath }/archive/list.do?selectid=${selectid}&page=${page_aj}&searchTxt=${searchTxt_aj }'" class="juse">
							<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/arrow_undo.png" />
							返回案卷
						</a>
					</td>
				</tr>
			</tbody>
		</table>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			<table id="data_table" class="data_table table-Kang" aline="left" width="98%"
				border=0 cellspacing="1" cellpadding="4">
				<thead>
					<tr class="tableTopTitle-bg">
						<td width="30px"><input type="checkbox" id="checkall"></td>
						<td width="40px">行号</td>
						<td width="40px">全文</td>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isgridshow == 1)}">
								<td>${item.chinesename }</td>
							</c:if>
						</c:forEach>
						<td width="180px">操作</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pagebean.list}" varStatus="i" var="archiveitem">
						<tr class="table-SbgList">
							<td name="${archiveitem.id }"><input type="checkbox" name="checkbox" value="${archiveitem.id }" class="shiftCheckbox"></td>
							<td>${pagebean.pageSize*(pagebean.pageNo-1) + i.index+1 }</td>
							<c:choose>
								<c:when test="${archiveitem['isdoc'] == 1 }">
									<td><a title="电子全文" href="javascript:;" onclick=""><img src="${pageContext.request.contextPath }/images/icons/attach.png" ></a></td>
								</c:when>
								<c:otherwise>
									<td></td>
								</c:otherwise>
							</c:choose>
							
							<c:forEach items="${fields}" varStatus="j" var="fielditem">
								<c:if test="${(fielditem.sort > 0) and (fielditem.isgridshow == 1)}">
								<td title="${archiveitem[fielditem.englishname] }">
								<c:choose>
									<c:when test="${fielditem.fieldtype =='VARCHAR' }">
										<c:set var="subStr" value="${archiveitem[fielditem.englishname]}"></c:set>
										<c:choose>
											<c:when test="${fn:length(subStr) > subString }">
												${fn:substring(archiveitem[fielditem.englishname], 0, subString)}..
											</c:when>
											<c:otherwise>
										      	${archiveitem[fielditem.englishname]}
										    </c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										${archiveitem[fielditem.englishname]}
									</c:otherwise>
								</c:choose>
									
								</td>
								</c:if>
							</c:forEach>
							<td>
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
			<table class=" " aline="left" width="100%" border=0 cellspacing="0" cellpadding="0" >
				<tr class="table-botton" id="fanye" >
					<c:choose>
						<c:when test="${pagebean.isPage == true }">
							<td><p>当前第 ${pagebean.pageNo } 页，共 ${pagebean.pageCount } 页，每页 ${pagebean.pageSize } 行，共 ${pagebean.rowCount } 行</p></td>
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