<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/picmov.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropmenu/style.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dropmenu/dropmenu.js"></script>

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
		
		/**************************************************
	     * Context-Menu with Sub-Menu
	     **************************************************/
	    /* $.contextMenu({
	        selector: '.scrollTable', 
	        callback: function(key, options) {
	        },
	        items: {
	        	"add": {
	        		name:"添加",
	        		icon:"add",
	        		callback: function(key, options) {
	        			add();
	                }
	        	},
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
	                    "fold1a-key1": {
	                    	name: "只文件级",
	                    	callback: function(key, options) {
	                    		allwj();
	    	                }
	                    },
	                    "fold1a-key2": {
	                    	name: "批量修改",
	                    	callback: function(key, options) {
	                    		update_multiple();
	    	                }
	                    },
	                    "fold1a-key3": {
	                    	name: "Excel导入"
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
	        			setshow('${templet.id}','01');
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
	    }); */
	});

	function callback() {
		var n = $(".scrollTable").height()-$(".aa").height();
		/* $('.data_table').fixHeader({
			height : n
		}); */
		
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

	function refresh() {
		jscroll('body-wrapper');
		window.location.reload(true);
	}
	
	function setshow(templetid,tabletype) {
		jscroll('body-wrapper');
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
		jscroll('body-wrapper');
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
	
	function searchArchive() {
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再检索档案。');
			return;
		}
		//var pageno = ${pagebean.pageNo };
		var searchTxt = $("#searchTxt").val();
		
		window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&searchTxt="+searchTxt;
	}
	
	function edit(id) {
		jscroll('body-wrapper');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
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
		jscroll('body-wrapper');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
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
	
	function del(id) {
		jscroll('xiangce');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再删除档案。');
			return;
		}
		
		var str = "";
		if (id=="") {
			$("input[name='checkbox']:checked").each(function () {
				str+=$(this).val()+ ",";
			});
			
			if (str == "") {
				alert("请先选择要删除的数据。");
				return;
			}
			str = str.substring(0,str.length-1);
		}
		else {
			str = id;
		}
		
		if (confirm("确定要删除选择的多媒体相册吗？将同时删除该相册下包含的多媒体文件数据。请谨慎操作。")) {
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
		jscroll('body-wrapper');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
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
	
	function openprint() {
		/* $("#bodyer_right").jqprint(); */
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再打印。');
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
			width : 750,
			height : 500
		};
		
		var url = "${pageContext.request.contextPath}/archive/openprint.do?treeid="+treeid+"&tabletype=01&ids=" + str + "&searchTxt=${searchTxt }&time=" + Date.parse(new Date());
		var result = openShowModalDialog(url, window, whObj);
		//window.location.reload(true);
	}
	
	function allwj() {
		var treeid = '${selectid}';
		
		var templettype = '${templet.templettype }';
		
		if (templettype != 'A' && templettype != 'P' ) {
			alert("纯文件级档案类型，已经是全部文件了，不能查看全文件级。");
			return;
		}
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再查看文件级。');
			return;
		}
		var str = "";
		
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str != "") {
			str = str.substring(0,str.length-1);
		}
		
		window.location.href = "${pageContext.request.contextPath}/archive/list.do?treeid=${selectid}&allwj=true&parentid="+str+"&tabletype=02";
	}
	
	function update_multiple() {
		jscroll('body-wrapper');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
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
		
		var url = "${pageContext.request.contextPath}/archive/edit.do?treeid="+treeid+"&tabletype=01&id=" + str + "&multiple=true&time=" + Date.parse(new Date());
		var whObj = {
			width : 850,
			height : 600
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function datacopy() {
		
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再复制档案数据。');
			return;
		}
		
		var str = "";
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要复制的档案数据。");
			return;
		}
		
		if (str != "") {
			str = str.substring(0,str.length-1);
		}
		
		$.ajax({
			async : false,
			url : "${pageContext.request.contextPath}/archive/datacopy.do",
			type : 'post',
			data : {
				'treeid':treeid,
				'tabletype':'01',
				'ids' : str
			},
			dataType : 'text',
			success : function(data) {
				alert(data);
				refresh();
			}
		});
	}
	
	function datapaster() {
		var ids = '${sessionScope.CURRENT_DATA_COPY_SESSION }';
		if (ids == "") {
			alert("请先选择要粘贴的档案数据。");
			return;
		}
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再粘贴档案数据。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/opendatapaster.do?targetTreeid="+treeid+"&targetTabletype=01&time=" + Date.parse(new Date());
		var whObj = {
			width : 850,
			height : 600
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function archiveImport() {
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再导入档案数据。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/importArchive.do?treeid="+treeid+"&tabletype=01&time=" + Date.parse(new Date());
		var whObj = {
			width : 850,
			height : 600
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function archiveExport() {
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再导入档案数据。');
			return;
		}
		
		var str = "";
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str != "") {
			str = str.substring(0,str.length-1);
		}
		
		var link = "${pageContext.request.contextPath}/archive/exportArchive.do?treeid="+treeid+"&tabletype=01&ids="+str+"&time=" + Date.parse(new Date());
        window.location.href=link;
        return false;
		
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
					<c:when test="${templet.templettype == 'P' }">
						-${treename }-多媒体相册
					</c:when>
					<c:otherwise>
						-${treename }-多媒体文件
					</c:otherwise>
				</c:choose>
			</c:if>
			</div>
			<div class="caozuoan">
				<div style="float: right;margin-top: 3px;margin-left: 5px">
					<input type="text" id="searchTxt" value="${searchTxt }" onKeyDown="javascript:if (event.keyCode==13) {searchArchive();}" />
					<a href="javascript:;" class="btn" onclick="searchArchive()">查询</a>
				</div>
				<div style="float: right;margin-top: 8px;">
					<ul id="sddm" style="width: 250px">
						<li><a href="javascript:;" onclick="setshow('${templet.id}','01')" onmouseout="mclosetime()">设置</a></li>
						<li><a href="javascript:;" onmouseover="mopen('m1')" onmouseout="mclosetime()">数据操作</a>
							<div id="m1" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
								<a href="javascript:;" onclick="archiveImport()">Excel导入</a>
								<a href="javascript:;" onclick="archiveExport()">导出Excel</a>
							</div>
						</li>
						<li><a href="javascript:;" onclick="add()" onmouseout="mclosetime()">添加</a></li>
					</ul>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			<div class="xiangce" style="overflow: auto;">
            	<ul>
            		<c:forEach items="${pagebean.list}" varStatus="i" var="archiveitem">
					<li>
						<div class="photo">
							<a title="打开相册" href="javascript:;" onclick="showWj('${archiveitem.id}')">
							<c:set var="slt" value="${archiveitem.slt}"></c:set>
							<c:choose>
								<c:when test="${fn:length(slt) == 0 }">
									<img src="${pageContext.request.contextPath}/images/no_photo_135.png" height="150" width="200"/>
								</c:when>
								<c:otherwise>
									<img src="${pageContext.request.contextPath}/${archiveitem.slt}" height="150" width="200"/>
								</c:otherwise>
							</c:choose>
							</a>
						</div>
						<div>
							<div class="miaoshu" title="${archiveitem.tm}">
								<span>
									<c:set var="subStr" value="${archiveitem.tm}"></c:set>
									<c:choose>
										<c:when test="${fn:length(subStr) > 12 }">
											${fn:substring(archiveitem.tm, 0, 12)}..
										</c:when>
										<c:otherwise>
									      	${archiveitem.tm }
									    </c:otherwise>
									</c:choose>
								</span>
							</div>
							<div class="xuanze_btn">
								<a href="javascript:;" onclick="edit('${archiveitem.id }')"><img src="${pageContext.request.contextPath}/images/xiangce_03.gif" width="29" height="28" /></a>
								<a href="javascript:;" onclick="del('${archiveitem.id }')"><img src="${pageContext.request.contextPath}/images/xiangce_05.jpg" width="29" height="28" /></a>
							</div>
							<div style="clear: both"></div>
						</div>
						<div style="clear: both"></div>
					</li>
					</c:forEach>
              </ul>
            </div>
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
