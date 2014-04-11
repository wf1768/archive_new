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
			window.location.href = "${pageContext.request.contextPath}/archive/list.do?treeid=" + nodes.id;
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
			window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&page="+page_index+"&searchTxt="+searchTxt;
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
			width : 900,
			height : 600
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function showWj(id) {
		window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&parentid="+id+"&page_aj=${pagebean.pageNo }&searchTxt_aj=${searchTxt }&tabletype=02";
	}
	
	function add() {
		var treeid = '${selectid}';
	
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再创建档案。');
			return;
		}
		var url = "${pageContext.request.contextPath}/archive/add.do?treeid=" + treeid + "&tabletype=01&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 500
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function search() {
		var pageno = ${pagebean.pageNo };
		var searchTxt = $("#searchTxt").val();
		
		window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&page="+pageno+"&searchTxt="+searchTxt;
	}
	
	function edit(id) {
		
		var treeid = '${selectid}';
		if (treeid == '') {
			alert('请选择左侧档案节点，再编辑档案。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/edit.do?treeid="+treeid+"&tabletype=01&id=" + id + "&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 500
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function show(id) {
		
		var treeid = '${selectid}';
		if (treeid == '') {
			alert('请选择左侧档案节点，再查看档案。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/show.do?treeid="+treeid+"&tabletype=01&id=" + id + "&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 500
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function del() {
		
		var treeid = '${selectid}';
		if (treeid == '') {
			alert('请选择左侧父档案节点，再删除档案。');
			return;
		}
		
		var str = "";
		
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要删除的数据。");
			return;
		}
		str = str.substring(0,str.length-1);
		
		if (confirm("确定要删除选择的档案吗？将同时删除该档案下包含的档案数据、电子全文。请谨慎操作。")) {
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
					url : "${pageContext.request.contextPath}/archive/delete.do",
					type : 'post',
					data : {
						'treeid':treeid,
						'tabletype':'01',
						'ids' : str
					},
					dataType : 'text',
					success : function(data) {
						$.unblockUI();
						alert(data);
					}
				});
				
				window.location.reload(true);
			},200);  
		};
	}
	
	function doc(id) {
		var treeid = '${selectid}';
		if (treeid == '') {
			alert('请选择左侧档案节点，再查看档案电子文件。');
			return;
		}
		
		var str = "";
		if (id != "") {
			str = id;
		}
		else {
			$("input[name='checkbox']:checked").each(function () {
				str+=$(this).val()+ ",";
			});
			
			if (str == "") {
				alert("请先选择要挂接电子文件的档案数据。");
				return;
			}
			str = str.substring(0,str.length-1);
		}
		//判断是单个挂接还是多个。
		var s = str.split(",");// 在每个逗号(,)处进行分解。
		var winW = $(window).width();
		var w = winW * 0.8;
		if (s.length == 1) {
			var whObj = {
				width : 650,
				height : 500
			};
		}
		else {
			var whObj = {
				width : w,
				height : 500
			};
		}
		var url = "${pageContext.request.contextPath}/archive/doc.do?treeid="+treeid+"&tabletype=01&id=" + str + "&time=" + Date.parse(new Date());
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
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
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:999; ">
			<div class="dqwz_l">当前位置：档案管理
			<c:if test="${not empty treename}">
				<c:choose>
					<c:when test="${templet.templettype == 'A' }">
						-${treename }-案卷级
					</c:when>
					<c:otherwise>
						-${treename }-文件级
					</c:otherwise>
				</c:choose>
			</c:if>
			</div>
			<div class="caozuoan">
				<input type="button" value="添加" class="btn" onClick="add()" />
				<input type="button" value="删除" class="btn" onClick="del()" />
				<input type="button" value="设置" class="btn" onClick="setshow('${templet.id}','01')" />
				<input type="button" value="刷新" class="btn" onClick="refresh()" />
				<input type="button" value="挂接" class="btn" onClick="doc('')" />
				<select name="pageselect" onchange="print_(options[selectedIndex].value)" >
					<option value="ajml">打印案卷目录</option>
				</select>
				<select name="pageselect" onchange="test(options[selectedIndex].value)" >
					<option value="import">数据导入</option>
					<option value="export">数据导出</option>
				</select>
				<!-- <select name="pageselect" onchange="self.location.href=options[selectedIndex].value" >
					<option value="http://www.baidu.com">百度</option>
					<option value="http://www.163.com">网易</option>
				</select>  -->
				<input type="text" id="searchTxt" value="${searchTxt }" onKeyDown="javascript:if (event.keyCode==13) {search();}" />
				<input type="button" value="查询" class="btn" onClick="search()" />
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			<table id="data_table" class="data_table table-Kang" aline="left" width="98%"
				border=0 cellspacing="1" cellpadding="4">
				<thead>
					<tr class="tableTopTitle-bg">
						<td width="30px"><input type="checkbox" id="checkall"></td>
						<td width="40px">行号</td>
						<c:if test="${templet.templettype=='A' or templet.templettype == 'P'}">
							<td width="40px">文件级</td>
						</c:if>
						<td width="40px">全文</td>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isgridshow == 1)}">
								<td>${item.chinesename }</td>
							</c:if>
						</c:forEach>
						<td width="100px">操作</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pagebean.list}" varStatus="i" var="archiveitem">
						<tr class="table-SbgList">
							<td><input type="checkbox" name="checkbox" value="${archiveitem.id }" class="shiftCheckbox"></td>
							<td>${pagebean.pageSize*(pagebean.pageNo-1) + i.index+1 }</td>
							<c:if test="${templet.templettype=='A' or templet.templettype == 'P'}">
								<td><a title="文件级" href="javascript:;" onclick="showWj('${archiveitem.id}')"><img src="${pageContext.request.contextPath }/images/icons/page.png" ></a></td>
							</c:if>
							<c:choose>
								<c:when test="${archiveitem['isdoc'] == 1 }">
									<td><a title="电子全文" href="javascript:;" onclick="doc('${archiveitem.id }')"><img src="${pageContext.request.contextPath }/images/icons/attach.png" ></a></td>
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
								<a href="javascript:;" onclick="show('${archiveitem.id }')">
									<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/application_view_list.png" />
									详细
							    </a>
								<a href="javascript:;" onclick="edit('${archiveitem.id }')">
									<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/application_form_edit.png" />
									修改
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