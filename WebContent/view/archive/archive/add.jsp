<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/easyvalidator/css/validate.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/Pinyin.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/easy_validator.pack.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/jquery.bgiframe.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/lhgcalendar/lhgcalendar.min.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	$(function(){
	})
	
	function save() {
		
		var d = {};
		var fieldArray = ${fieldjson};
		
		for (var i=0;i<fieldArray.length;i++) {
			var val = $("#"+fieldArray[i].englishname).val();
			if (fieldArray[i].fieldtype == 'INT') {
				if(isNaN(val)){
					val = 0;
				}
			}
			d[fieldArray[i].englishname] = val;
		}
		
		d["treeid"] = $("#treeid").val();
		d["status"] = $("#status").val();
		d["tabletype"] = $("#tabletype").val();
		d["parentid"] = $("#parentid").val();
		
		var myData = JSON.stringify(d); 
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/archive/save.do",
	        type : 'post',
	        data:{'str':myData},
	        dataType : 'text',
	        success : function(data) {
	        	alert(data);
	            window.dialogArguments.location.reload();
	        }
	    });
	}
	
</script>
<title>添加档案</title>
</head>
<body>
	<form action="" method="post">
		<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
			<tbody>
				<tr>
	                <td class="biaoti" colspan="2" align="center">
	                	添加档案
	                	<input type="hidden" id="treeid" name="treeid" value="${treeid }">
	                	<input type="hidden" id="status" name="status" value="${status }">
	                	<input type="hidden" id="tabletype" name="tabletype" value="${tabletype }">
	                	<input type="hidden" id="parentid" name="parentid" value="${parentid }">
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
											<td><input style="width: 200px" type="text" id="${item.englishname }" name="${item.englishname }" value="${item.defaultvalue == ''?'':item.defaultvalue }"> </td>			
										</c:when>
										<c:when test="${item.fieldtype == 'DATE'}">
											<script>
												$(function(){
													$('#${item.englishname }_date').calendar({ id:'#${item.englishname }' });
												})
											</script>
											<td>
												<input type="text" id="${item.englishname }" name="${item.englishname }" >
												<img id="${item.englishname }_date" align="absmiddle" src="${pageContext.request.contextPath}/images/icons/iconDate.gif">
											</td>
										</c:when>
									</c:choose>
								</c:when>
								<c:when test="${item.iscode == 1 }">
									<td>
										<select id="${item.englishname }" name="${item.englishname }" style="width: 200px">
											<c:forEach  items="${codeMap[item.id]}" var="item">
												<option value="${item.id }">${item.columndata }</option>
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
						<button type="button" onclick="closepage()">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>