<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%-- <%@ include file="/view/common/top_second_menu.jsp"%> --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropmenu/style.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dropmenu/dropmenu.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jqprint-0.3.js"></script>
<!-- 分页插件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/pagination/pagination.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pagination/jquery.pagination.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery.contextMenu/jquery.contextMenu.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.contextMenu/jquery.contextMenu.js"></script>
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
		
		/**************************************************
	     * Context-Menu with Sub-Menu
	     **************************************************/
	    $.contextMenu({
	        selector: '.scrollTable', 
	        callback: function(key, options) {
	            var m = "clicked: " + key;
	            window.console && console.log(m) || alert(m); 
	        },
	        items: {
	        	"del": {
	        		name:"删除",
	        		icon:"delete",
	        		callback: function(key, options) {
	        			del();
	                }
	        	},
	        	"data": {
	                "name": "数据操作", 
	                "items": {
	                    "fold1a-key2": {
	                    	name: "批量修改",
	                    	callback: function(key, options) {
	                    		update_multiple();
	    	                }
	                    },
	                    "fold1a-key4": {
	                    	name: "导出Excel"
	                    },
	                    "fold1a-key5": {
	                    	name: "数据移动"
	                    }
	                }
	            },
	        	"sep1": "---------",
	        	"setshow":{
	        		name:"设置",
	        		icon:"cog",
	        		callback: function(key, options) {
	        			setshow('${templet.id}','02');
	                }
	        	},
	        	"link":{
	        		name:"挂接",
	        		icon:"attach",
	        		callback:function(key,options) {
	        			doc("");
	        		}
	        	},
	        	"print":{
	        		name:"打印",
	        		icon:"print",
	        		callback:function(key,options) {
	        			openprint();
	        		}
	        	}
	        }
	    });
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
	}
	function pageselectCallback(page_index, jq){
		var searchTxt = "${searchTxt }";
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&allwj=true&parentid=${parentid}&tabletype=02&page="+page_index+"&searchTxt="+searchTxt;
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
		//var pageno = ${pagebean.pageNo };
		var searchTxt = $("#searchTxt").val();
		
		window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&allwj=true&parentid=${parentid}&tabletype=02&searchTxt="+searchTxt;
	}
	
	function edit(id) {
		
		var treeid = '${selectid}';
		if (treeid == '') {
			alert('请选择左侧档案节点，再编辑档案。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/edit.do?treeid="+treeid+"&tabletype=02&id=" + id + "&time=" + Date.parse(new Date());
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
		
		var url = "${pageContext.request.contextPath}/archive/show.do?treeid="+treeid+"&tabletype=02&id=" + id + "&time=" + Date.parse(new Date());
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
						'tabletype':'02',
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
		var url = "${pageContext.request.contextPath}/archive/doc.do?treeid="+treeid+"&tabletype=02&id=" + str + "&time=" + Date.parse(new Date());
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function openprint() {
		/* $("#bodyer_right").jqprint(); */
		
		var treeid = '${selectid}';
		if (treeid == '') {
			alert('请选择左侧档案节点，再查看档案电子文件。');
			return;
		}
		
		var str = "";
		
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str != "") {
			str = str.substring(0,str.length-1);
		}
		
		var whObj = {
			width : 650,
			height : 500
		};
		
		var url = "${pageContext.request.contextPath}/archive/openprint.do?treeid="+treeid+"&parentid=${parentid}&tabletype=02&ids=" + str + "&searchTxt=${searchTxt }&time=" + Date.parse(new Date());
		var result = openShowModalDialog(url, window, whObj);
		//window.location.reload(true);
	}
	
	function update_multiple() {
		
		var treeid = '${selectid}';
		if (treeid == '') {
			alert('请选择左侧档案节点，再编辑档案。');
			return;
		}
		
		var str = "";
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要批量编辑的数据。");
			return;
		}
		
		if (str != "") {
			str = str.substring(0,str.length-1);
		}
		
		var url = "${pageContext.request.contextPath}/archive/edit.do?treeid="+treeid+"&tabletype=02&id=" + str + "&multiple=true&time=" + Date.parse(new Date());
		var whObj = {
			width : 850,
			height : 600
		};
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
			<div class="dqwz_l">当前位置：档案管理-${treename }-案卷下全部文件级</div>
			<div class="caozuoan">
				<div style="float: left;">
				<ul id="cssdropdown">
					<li class="headlink"><a href="javascript:;" onclick="del()">删除</a></li>
					<li class="headlink"><a href="javascript:;">数据操作</a>
						<ul>
							<li><a href="javascript:;" onclick="update_multiple()">批量修改</a></li>
							<li><a href="javascript:;">导出Excel</a></li>
							<li><a href="javascript:;">数据移动</a></li>
						</ul>
					</li>
					<li class="headlink"><a href="javascript:;" onclick="setshow('${templet.id}','02')">设置</a></li>
					<li class="headlink"><a href="javascript:;" onclick="doc('')">挂接</a></li>
					<li class="headlink"><a href="javascript:;" onclick="openprint()">打印</a></li>
				</ul>
				</div>
				<div style="float: right;margin-top: 3px;margin-left: 5px">
					<input type="text" id="searchTxt" value="${searchTxt }" onKeyDown="javascript:if (event.keyCode==13) {search();}" />
				<!-- <input type="button" value="查询" class="btn" onClick="searchData()" /> -->
					<a href="javascript:;" class="btn" onclick="search()">查询</a>
				</div>
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
