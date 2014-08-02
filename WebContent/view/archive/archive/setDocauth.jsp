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

<base target="_self">

<script>
	
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function setDocauth() {
		var ids = '${ids}';
		var authid = $("#authid").val();
		
		if (authid == "") {
			alert("请先选择要设置的权限。");
			return;
		}
		
		var d = {};
		d.ids = ids;
		d.authid = authid;
		
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/doc/setDocauth.do",
	        type : 'post',
	        data:d,
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!");
	            }
	        }
	    });
		reload();
	}
	
</script>
<title>设置档案电子文件权限</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">
                	设置电子全文权限
                	<input type="hidden" id="parentid" name="parentid" value="${parentid }">
                </td>
            </tr>
			<tr>
				<td class="txt1">选择电子全文权限 :
				</td>
				<td>
					<select id="authid" style="width: 160px">
						<c:forEach  items="${docauth}" var="item">
							<option value="${item.id }">${item.columndata }</option>
					    </c:forEach>
					</select>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="setDocauth()">保存</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>