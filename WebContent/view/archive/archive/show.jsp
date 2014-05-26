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

<base target="_self">

<script>

	$(function(){
		var tabletype = '${tabletype }';
		
		if (tabletype == '01') {
			$('#aj').hide();
		}
		
		//var parentWin=window.dialogArguments;
		//var buttonValue=parentWin.document.getElementByIdx("mybutton2").value;   //获取父窗口中的对象
		//var parentValue=parentWin.nodes;       //获取父窗口中的变量
		
	})
	
	function down(id) {
        var link = "${pageContext.request.contextPath}/doc/download.do?id=" + id;
        //window.open(link);
        window.location.href=link;
        return false;
	}
	
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function del(id) {
		if (confirm("确定要删除选择的电子文件吗？删除操作不可逆,请谨慎操作。")) {
			setTimeout(function () {  
				$.ajax({
					async : false,
					url : "${pageContext.request.contextPath}/doc/delete.do",
					type : 'post',
					data : {
						'id' : id
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
	
	function upload(id) {
		
		var treeid = '${treeid}';
		if (treeid == '') {
			alert('没有获得系统参数，请退出重新登录，再尝试操作。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/doc/show_upload.do?treeid="+treeid+"&tabletype=${tabletype}&archiveid=" + id + "&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 500
		};
		var result = openShowModalDialog(url, window, whObj);
		reload();
	}
	
</script>
<title>查看档案</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<table width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">
                	查看档案
                	<input type="hidden" id="id" name="id" value="${maps[0]['id']}">
                	<input type="hidden" id="treeid" name="treeid" value="${treeid }">
                	<input type="hidden" id="tabletype" name="tabletype" value="${tabletype }">
                </td>
            </tr>
            <c:forEach items="${fields}" varStatus="j" var="item">
				<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
					<tr>
						<td>${item.chinesename } :</td>
						<td>${maps[0][item.englishname]}</td>
					</tr>
				</c:if>
			</c:forEach>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
	<table width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="6" align="center">
                	电子文件
                </td>
            </tr>
            <tr>
                <td align="center">序号</td>
                <td align="center">文件名</td>
                <td align="center">类型</td>
                <td align="center">大小</td>
                <td align="center">权限</td>
                <td align="center">操作</td>
            </tr>
            <c:choose>
            	<c:when test="${treeauth.docauth == '1' }">
            		<c:forEach items="${docs}" varStatus="i" var="doc">
						<tr>
							<td align="center">${i.index+1 }</td>
							<td align="center">${doc.docoldname}</td>
							<td align="center">${doc.docext}</td>
							<td align="center">${doc.doclength}</td>
							<td align="center">${codeMap[doc.docauth] }</td>
							<td align="center">
								<c:if test="${treeauth.filescan == 1 && isFileShow == true }">
									<button>查看</button>
								</c:if>
								<c:if test="${treeauth.filedown == 1 }">
									<a href="javascript:;" onclick="down('${doc.id}')">下载</a>
								</c:if>
								<c:if test="${readonly == 0 }">
								<a href="javascript:;" onclick="del('${doc.id}')">删除</a>
								</c:if>
							</td>
						</tr>
					</c:forEach>
            	</c:when>
            	<c:otherwise>
            		<c:forEach items="${docs}" varStatus="i" var="doc">
		            	<c:if test="${doc.docauth == treeauth.docauth }">
		            		<tr>
								<td align="center">${i.index+1 }</td>
								<td align="center">${doc.docoldname}</td>
								<td align="center">${doc.docext}</td>
								<td align="center">${doc.doclength}</td>
								<td align="center">${codeMap[doc.docauth] }</td>
								<td align="center">
									<c:if test="${treeauth.filescan == 1 && isFileShow == true}">
										<button>查看</button>
									</c:if>
									<c:if test="${treeauth.filedown == 1 }">
										<a href="javascript:;" onclick="down('${doc.id}')">下载</a>
									</c:if>
									<c:if test="${readonly == 0 }">
									<a href="javascript:;" onclick="del('${doc.id}')">删除</a>
									</c:if>
								</td>
							</tr>
		            	</c:if>
					</c:forEach>
            	</c:otherwise>
            </c:choose>
			<tr>
				<td colspan="6" align="center">
					<button type="button" onclick="closepage()">关闭</button>
					<c:if test="${readonly == 0 }">
					<button type="button" onclick="upload('${maps[0]['id']}')">上传</button>
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>
	
	<table id="aj" width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">
                	所属案卷信息
                </td>
            </tr>
            <c:forEach items="${ajField}" varStatus="j" var="item">
				<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
					<tr>
						<td>${item.chinesename } :</td>
						<td>${ajMaps[0][item.englishname]}</td>
					</tr>
				</c:if>
			</c:forEach>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>