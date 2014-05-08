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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>

<base target="_self">

<script>

	var targetTreeid = '${targetTreeid}';
	var targetTabletype = '${targetTabletype}';
	var parentid = '${parentid}';
	
	var treeid = '${sessionScope.CURRENT_DATA_COPY_TREEID_SESSION }';
	var tabletype = '${sessionScope.CURRENT_DATA_COPY_TABLETYPE_SESSION }';
	
	var treename = '${tree.treename}';
	var targetTreename = '${targetTree.treename}';
	var templettype = '${templet.templettype}';
	var targetTemplettype = '${targetTemplet.templettype}';
	
	var fieldsTarget_json = JSON.parse('${fieldsTarget_json}');

	function paster() {
		if (treeid == "" || tabletype == "") {
			alert("未获得源档案数据，可能因为您操作时间过长，请退出重新复制。");
			return;
		}
		
		var isdoc = $('input[name="doc"]:checked').val();
		
		//字段对应关系
		var s = {};
		
		for (var i=0;i<fieldsTarget_json.length;i++) {
			if (fieldsTarget_json[i].sort >0) {
				var dy = $("#" + fieldsTarget_json[i].englishname).val();
				if (dy != "") {
					s[fieldsTarget_json[i].englishname] = dy;
				}
			}
		}
		
		var dy = JSON.stringify(s);
		if (confirm("确定要粘贴档案数据吗？")) {
			$.blockUI({
				message:"正在准备粘贴数据，请稍候...",
				css: {
	                padding: '15px',
	                width:"300px"
	            } 
	        });
			
			setTimeout(function () {  
				$.ajax({
					async : false,
					url : "${pageContext.request.contextPath}/archive/datapaster.do",
					type : 'post',
					data : {
						'dy' : dy,
						'targetTreeid':targetTreeid,
						'targetTabletype':targetTabletype,
						'parentid':parentid,
						'isdoc':isdoc
					},
					dataType : 'text',
					success : function(data) {
						$.unblockUI();
						alert(data);
					}
				});
				reload();
			},200);  
		};
	}
	
	$(function(){
		var str = "档案数据将要从 [<font color=\"red\">"+treename+"</font>] ";
		var targetStr = " [<font color=\"red\">"+targetTreename+"</font>] ";
		if (templettype == "A" || templettype == "P") {
			if (tabletype == "01") {
				str += "案卷级";
			}
			else {
				str += "文件级";
			}
		}
		else {
			str += "文件级";
		}
		
		if (targetTemplettype == "A" || targetTemplettype == "P") {
			if (targetTabletype == "01") {
				targetStr += "案卷级";
			}
			else {
				targetStr + "文件级";
			}
		}
		else {
			targetStr += "文件级";
		}
		
		var html = str + " 粘贴到 " +targetStr;
		$("#memo").html(html);
	})
	
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	
</script>
<title>粘贴档案数据字段属性对应</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	
	<table width="90%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
            <tr>
                <td colspan="2" align="center">
                	粘贴档案数据字段属性对应
                </td>
            </tr>
            <tr>
            	<td>目标档案库字段属性</td>
            	<td>源档案库字段属性</td>
            </tr>
            <c:forEach items="${fieldsTarget}" varStatus="i" var="itemTarget">
            	<c:if test="${(itemTarget.sort > 0)}">
		            <tr>
		            	<td>${itemTarget.chinesename }</td>
		            	<td>
		            		<select id="${itemTarget.englishname }">
		            			<option value="" >空白</option>
								<c:forEach items="${fields}" varStatus="i" var="item">
									<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
										<option value="${item.englishname }" ${itemTarget.englishname == item.englishname?'selected':'' }>${item.chinesename }</option>
									</c:if>
								</c:forEach>
							</select>
		            	</td>
		            </tr>
	            </c:if>
            </c:forEach>
            <tr>
            	<td colspan="2" align="center">
            		<input type="radio" value="0" name="doc" checked />仅粘贴数据
            		<input type="radio" value="1" name="doc"/>粘贴数据及电子文件
            	</td>
            </tr>
            <tr>
            	<td id="memo" colspan="2" align="center">
            		
            	</td>
            </tr>
            <tr>
            	<td colspan="2" align="center">
            		<button type="button" onclick="paster()">粘贴</button>
            		<button type="button" onclick="closepage()">关闭</button>
            	</td>
            </tr>
		</tbody>
	</table>
	
	
	<table id="table_archive" aline="left" width="90%" cellspacing="0" cellpadding="6" align="center" style="margin-top:20px">
		<thead>
			<tr>
                <td colspan="${fn:length(fields)+3 } " align="center">
                	粘贴档案数据
                </td>
            </tr>
			<tr class="tableTopTitle-bg" align="center">
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
	
</body>
</html>