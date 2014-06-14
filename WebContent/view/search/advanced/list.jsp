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
        
		//加载树
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		//删除动态条件
		$('button.delete-query-item').live('click', function () {
		    $(this).parent().remove();
		    return false;
		});
	});
	
	function selectNode(treeid) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id", treeid, null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			treeObj.expandNode(node);
		}
	}

	var orgid;	//树节点ID
	function onClick(event, treeId, nodes) {
		orgid = nodes.id;
		if (nodes.treetype != 'W') {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			treeObj.expandNode(nodes);
		} else {
			readField(nodes.id,'01'); // 初始读取案卷
			//window.location.href = "${pageContext.request.contextPath}/archive/list.do?treeid=" + nodes.id;
		}
	};

	function pageselectCallback(page_index, jq){
		//var searchTxt = "${searchTxt }";
		//var openexpand = JSON.stringify(expand);
		//var searchTreeids = '${searchTreeids}';
		
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			//window.location.href="${pageContext.request.contextPath }/intelligent/list.do?treeid=${selectid}&page="+page_index+"&expand="+openexpand + "&searchTreeids="+searchTreeids;
		}
	};
	
</script>

<script type="text/javascript">

	//添加条件
	function addTerm(){
		var fieldNameId = $("#selectField").val();
		var fieldName = $("#selectField option:selected").text();
		if(fieldNameId!='' && fieldName!=''){
			var html = "";
		    html += '<div class="query-item">';
			html += '    <input type="text" readonly="readonly" value="'+fieldName+'" title="字段名" class="span2" />';
			html += '    <select class="input-small operate-type" title="条件">';
			html += '        <option value="1">等于</option>';
			html += '        <option value="2">不等于</option>';
			html += '        <option value="3">大于</option>';
			html += '        <option value="4">大于等于</option>';
			html += '        <option value="5">小于</option>';
			html += '        <option value="6">小于等于</option>';
			html += '        <option value="7">包含</option>';
			html += '    </select>';
			html += '    <input type="text" name="'+fieldNameId+'" class="input-large" id="'+fieldNameId+'" value="" title="值" />';
			html += '    <button type="button" style="margin-bottom: 9px;" class="btn btn-mini btn-danger delete-query-item" title="删除条件">';
			html += '        <i class="icon-minus icon-white"></i>';
			html += '    </button>';
			html += '</div>';
			
			$(".query-group").append(html);
		}else{
			alert("请选择档案节点并添加过滤条件!");
		}
	}


	//同步读取当前节点的字段
	function readField(treeid,tableType) {
		//alert(treeid + "--" + tableType);
	    $.ajax({
	        async: false,
	        url: "../advanced/getFieldAdvanced.do",
	        type: 'post',
	        dataType: 'script',
	        data: {treeid: treeid, tableType: tableType},
	        success: function (data) {
	            if (data != "error") {
	                field = eval(fields);
	                tableFields = field;
	                talbeField(field); //字段
	                var html = "";
	                if(templeType=="A"){ //案卷
	                	html += '<label class="radio"><input id="templeType_A" type="radio" name="templeType" onclick="getTableType(this.value)" value="01" /> 案卷级</label>';
						html += '<label class="radio"><input id="templeType_W" type="radio" name="templeType" onclick="getTableType(this.value)" value="02" /> 文件级</label>';
	                }else{
						html += '<label class="radio"><input id="templeType_W" type="radio" name="templeType" onclick="getTableType(this.value)" value="01" /> 文件级</label>';                	
	                }
	                $("#templeType").html(html);
	               // searchCommon.templettype = templeType;
	            } else {
	                alert('读取数据时出错，请尝试重新操作或与管理员联系!');
	            }
	        }
	    });
	}

	//表字段
	function talbeField(tableField){
		var html = "";
		if (tableField.length > 0) {
	        for (var i=0;i<tableField.length;i++) {
	        	html += "<option value=\""+tableField[i].englishname+"\">"+tableField[i].chinesename+"</option>";
	        }
	    }
	    $("#selectField").html(html);
	}

	//获得类型
	function getTableType(value){
		$("#tableType").val(value);
		readFieldAW(value);//读取字段
	}
	
	//案卷，文件切换
	function readFieldAW(tableType) {
	    $.ajax({
	        async: false,
	        url: "../advanced/getFieldAdvanced.do",
	        type: 'post',
	        dataType: 'script',
	        data: {treeid: orgid, tableType: tableType},
	        success: function (data) {
	            if (data != "error") {
	                field = eval(fields);
	                tableFields = field;
	                talbeField(field); //字段
	                $(".query-item").remove(); //清空条件 
	            } else {
	                alert('读取数据时出错，请尝试重新操作或与管理员联系!');
	            }
	        }
	    });
	}
	
	/**
	 * 根据选择的树节点 查找统计
	 */
	function doSearch(){
		var qg = GetQueryGroup('.query-group');
		var item = JSON.stringify(qg);
		var field = $("#selectField").val();
		var tableType = $("#tableType").val();
		if(field == "" || field == null){
			alert("请选择档案节点并添加过滤条件!");
			return ;
		}else{
			if(tableType == ""){
				alert("请选择档案类型!");
				return;
			}
		}
		alert(item);
		window.location.href="${pageContext.request.contextPath }/advanced/searchAdvanced.do?groupitem="+item+"&treeid="+orgid+"&tabletype="+tableType+"&page=0";

		/*
		$.ajax({
		     async: false,
		     url: "searchAdvanced.do",
		     type: 'post',
		     dataType: 'script',
		     data: {groupitem: item ,treeid:orgid,tabletype:tableType,page:0},
		     success: function (data) {
		     	list = eval(dynamicList);
		     	//searchCommon.tablename = tableName;
		     	if(list.length>0){
		     		var doc = showResultList(list);
		     		$("#countResult").html(doc);
		     		//searchCommon.pages = intPageCount; //总页数
		     		//searchCommon.currentPage = 1; //初始
		     		if(intPageCount>1){ //显示分页
		     			pageState();
		     		}else{
		     			$('.pagination').css('display','none');
		     		}
		     	}else{
		     		alert("没有符合该条件的数据，请重新选择条件");
		     	}
		     }
		});
		*/
	}
	
	/**
	 * 条件对象
	 */
	function QueryItem() {
	    this.name = '';
	    this.operatorType = 0;
	    this.value = '';
	    //this.valueType = 0;
	}
	
	/**
	 * 获得条件
	 */
	function GetQueryGroup(group) {
	    group = $(group);
	    var queryItems = group.children('.query-item');
	    var items = [];
	    for (var k = 0; k < queryItems.length; k++) {
	        var queryItem = new QueryItem();
	        queryItem.name = $(queryItems[k]).find('.input-large').attr('id');
	        //queryItem.operatorType = parseInt($(queryItems[k]).find('.operate-type').val());
	        queryItem.operatorType = $(queryItems[k]).find('.operate-type').val();
	        queryItem.value = $(queryItems[k]).find('.input-large').val();
	        //queryItem.valueType = parseInt($(queryItems[k]).find('.value-type').val());
	        items.push(queryItem);
	    }
	    return items;
	}

	//设置cookie
	function setCookie(name,value){
	    var Days = 30;
	    var exp = new Date();
	    exp.setTime(exp.getTime() + Days*24*60*60*1000);
	    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
	}
	//获取cookies
	function getCookie(name){
	    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
	    if(arr=document.cookie.match(reg))
	        return (arr[2]);
	    else
	        return null;
	}
	//删除cookies
	function delCookie(name){
	    var exp = new Date();
	    exp.setTime(exp.getTime() - 1);
	    var cval=getCookie(name);
	    if(cval!=null)
	        document.cookie= name + "="+cval+";expires="+exp.toGMTString();
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
		<div align="center">
			<div class="query-group well">
				<div class="form-horizontal">
					<div class="control-group">
						<label for="servername" class="control-label">匹配以下</label>
						<div class="controls">
							<div class="span3"><select id="selectField" class="input-small group-type span2"></select>
			                <button type="button" class="btn btn-mini btn-success add-query-item" onclick="addTerm();" title="增加一个条件">
			                    <i class="icon-plus icon-white"></i>
			                </button></div>
			                <div><input type="text" id="tableType" value="" /></div>
			                <div class="span1"><button class="btn btn-primary" onclick="doSearch();">查询</button></div>
						</div>
					</div>
				</div>
				<div class="form-horizontal">
					<div class="control-group">
						<label for="servername" class="control-label">档案类型</label>
						<div id="templeType" class="controls">
							
						</div>
					</div>
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
								
								<td width="40px">全文</td>
								<c:forEach items="${fields}" varStatus="i" var="item">
									<c:if test="${(item.sort > 0) and (item.isgridshow == 1)}">
										<td>${item.chinesename }</td>
									</c:if>
								</c:forEach>
								<td width="60px">操作</td>
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
