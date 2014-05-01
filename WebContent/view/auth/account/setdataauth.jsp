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
<base target="_self">

<script>

	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function saveDataAuth() {
		
		//获取权限内容
		var dataAuthValue = $("#dataAuthValue").val();
		var selectField = $("#selectField").val();
		var oper = $("#oper").val();
		var fieldname = $("#selectField").find("option:selected").text();
		
		if(dataAuthValue == "") {
			alert("请输入数据访问权限值！");
			return;
		}
		
		//创建对象
		var dataAuth = {};
		dataAuth.dataAuthValue = dataAuthValue;
		dataAuth.selectField = selectField;
		dataAuth.fieldname = fieldname;
		dataAuth.oper = oper;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/account/saveDataAuth.do",
	        type : 'post',
	        data:{accountid:"${account.id}",treeid:"${tree.id}",tabletype:"${tabletype }",filter:JSON.stringify(dataAuth)},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	        }
	    });
	}
	
</script>
<title>设置数据访问权限</title>
</head>
<body>
	<table width="90%" cellspacing="0" cellpadding="8" align="center" style="margin-top:0px">
		<thead>
			<tr >
                <td colspan="3" align="center">设置帐户 [${account.accountcode }] 对于 [${tree.treename }] 档案节点的数据访问权限。</td>
            </tr>
			<tr align="center">
				<th>字段</th>
				<th>关系符</th>
				<th>值</th>
			</tr>
		</thead>
		<tbody>
		<tr align="center">
			<td>
				<select id="selectField">
					<c:forEach  items="${templetfields}" var="item">
						<c:if test="${item.sort > 0}">
						<option value="${item.englishname }">${item.chinesename }</option>
						</c:if>
				    </c:forEach>
				</select>
			</td>
			<td>
				<select id="oper">
					<option value="equal">等于</option>
					<option value="like">包含</option>
				</select>
			</td>
			<td>
				<input id="dataAuthValue" value="" type="text"/>
			</td>
		</tr>
		<tr>
			<td colspan="3" align="center"><button onclick="saveDataAuth()">保存</button><button type="button" onclick="closepage()">关闭</button></td>
			
		</tr>
		</tbody>
	</table>
</body>
</html>