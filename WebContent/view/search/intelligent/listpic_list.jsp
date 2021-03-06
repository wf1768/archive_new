<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<!-- 分页插件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/pagination/pagination.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pagination/jquery.pagination.js"></script>

<script type="text/javascript">

	$.blockUI({
		message:"正在进行加载，请稍候...",
		css: {
	        padding: '15px',
	        width:"300px"
	    } 
	}); 
	var selectTreeid = 0;
	var setting = {
		check: {
			enable: true
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
		view: {
			fontCss: getFont,
			nameIsHTML: true
		},
		callback: {
			onClick: onClick,
			//beforeCollapse: beforeCollapse,
			//beforeExpand: beforeExpand,
			onCollapse: onCollapse,
			onExpand: onExpand
		}
	};
	
	var expand = new Array();
	//当折叠时
	function onCollapse(event, treeId, treeNode) {
		if (expand.length != 0) {
			var treeid = treeNode.id;
			for (var i=0;i<expand.length;i++) {
				if (expand[i] == treeid) {
					expand.splice(i,1);
				}
			}
		}
	}
	//当展开节点时
	function onExpand(event, treeId, treeNode) {
		expand.push(treeNode.id);
	}
	
	function getFont(treeId, node) {
		return node.font ? node.font : {};
	}
	
	function openExpand() {
		var expandid = '${expand}';
		if (expandid != "") {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var openExpandid = JSON.parse(expandid);
			for (var i=0;i<openExpandid.length;i++) {
				var node = treeObj.getNodeByParam("id", openExpandid[i], null);
				if (node != null && node.id != 1) {
					treeObj.expandNode(node);
				}
			}
			expand = openExpandid;
		}
	}
	
	function checkNode() {
		var searchTreeids = '${searchTreeids}';
		if (searchTreeids == "") {
			return;
		}
		searchTreeids = JSON.parse(searchTreeids);
		
		for (var i=0;i<searchTreeids.length;i++) {
			var treeid = searchTreeids[i];
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			var node = zTree.getNodeByParam("id", treeid, null);
			if (node != null) {
				zTree.checkNode(node, true, false);
			}
		}
	}
	
	function selectNode(treeid) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id", treeid, null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			var expandid = '${expand}';
			if (expandid == "") {
				treeObj.expandNode(node);
			}
		}
	}
	
	function onClick(event, treeId, nodes) {
		if (nodes.treetype != 'W') {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var aa = treeObj.expandNode(nodes);
			if (aa) {
				onExpand(event, treeId, nodes);
			}
			else {
				onCollapse(event, treeId, nodes);
			}
		} else {
			var openexpand = JSON.stringify(expand);
			var searchTreeids = '${searchTreeids}';
			
			var nameArr = new Array();
			var valueArr = new Array();
			
			nameArr[0] = "searchTreeids";
			valueArr[0] = searchTreeids;
			nameArr[1] = "expand";
			valueArr[1] = openexpand;
			nameArr[2] = "treeid";
			valueArr[2] = nodes.id;
			
			post("listSearch.do",nameArr,valueArr);
			
			//window.location.href = "${pageContext.request.contextPath}/intelligent/list.do?treeid=" + nodes.id + "&expand="+openexpand + "&searchTreeids="+searchTreeids;
		}
	};
	
	function post(URL, nameArr,valueArr) {
	    $("body").append("<form></form>");  
	    $("body").find("form").attr("action",URL);  
	    $("body").find("form").attr("method","post");  
	    $("body").find("form").attr("display","none");
	    
	    for (var i=0;i<nameArr.length;i++) {
	    	$("body").find("form").append("<input type='text' name='"+nameArr[i]+"' value = '"+valueArr[i]+"'></input>");  
	    }
	    $("body").find("form").submit();  
	} 
	
	var nodes =  ${treeList};
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
		
		checkNode();
		openExpand();
		
		var treeid = "${selectid}";
		if (treeid != "") {
			selectTreeid = treeid;
		}
		
		selectNode(selectTreeid);
	});
	
	function callback() {
		var n = $(".scrollTable").height()-$(".aa").height();
		$("#la").height($("#bodyer_right").height());
		$('.data_table').fixHeader({
			height : n
		});
		
		/* $('#la').layout({ 
			applyDemoStyles: false,
			spacing_open:3, //边框的间隙
			west__size:230
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
		//$('.shiftCheckbox').shiftcheckbox();
		
		var jscroll = getCookie('jscroll');
		$('.body-wrapper').scrollTop(jscroll);
		delCookie('jscroll');//删除cookie
		
		$.unblockUI();
		
		
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
			var oHeight = $('#tip').height();
			var bheight = $('body').height();
			
			var t = (bheight - e.pageY);
			
			if (imgHeight > t) {
				var bb = imgHeight - t;
				$('#tip').css({"top":(e.pageY - bb)+"px","left":(e.pageX+30)+"px"});
			}
			
		}).mouseout(function(){
		   $('#tip').remove();
		}).mousemove(function(e){
			var imgHeight = $('#tipImg').height();
			//var oTop = $('#tip').offset().top;
			var oHeight = $('#tip').height();
			var bheight = $('body').height();
			
			var t = (bheight - e.pageY);
			
			if (imgHeight > t) {
				var bb = imgHeight - t;
				$('#tip').css({"top":(e.pageY - bb)+"px","left":(e.pageX+30)+"px"});
			}
			else {
				$('#tip').css({"top":(e.pageY-60)+"px","left":(e.pageX+30)+"px"});
			}
		})
	}
	
	function pageselectCallback(page_index, jq){
		var searchTxt = "${searchTxt }";
		var openexpand = JSON.stringify(expand);
		var searchTreeids = '${searchTreeids}';
		
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			
			var nameArr = new Array();
			var valueArr = new Array();
			
			nameArr[0] = "treeid";
			valueArr[0] = "${selectid}";
			nameArr[1] = "page";
			valueArr[1] = page_index;
			nameArr[2] = "expand";
			valueArr[2] = openexpand;
			nameArr[3] = "searchTreeids";
			valueArr[3] = searchTreeids;
			
			post("listSearch.do",nameArr,valueArr);
			
			//window.location.href="${pageContext.request.contextPath }/intelligent/list.do?"
			//		+"treeid=${selectid}&page="+page_index+"&expand="+openexpand + "&searchTreeids="+searchTreeids;
		}
	};
	
	function isSearchWj() {
		var openexpand = JSON.stringify(expand);
		var searchTreeids = '${searchTreeids}';
		
		var nameArr = new Array();
		var valueArr = new Array();
		
		nameArr[0] = "treeid";
		valueArr[0] = "${selectid}";
		nameArr[1] = "tabletype";
		valueArr[1] = "02";
		nameArr[2] = "expand";
		valueArr[2] = openexpand;
		nameArr[3] = "searchTreeids";
		valueArr[3] = searchTreeids;
		nameArr[4] = "isSearchWj";
		valueArr[4] = "1";
		
		post("listSearch.do",nameArr,valueArr);
		
		//window.location.href="${pageContext.request.contextPath }/intelligent/list.do?"
		//		+"treeid=${selectid}&tabletype=02&expand="+openexpand + "&searchTreeids="+searchTreeids + "&isSearchWj=1";
	}
	
	function showWj(id) {
		var openexpand = JSON.stringify(expand);
		var searchTreeids = '${searchTreeids}';
		
		var nameArr = new Array();
		var valueArr = new Array();
		
		nameArr[0] = "treeid";
		valueArr[0] = "${selectid}";
		nameArr[1] = "parentid";
		valueArr[1] = id;
		nameArr[2] = "page_aj";
		valueArr[2] = "${pagebean.pageNo }";
		nameArr[3] = "searchTxt_aj";
		valueArr[3] = "${searchTxt }";
		nameArr[4] = "tabletype";
		valueArr[4] = "02";
		nameArr[5] = "expand";
		valueArr[5] = openexpand;
		nameArr[6] = "searchTreeids";
		valueArr[6] = searchTreeids;
		
		post("listSearch.do",nameArr,valueArr);
		
		//window.location.href="${pageContext.request.contextPath }/intelligent/list.do?"
		//		+"treeid=${selectid}&parentid="+id+"&page_aj=${pagebean.pageNo }&"
		//		+"searchTxt_aj=${searchTxt }&tabletype=02&expand="+openexpand + "&searchTreeids="+searchTreeids;
	}
	
	function searchArchive() {
		
		var searchTxt = $("#searchTxt").val();
		
		if (searchTxt != "") {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = treeObj.getCheckedNodes(true);
			
			var arrayObj = new Array();
			for(var i=0;i<nodes.length;i++) {
				arrayObj.push(nodes[i].id);
			}
			
			if (arrayObj.length == 0) {
				alert("请先选择左侧要查询的树节点，再查询.");
				return;
			}
			var treeids = JSON.stringify(arrayObj);
		}
		
		var openexpand = JSON.stringify(expand);
		
		var nameArr = new Array();
		var valueArr = new Array();
		
		nameArr[0] = "searchTreeids";
		valueArr[0] = treeids;
		nameArr[1] = "expand";
		valueArr[1] = openexpand;
		nameArr[2] = "searchTxt";
		valueArr[2] = searchTxt;
		
		post("listSearch.do",nameArr,valueArr);
		
		//window.location.href="${pageContext.request.contextPath }/intelligent/list.do?searchTreeids="+treeids+"&expand="+openexpand+"&searchTxt="+searchTxt;
	}
	
	function setshow(templetid,tabletype) {
		jscroll('body-wrapper');
		if (templetid == "") {
			alert("请选择左侧父档案树节点，再设置显示设置。");
			return;
		}
		var url = "${pageContext.request.contextPath}/archive/setshow.do?templetid=" + templetid + "&tabletype="+tabletype+"&readonly=1&time=" + Date.parse(new Date());
		var whObj = {
			width : 900,
			height : 600
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
		
		var url = "${pageContext.request.contextPath}/archive/show.do?treeid="+treeid+"&tabletype=01&id=" + id + "&readonly=1&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 500
		};
		var result = openShowModalDialog(url, window, whObj);
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
		var url = "${pageContext.request.contextPath}/archive/doc.do?treeid="+treeid+"&tabletype=01&id=" + str + "&readonly=1&time=" + Date.parse(new Date());
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
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
<!--智能检索-内容部分开始-->
  	<div id="bodyer">
  		<div id="la" style="width: 100%;">
	        <div id="bodyer_left" class="ui-layout-west" style="widht:100%;padding: 0;">
				<dl style="width: 100%">
					<dt>
						<a href="#" style="width: 100%" class="blue"><img src="${pageContext.request.contextPath}/images/i1_03.png" width="29" height="22" class="tubiao" />
							<span>
								智能检索
							</span>
						</a>
					</dt>
					<dd>
						<ul id="treeDemo" class="ztree"></ul>
					</dd>
				</dl>
			</div>
			<div id="bodyer_right" class="ui-layout-center" style="widht:100%;padding: 0;">
				<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:999; ">
					<div class="dqwz_l">当前位置：智能检索
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
							
							<c:if test="${isSearchTreeid==true }">
								<button onclick="isSearchWj()">文件级</button>
							</c:if>
							<button onclick="setshow('${templet.id}','01')">设置</button>
							<input type="text" id="searchTxt" value="${searchTxt }" onKeyDown="javascript:if (event.keyCode==13) {searchArchive();}" />
							<a href="javascript:;" class="btn" onclick="searchArchive()">查询</a>
						</div>
					</div>
					<div style="clear: both"></div>
				</div>
				<div class="scrollTable" align="left" style="padding-left:5px; ">
					<table id="data_table" class="data_table table-Kang" aline="left" width="98%"
				border=0 cellspacing="1" cellpadding="4">
						<thead>
							<tr id="table_head" class="tableTopTitle-bg">
								<td width="30px"><input type="checkbox" id="checkall"></td>
								<td width="40px">行号</td>
								<c:if test="${templet.templettype=='A' or templet.templettype == 'P'}">
									<td width="40px">文件级</td>
								</c:if>
								<td width="40px">预览</td>
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
										<td><a title="多媒体文件级" href="javascript:;" onclick="showWj('${archiveitem.id}')"><img src="${pageContext.request.contextPath }/images/icons/page.png" ></a></td>
									</c:if>
									<c:set var="slt" value="${archiveitem.slt}"></c:set>
				                    <c:set var="slttype" value="${archiveitem.slttype}"></c:set>
									<c:choose>
										<c:when test="${fn:length(slt) == 0 }">
											<td><img class="tip" style="z-index:1;" src="${pageContext.request.contextPath}/images/no_photo_135.png" height="30" width="35"/></td>
										</c:when>
										<c:when test="${slttype == 'VIDEO' }">
											<td><img class="tip" title="${archiveitem.sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/file/pic/video.jpg" height="30" width="35"/></td>
										</c:when>
										<c:when test="${slttype == 'OTHER' }">
											<td><img class="tip" title="${archiveitem.sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/images/no_photo_135.png" height="30" width="35"/></td>
										</c:when>
										<c:otherwise>
											<td><img class="tip" title="${archiveitem.sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/${archiveitem.slt}" height="30" width="35"/></td>
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
	</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>