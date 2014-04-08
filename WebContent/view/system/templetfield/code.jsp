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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dialog_util.js"></script>
<base target="_self">

<script>

	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function add(fieldid) {
		if (fieldid == "") {
			alert('没有获取信息，请退出，重新操作或与管理员联系。');
			return;
		}
		var columndata = $("#columndata").val();
		if (columndata == "") {
			alert('没有获取信息，请退出，重新操作或与管理员联系。');
			return;
		}
		var par = {};
		par.id = fieldid;
		par.columndata = columndata;
		
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/savecode.do",
	        type : 'post',
	        data:par,
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	
	            } else {
	            	alert("读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!");
	            }
	            reload();
	        }
	    });
	}
	
	function del(id) {
		if (id == "") {
			alert('没有获取信息，请退出，重新操作或与管理员联系。');
			return;
		}
		
		var par = {};
		par.id = id;
		
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/delcode.do",
	        type : 'post',
	        data:par,
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	
	            } else {
	            	alert("读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!");
	            }
	            reload();
	        }
	    });
	}
	
	function sort(id,type) {
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/sortcode.do",
	        type : 'post',
	        data: {id:id,type:type},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	            reload();
	        }
	    });
	}
	
	function copy(id) {
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/copycode.do",
	        type : 'post',
	        data: {id:id},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	        }
	    });
	}
	
	function paste(targetid) {
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/pastecode.do",
	        type : 'post',
	        data: {targetid:targetid},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	reload();
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	        }
	    });
	}
	
	$(function() {  
        $("#columndata").keyup(function(event){  
          if(event.keyCode == 13){  
        	  add('${field.id}');
          }  
        });
        
        $("#columndata").focus();
    });
	
</script>
<title>字段代码维护</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<table width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td class="biaoti" colspan="2" align="center">为［<font color="red">${field.chinesename }</font>］添加字段代码</td>
            </tr>
			<tr class="tr1">
				<td class="txt1">代码内容 :</td>
				<td><input type="text" id="columndata" name="columndata" ></td>
			</tr>
			<tr>
				<td class="caozuo" colspan="2" align="center">
					<button type="button" onclick="add('${field.id}')">保存</button>
					<button type="button" onclick="closepage()">关闭</button>
					<c:if test="${field.accountid=='SYSTEM' }">
						<button type="button" onclick="copy('${field.id}')">复制</button>
						<c:choose>
							<c:when test="${sessionScope.CURRENT_CODE_COPY_SESSION==null}">
								<button type="button" disabled="disabled" onclick="paste('${field.id}')">粘贴</button>
							</c:when>
							<c:otherwise>
								<button type="button"  onclick="paste('${field.id}')">粘贴</button>
							</c:otherwise>
						</c:choose>
					</c:if>
					
				</td>
			</tr>
		</tbody>
	</table>
	<table width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="4" align="center">代码列表</td>
            </tr>
            <tr>
				<td>序号</td>
				<td>代码内容</td>
				<td>排序</td>
				<td>操作</td>
			</tr>
			<c:forEach items="${codes}" varStatus="i" var="item">
			<tr>
				<td>${i.index+1 }</td>
				<td>${item.columndata}</td>
				<td align="center">
				<c:if test="${!i.first }">
					<a href="javascript:;" onclick="sort('${item.id}','up')"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/up.png" /></a>
				</c:if>
				<c:if test="${!i.last }">
					<a href="javascript:;" onclick="sort('${item.id}','down')"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/down.png" /></a>
				</c:if>
				</td>
				<td>
					<a href="javascript:;" onclick="del('${item.id}')" class="juse">
						删除
					</a>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>