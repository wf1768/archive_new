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
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function selectRadio(value) {
		var parentid = $("#parentid").val();
		window.location.href="${pageContext.request.contextPath}/templet/add.do?parentid=" + parentid + "&m="+value+"&time=" + Date.parse(new Date());
	}
	
	function save() {
		var parentid = $("#parentid").val();
		var templetname = $("#templetname").val();
		var copyTempletid = $("#copytemplet").val();
		
		if (parentid == "" || templetname == "" || copyTempletid=="") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		var d = {};
		d.parentid = parentid;
		d.templetname = templetname;
		d.copyTempletid = copyTempletid;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templet/save.do",
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
	}
	
</script>
<title>添加档案类型</title>
</head>
<body>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">
                	添加档案类型
                	<input type="hidden" id="parentid" name="parentid" value="${parentid }">
                </td>
            </tr>
            <tr>
				<td class="txt1">档案类型参考模版 :</td>
				<td>
					<input type="radio" name="ca" value="c" ${m=='c'?'checked':'' } onclick="selectRadio('c')">基础模版<input type="radio" name="ca" value="a" ${m=='a'?'checked':'' } onclick="selectRadio('a')">档案类型
				</td>
			</tr>
			<tr>
				<td class="txt1">档案类型参考模版 :
				</td>
				<td>
					<select id="copytemplet" style="width: 160px">
						<c:forEach  items="${templets}" var="item">
							<option value="${item.id }">${item.templetname }</option>
					    </c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="txt1">档案类型名称 :</td>
				<td><input type="text" id=templetname name="templetname" >* </td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="save()">保存</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>