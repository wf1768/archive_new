<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dialog_util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>

<base target="_self">
<style type="text/css">
	.div1 {
		width:100%;
		height:250px;
		clear:both;
		overflow:auto;
	}
	.div2 {
		float:left;
		width:375px;
		height:220px;
		overflow:auto;
	}
	.div3 {
		width:275px;
		height:220px;
		float:left;
		overflow:auto;
	}
	
	.table-SbgList:hover {
		background:#b8e4f9;
	}
	
	.selected {
		background:#b8e4f9;
	}  
</style>

<script>

	var treeid = '${treeid}';
	var tabletype = '${tabletype}';
	//档案数据json
	var maps = JSON.parse('${maps_json }');
	//当前帐户上传的未挂接电子文件json
	var doc_no_json = JSON.parse('${doc_no_json}');
	//未挂接电子文件处理后的对象。
	var doc_no = [];
	//档案与电子文件挂接后对象的集合数组
	var doc_yes = [];
	
	
	//挂接条件预设
	var linkField = "AJH";
	
	function autolink() {
		if (confirm("自动挂接将取消本次已经挂接的其他操作，重新挂接电子文件，确定吗？。")) {
			setDocNotTr();
			//初始化
			doc_yes = [];
			//如果没有档案
			if (maps.length == 0) {
				alert("没有找到档案，请退出重新尝试。");
				return;
			}
			
			if (doc_no.length == 0) {
				alert("没有找到未挂接电子文件，请先上传电子文件，再重新尝试自动挂接。");
				return;
			}
			for (var i=0;i<doc_no.length;i++) {
				for (var j=0;j<maps.length;j++) {
					var docname = doc_no[i].docoldname.substring(0, doc_no[i].docoldname.lastIndexOf('.'));
					if (docname == maps[j][linkField] ) {
						//如果符合条件，则移入已挂接全文table
						addRow("table_yes_body",doc_no[i].id,"yes");
						deleteRow("table_not",doc_no[i].id,"not");
						//保存对应关系
						var doc = {};
						doc.fileid = maps[j].ID;
						doc.id = doc_no[i].id;
						doc_yes.push(doc);
						$("#"+doc.fileid +"_isdoc").html("<img src='${pageContext.request.contextPath }/images/icons/attach.png' >");
						break;
					}
				}
			}
		}
		setCss();
		
		//addRow("table_yes_body","aabb6aab-3163-432a-9a30-7a27803966f0","yes");
		//deleteRow("table_not","aabb6aab-3163-432a-9a30-7a27803966f0","not");
	}
	
	function selflink() {
		var archive_select = "";
		
		$("input[name='checkbox_archive']:checked").each(function () {
			archive_select+=$(this).val()+ ",";
		});
		
		if (archive_select == "") {
			alert("请先选择要手动挂接的档案数据。");
			return;
		}
		
		archive_select = archive_select.substring(0,archive_select.length-1);
		
		archive_select  = archive_select.split(",");
		
		if (archive_select.length != 1) {
			alert("手动挂接只能选中一个档案数据进行挂接。");
			return;
		}
		
		var str = "";
		
		$("input[name='checkbox_not']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要手动挂接的电子文件数据。");
			return;
		}
		str = str.substring(0,str.length-1);
		
		str = str.split(",");
		
		if (doc_no.length == 0) {
			alert("没有找到未挂接电子文件，请先上传电子文件，再重新尝试自动挂接。");
			return;
		}
		
		for (var i=0;i<str.length;i++) {
			//如果符合条件，则移入已挂接全文table
			addRow("table_yes_body",str[i],"yes");
			deleteRow("table_not",str[i],"not");
			//保存对应关系
			var doc = {};
			doc.fileid = archive_select[0];
			doc.id = str[i];
			doc_yes.push(doc);
			$("#"+doc.fileid +"_isdoc").html("<img src='${pageContext.request.contextPath }/images/icons/attach.png' >");
		}
		
		setCss();
	}
	
	function move() {
		var str = "";
		
		$("input[name='checkbox_yes']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要移除挂接的电子文件数据。");
			return;
		}
		str = str.substring(0,str.length-1);
		
		str = str.split(",");
		
		if (doc_no.length == 0) {
			alert("没有找到未挂接电子文件，请先上传电子文件，再重新尝试自动挂接。");
			return;
		}
		//记录下被移除文件的档案id，移除后，如果这些档案id下没有其他挂接文件，将全文表示图片去掉
		var isdoc = [];
		for (var i=0;i<str.length;i++) {
			for (var j=0;j<doc_yes.length;j++) {
				if (str[i] == doc_yes[j].id ) {
					//如果符合条件，则移入未挂接全文table
					addRow("table_not_body",str[i],"not");
					deleteRow("table_yes",str[i],"yes");
					//移除对应关系
					//doc_yes.remove(j);
					isdoc.push(doc_yes[j].fileid);
					doc_yes.splice(j, 1);
					break;
				}
			}
		}
		
		var having = false;
		for (var i=0;i<isdoc.length;i++) {
			for (var j=0;j<doc_yes.length;j++) {
				if (isdoc[i] == doc_yes[j].fileid) {
					having = true;
				}
			}
			if (!having) {
				$("#"+isdoc[i] +"_isdoc").html("");
			} 
		}
		setCss();
	}
	
	function setCss() {
		$('#table_not_body>tr').unbind('click');
		$('#table_yes_body>tr').unbind('click');
		
		$('#table_not_body>tr').click(function () {
           if (!$(this).hasClass("selected")) {  
               $(this).addClass("selected").find(":checkbox").attr("checked", true);
           }  
           else {  
               $(this).removeClass("selected").find(":checkbox").attr("checked", false);  
           }
	    });
		$('#table_yes_body>tr').click(function () {
           if (!$(this).hasClass("selected")) {  
               $(this).addClass("selected").find(":checkbox").attr("checked", true);
           }  
           else {  
               $(this).removeClass("selected").find(":checkbox").attr("checked", false);  
           }
	    });
	}
	
	/*
	*	table:容器id
	*	id:doc的id
	*	type：类型，yes or not
	*/
	function addRow(table,id,type) {
	
		var doc = null;
		for (var i=0;i<doc_no.length;i++) {
			if (doc_no[i].id == id) {
				doc = doc_no[i];
				break;
			}
		}
		
		if (doc) {
			var rowHtml="";
		    rowHtml +="<tr id='"+id + "_" + type +"' class=\"table-SbgList\"  align='center'>";
		    rowHtml +="<td><input type=\"checkbox\" name=\"checkbox_"+type+"\" value=\""+id+"\" class=\"shiftCheckbox\"></td>";
		    rowHtml +="<td>"+doc.num+"</td>";
		    rowHtml +="<td>"+doc.docoldname+"</td>";
		    rowHtml +="<td>"+doc.docext+"</td>";
		    rowHtml +="<td>"+doc.doclength+"</td>";
		    rowHtml +="</tr>";
		    $("#" + table).append(rowHtml);
		}
	}
	
	//删除行
	function deleteRow(table,id,type){
	    $("#"+table).children().find("#"+id + "_" + type).remove();
	}
	
	function setLinkField() {
		linkField = $("#linkfield").val();
		$("#linkfield_span").html($("#linkfield").find("option:selected").text());
	}
	
	function show_doc_yes() {
		if (doc_yes.length == 0) {
			return;
		}
		
		$("#table_yes_body").html("");
		
		var archive_select = "";
		
		$("input[name='checkbox_archive']:checked").each(function () {
			archive_select+=$(this).val()+ ",";
		});
		
		if (archive_select == "") {
			return;
		}
		
		archive_select = archive_select.substring(0,archive_select.length-1);
		archive_select  = archive_select.split(",");
		
		for (var i=0;i<doc_yes.length;i++) {
			for (var j=0;j<archive_select.length;j++) {
				if (doc_yes[i].fileid == archive_select[j]) {
					addRow("table_yes_body",doc_yes[i].id,"yes");
					//deleteRow("table_not",doc_no_json[i].id,"not");
				}
			}
		}
		setCss();
	}
	
	function save() {
		
		if (doc_yes.length == 0) {
			alert("档案并没有挂接任何文件。请挂接后，再保存挂接关联。");
			return;
		}
		
		//系统字段对象
		var s = {};
		s.treeid = treeid;
		s.tabletype = tabletype;
		
		var data = JSON.stringify(doc_yes);
		var sys = JSON.stringify(s);
		if (confirm("确定要保存批量挂接的电子文件吗？")) {
			setTimeout(function () {  
				$.ajax({
					async : false,
					url : "${pageContext.request.contextPath}/doc/multiple.do",
					type : 'post',
					data : {
						'data' : data,
						'sys'  : sys
					},
					dataType : 'text',
					success : function(data) {
						alert(data);
					}
				});
				reload();
			},200);  
		};
	}
	
	$(function(){
		var winW = $(window).width();
		var div2_w = $(".div2").width();
		$(".div3").width(winW - div2_w - 20);
		
		setDocNotTr();
		
		$('#table_archive_body>tr').click(function () {
           if (!$(this).hasClass("selected")) {  
               $(this).addClass("selected").find(":checkbox").attr("checked", true);
           }  
           else {  
               $(this).removeClass("selected").find(":checkbox").attr("checked", false);  
           }
           show_doc_yes();
	    });
		
		$('#checkall_archive').click(function(){
		    $('input[name="checkbox_archive"]').attr("checked",this.checked);
		    if (this.checked) {
		    	$('input[name="checkbox_archive"]').parent().parent().addClass("selected");
            }  
            else {  
            	$('input[name="checkbox_archive"]').parent().parent().removeClass("selected");
            }
		    
		    show_doc_yes();
		});
		
		$('#checkall_not').click(function(){
		    $('input[name="checkbox_not"]').attr("checked",this.checked);
		    if (this.checked) {
		    	$('input[name="checkbox_not"]').parent().parent().addClass("selected");
            }  
            else {  
            	$('input[name="checkbox_not"]').parent().parent().removeClass("selected");
            }
		});
		$('#checkall_yes').click(function(){
		    $('input[name="checkbox_yes"]').attr("checked",this.checked);
		    if (this.checked) {
		    	$('input[name="checkbox_yes"]').parent().parent().addClass("selected");
            }  
            else {  
            	$('input[name="checkbox_yes"]').parent().parent().removeClass("selected");
            }
		});
		
		$('.shiftCheckbox').shiftcheckbox();
		
	})
	
	//填充未挂接电子文件列表
	function setDocNotTr() {
		$("#table_not_body").html("");
		doc_no = [];
		if (doc_no_json.length == 0) {
			return;
		}
		
		var doc = null;
		for (var i=0;i<doc_no_json.length;i++) {
			doc = doc_no_json[i];
			if (doc) {
				doc.num = i+1;
				var rowHtml="";
			    rowHtml +="<tr id='"+doc.id + "_not' class=\"table-SbgList\"  align='center'>";
			    rowHtml +="<td><input type=\"checkbox\" name=\"checkbox_not\" value=\""+doc.id+"\" class=\"shiftCheckbox\"></td>";
			    rowHtml +="<td>"+doc.num+"</td>";
			    rowHtml +="<td>"+doc.docoldname+"</td>";
			    rowHtml +="<td>"+doc.docext+"</td>";
			    rowHtml +="<td>"+doc.doclength+"</td>";
			    rowHtml +="</tr>";
			    $("#table_not_body").append(rowHtml);
			    doc_no.push(doc);
			}
		}
		setCss();
	}
	
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function del() {
		var str = "";
		
		$("input[name='checkbox_not']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要删除的数据。");
			return;
		}
		str = str.substring(0,str.length-1);
		
		if (confirm("确定要删除选择的电子文件吗？删除操作不可逆,请谨慎操作。")) {
			setTimeout(function () {  
				$.ajax({
					async : false,
					url : "${pageContext.request.contextPath}/doc/delete.do",
					type : 'post',
					data : {
						'id' : str
					},
					dataType : 'text',
					success : function(data) {
						alert(data);
					}
				});
				reload();
			},200);  
		};
	}
	
	function upload() {
		
		alert("可以单次、多次上传多个电子文件，请将电子文件全部上传完毕，再处理挂接。");
		
		var treeid = '${treeid}';
		if (treeid == '') {
			alert('请选择左侧档案节点，再查看档案。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/doc/show_upload.do?treeid="+treeid+"&tabletype=${tabletype}&archiveid=&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 500
		};
		var result = openShowModalDialog(url, window, whObj);
		reload();
	}
	
</script>
<title>批量挂接电子文件</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<div class="div1"  style ="border:solid 1px #345674;">
		<table id="table_archive" aline="left" width="100%" cellspacing="0" cellpadding="6" align="center">
			<thead>
				<tr>
	                <td colspan="${fn:length(fields)+4 } " align="left">
	                	批量挂接档案
	                </td>
	            </tr>
	            <tr>
					<td colspan="${fn:length(fields)+4 }" align="left">
						<button type="button" onclick="save()">保存</button>
						<button type="button" onclick="closepage()">关闭</button>
						自动挂接条件：<span id="linkfield_span" style="color: red;">案卷号.</span>
						<select id="linkfield">
							<c:forEach items="${fields}" varStatus="i" var="item">
								<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
									<option value="${item.englishname }">${item.chinesename }</option>
								</c:if>
							</c:forEach>
						</select>
						<button type="button" onclick="setLinkField()">更改条件</button>
					</td>
				</tr>
				<tr class="tableTopTitle-bg" align="center">
					<td width="30px"><input type="checkbox" id="checkall_archive"></td>
					<td width="40px">行号</td>
					<td width="40px">全文</td>
					<c:forEach items="${fields}" varStatus="i" var="item">
						<c:if test="${(item.sort > 0) and (item.isgridshow == 1)}">
							<td>${item.chinesename }</td>
						</c:if>
					</c:forEach>
				</tr>
			</thead>
			<tbody id="table_archive_body">
				<c:forEach items="${maps}" varStatus="i" var="archiveitem">
					<tr class="table-SbgList" align="center">
						<td><input type="checkbox" name="checkbox_archive" value="${archiveitem.id }" class="shiftCheckbox"></td>
						<td>${i.index+1 }</td>
						<td id="${archiveitem.id }_isdoc"></td>
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
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="div2"  style ="border:solid 1px #556677;">
		<table id="table_yes" width="600px" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
			<thead>
				<tr>
	                <td colspan="6" align="left">
	                	已挂接电子文件
	                </td>
	            </tr>
	            <tr>
					<td colspan="6" align="left">
						<button type="button" onclick="move()">移除</button>
					</td>
				</tr>
	            <tr>
	                <td align="center" width="30px"><input type="checkbox" id="checkall_yes"></td>
	                <td align="center">序号</td>
	                <td align="center">文件名</td>
	                <td align="center">类型</td>
	                <td align="center">大小</td>
	            </tr>
	         </thead>
	         <tbody id="table_yes_body">
	            
			</tbody>
	  	</table>
	</div>
	<div class="div3"  style ="border:solid 1px #09aadd;">
		<table id="table_not" width="100%" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
			<thead>
				<tr>
	                <td colspan="6" align="left">
	                	未挂接电子文件
	                </td>
	            </tr>
	            <tr>
					<td colspan="6" align="left">
						<button type="button" onclick="upload()">上传</button>
						<button type="button" onclick="autolink()">自动挂接</button>
						<button type="button" onclick="selflink()">手动挂接</button>
						<button type="button" onclick="del()">删除</button>
					</td>
				</tr>
	            <tr>
	            	<td align="center" width="30px"><input type="checkbox" id="checkall_not"></td>
	                <td align="center">序号</td>
	                <td align="center">文件名</td>
	                <td align="center">类型</td>
	                <td align="center">大小</td>
	            </tr>
	        </thead>
	        <tbody id="table_not_body">
	            
			</tbody>
	  	</table>
	</div>
	
</body>
</html>