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
	
	function save() {
		var id = $("#id").val();
		var treename = $("#treename").val();
		
		if (id == "" || treename == "" ) {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		var d = {};
		d.id = id;
		d.treename = treename;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/tree/update.do",
	        type : 'post',
	        data:d,
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!");
	            }
	            window.dialogArguments.location.reload();
	        }
	    });
	}
	
</script>
<title>${treetype=='FT'?'修改档案树夹':'修改档案树节点' }</title>
</head>
<body>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td class="biaoti" colspan="2" align="center">
                	${treetype=='FT'?'修改档案树夹':'修改档案树节点' }
                	<input type="hidden" id="id" name="id" value="${tree.id }">
                </td>
            </tr>
			<tr class="tr1">
				<td class="txt1">${treetype=='FT'?'档案树夹名称':'档案树节点名称' } :</td>
				<td><input type="text" id="treename" name="treename" value="${tree.treename }">* </td>
			</tr>
			<tr>
				<td class="caozuo" colspan="2" align="center">
					<button type="button" onclick="save()">保存</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>