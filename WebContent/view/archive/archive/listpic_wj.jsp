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

<!-- 右键 -->
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
	        selector: '.pic_li', 
	        callback: function(key, options) {
	        	/* alert($(this).attr("id")); */
	        },
	        items: {
	        	"edit": {
	        		name:"编辑",
	        		icon:"edit",
	        		callback: function(key, options) {
	        			edit($(this).attr("id"));
	                }
	        	},
	        	"show": {
	        		name:"详细",
	        		icon:"",
	        		callback: function(key, options) {
	        			show($(this).attr("id"));
	                }
	        	},
	        	"setCover": {
	        		name:"设为相册封面",
	        		icon:"cog",
	        		callback: function(key, options) {
	        			setCover($(this).attr("id"));
	                }
	        	},
	        	"sep1": "---------",
	        	"upload": {
	        		name:"上传",
	        		icon:"attach",
	        		callback: function(key, options) {
	        			upload_pic_single($(this).attr("id"));
	                }
	        	},
	        	"download": {
	        		name:"下载",
	        		icon:"attach",
	        		callback: function(key, options) {
	        			down_pic($(this).attr("id"));
	                }
	        	},
	        	"del": {
	        		name:"删除",
	        		icon:"delete",
	        		callback: function(key, options) {
	        			del($(this).attr("id"));
	                }
	        	},
	        	"sep1": "---------",
	        	"setshow":{
	        		name:"设置",
	        		icon:"cog",
	        		callback: function(key, options) {
	        			setshow('${templet.id}','02');
	                }
	        	}
	        }
	    });
	});

	function callback() {
		var n = $(".scrollTable").height()-$(".aa").height();
		/* $('.data_table').fixHeader({
			height : n
		}); */
		
		var jscroll = getCookie('xiangce_wj');
		$('.xiangce').scrollTop(jscroll);
		delCookie('xiangce_wj');//删除cookie
		
		
		$('.tip').mouseover(function(e){
			var img = new Image();
			img.src =this.src ;
			var h = img.height;
			
			if (h > 300) {
				h = h/2;
			}
			var $tip=$('<div id="tip"><div class="t_box"><div><s><i></i></s><img height="'+h+'" id="tipImg" src="'+this.src+'" /></div></div></div>');
			
			$('body').append($tip);
			$('#tip').show('fast');
			
			var imgHeight = $('#tipImg').height();
			//var oTop = $('#tip').offset().top;
			//var oHeight = $('#tip').height();
			var bheight = $('body').height();
			
			var imgWidth = $('#tipImg').width();
			var bWidth = $('body').width();
			
			var t = (bheight - e.pageY);
			var x = (bWidth - e.pageX);
			
			if (imgHeight > t) {
				var bb = imgHeight - t;
				$('#tip').css({"top":(e.pageY - bb)+"px","left":(e.pageX+30)+"px"});
			}
			else {
				$('#tip').css({"top":(e.pageY-60)+"px","left":(e.pageX+30)+"px"});
			}
			
			if (imgWidth > x) {
				var xx = bWidth - imgWidth - x -30;
				$('#tip').css({"left":xx+"px"});
			}
			
		}).mouseout(function(){
		   $('#tip').remove();
		}).mousemove(function(e){
			var imgHeight = $('#tipImg').height();
			//var oTop = $('#tip').offset().top;
			//var oHeight = $('#tip').height();
			var bheight = $('body').height();
			
			var imgWidth = $('#tipImg').width();
			var bWidth = $('body').width();
			
			var t = (bheight - e.pageY);
			var x = (bWidth - e.pageX);
			
			if (imgHeight > t) {
				var bb = imgHeight - t;
				$('#tip').css({"top":(e.pageY - bb)+"px","left":(e.pageX+30)+"px"});
			}
			else {
				$('#tip').css({"top":(e.pageY-60)+"px","left":(e.pageX+30)+"px"});
			}
			
			if (imgWidth > x) {
				var xx = bWidth - imgWidth - x -30;
				$('#tip').css({"left":xx+"px"});
			}
		})
	}
	
	function pageselectCallback(page_index, jq){
		var searchTxt = "${searchTxt }";
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&parentid=${parentid}&page_aj=${page_aj}&searchTxt_aj=${searchTxt_aj }&page="+page_index+"&searchTxt="+searchTxt+"&tabletype=02";
		}
	};  

	function refresh() {
		jscroll('xiangce','xiangce_wj');
		window.location.reload(true);
	}
	
	function setshow(templetid,tabletype) {
		jscroll('xiangce','xiangce_wj');
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
		jscroll('xiangce','xiangce_wj');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再创建档案。');
			return;
		}
		var url = "${pageContext.request.contextPath}/archive/add.do?treeid=" + treeid + "&parentid=${parentid}&tabletype=02&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 500
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function search() {
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再检索档案。');
			return;
		}
		//var pageno = ${pagebean.pageNo };
		var searchTxt = $("#searchTxt").val();
		
		window.location.href="${pageContext.request.contextPath }/archive/list.do?treeid=${selectid}&parentid=${parentid}&page_aj=${page_aj}&searchTxt_aj=${searchTxt_aj }&searchTxt="+searchTxt+"&tabletype=02";
	}
	
	function edit(id) {
		jscroll('xiangce','xiangce_wj');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
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
	
	function del(id) {
		jscroll('xiangce','xiangce_wj');
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
		
		
		if (confirm("确定要删除选择的多媒体文件吗？请谨慎操作。")) {
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
	
	function show(id) {
		jscroll('xiangce','xiangce_wj');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
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
	
	
	
	function archiveImport() {
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再导入档案数据。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/importArchive.do?treeid="+treeid+"&tabletype=02&parentid=${parentid}&time=" + Date.parse(new Date());
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
		
		var link = "${pageContext.request.contextPath}/archive/exportArchive.do?treeid="+treeid+"&tabletype=02&ids="+str+"&parentid=${parentid}&time=" + Date.parse(new Date());
        window.location.href=link;
        return false;
		
	}
	
	function hideFirstEl(id) {
		document.getElementById(id+"_xlan").style.display="none";
		document.getElementById(id+"_xuanxiang").style.display="none";
	}
	
	function showFirstEl(id) {
		document.getElementById(id+"_xlan").style.display="block";
		document.getElementById(id+"_xuanxiang").style.display="block";
	}
	
	function setCover(id) {
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再操作。');
			return;
		}
		
		if (id == "") {
			alert("请先选择要设置为封面的多媒体文件。");
			return;
		}
		
		$.ajax({
			async : false,
			url : "${pageContext.request.contextPath}/archive/setCover.do",
			type : 'post',
			data : {
				'treeid':treeid,
				'tabletype':'02',
				'id' : id
			},
			dataType : 'text',
			success : function(data) {
				alert(data);
			}
		});
	}
	
	function upload_pic_single(id) {
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再上传多媒体文件。');
			return;
		}
		
		if (id == "") {
			alert('请选择要上传的多媒体文件。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/open_upload_pic_single.do?treeid="+treeid+"&tabletype=02&id="+id+"&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 300
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function upload_pic_multiple(slttype) {
		jscroll('xiangce','xiangce_wj');
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再上传多媒体文件。');
			return;
		}
		
		var whObj = {
				width : 650,
				height : 500
			};
		var url = "${pageContext.request.contextPath}/archive/show_upload_pic_multiple.do?treeid="+treeid+"&tabletype=02&parentid=${parentid}&status=0&slttype="+slttype+"&time=" + Date.parse(new Date());
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function down_pic(id) {
		
		var treeid = '${selectid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再下载多媒体文件。');
			return;
		}
		
		var link = "${pageContext.request.contextPath}/archive/down_pic.do?treeid="+treeid+"&tabletype=02&id="+id+"&time=" + Date.parse(new Date());
        window.location.href=link;
        return false;
	}
	
</script>

<style type="text/css">
#tip   {position:absolute;color:#333;display:none;}
#tip s   {position:absolute;top:40px;left:-20px;display:block;width:0px;height:0px;font-size:0px;line-height:0px;border-color:transparent #BBA transparent transparent;border-style:dashed solid dashed dashed;border-width:10px;}
#tip s i   {position:absolute;top:-10px;left:-8px;display:block;width:0px;height:0px;font-size:0px;line-height:0px;border-color:transparent #fff transparent transparent;border-style:dashed solid dashed dashed;border-width:10px;}
#tip .t_box   {position:relative;background-color:#CCC;filter:alpha(opacity=50);-moz-opacity:0.5;bottom:-3px;right:-3px;}
#tip .t_box div  {position:relative;background-color:#FFF;border:1px solid #ACA899;background:#FFF;padding:1px;top:-3px;left:-3px;}
 
.tip   {border:1px solid #DDD;}
</style>
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
				-${treename }-多媒体文件
			</c:if>
			</div>
			<div class="caozuoan">
				<div style="float: right;margin-top: 3px;margin-left: 5px">
					<input type="text" id="searchTxt" value="${searchTxt }" onKeyDown="javascript:if (event.keyCode==13) {search();}" />
					<a href="javascript:;" class="btn" onclick="search()">查询</a>
				</div>
				<div style="float: right;margin-top: 8px;">
					<ul id="sddm" style="width: 325px">
						<li><a href="javascript:;" onclick="add()" onmouseout="mclosetime()">添加</a></li>
						<li><a href="javascript:;" onmouseover="mopen('m1')" onmouseout="mclosetime()">数据操作</a>
							<div id="m1" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
								<a href="javascript:;" onclick="archiveImport()">Excel导入</a>
								<a href="javascript:;" onclick="archiveExport()">导出Excel</a>
							</div>
						</li>
						<li><a href="javascript:;" onmouseover="mopen('m2')" onmouseout="mclosetime()">批量上传</a>
							<div id="m2" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
								<a href="javascript:;" onclick="upload_pic_multiple('IMAGE')">上传图片</a>
								<a href="javascript:;" onclick="upload_pic_multiple('VIDEO')">上传视频</a>
							</div>
						</li>
						<li><a href="javascript:;" onclick="setshow('${templet.id}','01')" onmouseout="mclosetime()">设置</a></li>
					</ul>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			<div class="xiangce" style="overflow: auto;overflow-x:hidden;">
            	<ul>
            		<c:forEach items="${pagebean.list}" varStatus="i" var="archiveitem">
	            		<%-- <li class="pic_li" id="${archiveitem.id }" onMouseOver="showFirstEl('${archiveitem.id }')" onMouseOut="hideFirstEl('${archiveitem.id }')"> --%>
	            		<li class="pic_li" id="${archiveitem.id }">
		                    <div class="photo22" >
		                    	<c:set var="slt" value="${archiveitem.slt}"></c:set>
		                    	<c:set var="slttype" value="${archiveitem.slttype}"></c:set>
								<c:choose>
									<c:when test="${fn:length(slt) == 0 }">
										<img class="tip" style="z-index:1;" src="${pageContext.request.contextPath}/images/no_photo_135.png" height="170" width="220"/>
									</c:when>
									<c:when test="${slttype == 'VIDEO' }">
										<img class="tip" title="${archiveitem.sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/file/pic/video.jpg" height="170" width="220"/>
									</c:when>
									<c:when test="${slttype == 'OTHER' }">
										<img class="tip" title="${archiveitem.sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/images/no_photo_135.png" height="170" width="220"/>
									</c:when>
									<c:otherwise>
										<img class="tip" title="${archiveitem.sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/file/pic/${archiveitem.slt}" height="170" width="220"/>
									</c:otherwise>
								</c:choose>
		                   		<div id="${archiveitem.id }_xlan" class="xlan" onclick="showElement('xuanxiang')" >
		                           	<img src="${pageContext.request.contextPath}/images/xh1_03.png" width="40px" height="30" />
		                        </div>
								<div id="${archiveitem.id }_xuanxiang" class="xuanxiang" onMouseOver="" onMouseOut="">
									<div class="xx11"><a href="javascript:;" onclick="edit('${archiveitem.id }')">编辑</a></div>
									<div class="xx11"><a href="#">设为封面</a></div>
									<div class="xx11"><a href="javascript:;">上传</a></div>
									<div class="xx11"><a href="#">下载</a></div>
									<div class="xx11"><a href="#" onclick="del('${archiveitem.id }')">删除</a></div>
								</div>
							</div>
		                    <div >
		                    	<div class="miaoshu22" title="${archiveitem.tm }">
			                    	<c:set var="subStr" value="${archiveitem.tm}"></c:set>
									<c:choose>
										<c:when test="${fn:length(subStr) > 14 }">
											${fn:substring(archiveitem.tm, 0, 14)}..
										</c:when>
										<c:otherwise>
									      	${archiveitem.tm }
									    </c:otherwise>
									</c:choose>
								</div>
		                    </div>
		                    <div style=" clear:both"></div>
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
