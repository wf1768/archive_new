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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/lhgcalendar/lhgcalendar.min.js"></script>
<base target="_self">

<script>
	function closepage2() {
		window.returnValue="ok";
		window.close();
	}
	
	function save() {
		
		var d = {};
		var fieldArray = ${fieldjson};
		
		for (var i=0;i<fieldArray.length;i++) {
			if (fieldArray[i].sort > 0 && fieldArray[i].isedit == 1) {
				var val = $("#"+fieldArray[i].englishname).val();
				if (fieldArray[i].fieldtype == 'INT') {
					if (val == "") {
						val = 0;
					}
					if(isNaN(val)){
						val = 0;
					}
				}
				d[fieldArray[i].englishname] = val;
			}
		}
		d["id"] = $("#id").val();
		d["treeid"] = $("#treeid").val();
		var tabletype = $("#tabletype").val();
		
		var myData = JSON.stringify(d); 
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/archive/update.do",
	        type : 'post',
	        data:{'data':myData,'tabletype':tabletype},
	        dataType : 'text',
	        success : function(data) {
	        	alert(data);
	        }
	    });
	}
	
</script>
<title>修改档案</title>
</head>
<body>
	<form action="" method="post">
		<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
			<tbody>
				<tr>
	                <td class="biaoti" colspan="2" align="center">
	                	修改档案
	                	<input type="hidden" id="id" name="id" value="${maps[0]['id']}">
	                	<input type="hidden" id="treeid" name="treeid" value="${treeid }">
	                	<input type="hidden" id="tabletype" name="tabletype" value="${tabletype }">
	                </td>
	            </tr>
	            <c:forEach items="${fields}" varStatus="j" var="item">
					<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
						<tr>
							<td>${item.chinesename } :</td>
							<c:choose>
								<c:when test="${item.iscode == 0 }">
									<c:choose>
										<c:when test="${(item.fieldtype == 'VARCHAR') or (item.fieldtype == 'INT') }">
											<td><input style="width: 200px" type="text" id="${item.englishname }" name="${item.englishname }" value="${maps[0][item.englishname]}"> </td>			
										</c:when>
										<c:when test="${item.fieldtype == 'DATE'}">
											<script>
												$(function(){
													$('#${item.englishname }_date').calendar({ id:'#${item.englishname }' });
												})
											</script>
											<td>
												<input type="text" id="${item.englishname }" name="${item.englishname }" value="${maps[0][item.englishname]}" >
												<img id="${item.englishname }_date" align="absmiddle" src="${pageContext.request.contextPath}/images/icons/iconDate.gif">
											</td>
										</c:when>
									</c:choose>
								</c:when>
								<c:when test="${item.iscode == 1 }">
									<td>
										<select id="${item.englishname }" name="${item.englishname }" style="width: 200px">
											<c:forEach  items="${codeMap[item.id]}" var="code">
												<option value="${code.columndata }" ${maps[0][item.englishname] == code.columndata?"selected":""}>${code.columndata }</option>
										    </c:forEach>
										</select>
									</td>
								</c:when>
							</c:choose>
						</tr>
					</c:if>
				</c:forEach>
				<tr>
					<td colspan="2" align="center">
						<button type="button" onclick="save()">保存</button>
						<button type="button" onclick="closepage2()">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>