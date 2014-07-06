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

<script>
	$(function(){
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
		$('.shiftCheckbox').shiftcheckbox();
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
			alert('请选择左侧档案节点，再查看档案。');
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
	
	//预览
	function openContentDialog(docid) {
		var treeid = '${treeid}';
		$.ajax({
			async : false,
			url : "${pageContext.request.contextPath}/doc/preview.do",
			type : 'post',
			dataType : 'text',
			data:"treeid="+treeid+"&docid="+docid,
			success : function(data) {
				if (data == "0") {
					alert("对不起，您没有权限预览此文件！");
				} else {
					var a=document.createElement("a");  
					a.target="_blank"; 
					a.href="../readFile.html?selectid="+docid+"&treeid="+treeid;
					document.body.appendChild(a);  
					a.click();
				}
			}
		});
		
	}
	
	function setDocauth() {
		var str = "";
		
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要设置权限的电子全文。");
			return;
		}
		str = str.substring(0,str.length-1);
		
		var whObj = {
				width : 450,
				height : 300
			};
			
		var url = "${pageContext.request.contextPath}/doc/openSetDocauth.do?ids=" + str + "&time=" + Date.parse(new Date());
		var result = openShowModalDialog(url, window, whObj);
		reload();
	}
	
</script>
<title>查看档案电子文件</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<table width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="6" align="center">
                	电子文件
                	<input type="hidden" id="id" name="id" value="${maps[0]['id']}">
                	<input type="hidden" id="treeid" name="treeid" value="${treeid }">
                	<input type="hidden" id="tabletype" name="tabletype" value="${tabletype }">
                </td>
            </tr>
            <tr>
            	<td width="30px"><input type="checkbox" id="checkall"></td>
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
							<td><input type="checkbox" name="checkbox" value="${doc.id }" class="shiftCheckbox"></td>
							
							<td align="center">${i.index+1 }</td>
							<td align="center">${doc.docoldname}</td>
							<td align="center">${doc.docext}</td>
							<td align="center">${doc.doclength}</td>
							<td align="center">${codeMap[doc.docauth] }</td>
							<td align="center">
								<c:if test="${treeauth.filescan == 1 && isFileShow == true }">
									<a href="javascript:;" onclick="openContentDialog('${doc.id}')">查看</a>
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
										<a href="javascript:;" onclick="openContentDialog('${doc.id}')">查看</a>
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
					<button type="button" onclick="setDocauth()">设置权限</button>
					<c:if test="${readonly == 0 }">
					<button type="button" onclick="upload('${maps[0]['id']}')">上传</button>
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>