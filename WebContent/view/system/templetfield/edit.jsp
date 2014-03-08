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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/Pinyin.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/easy_validator.pack.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/jquery.bgiframe.min.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function update() {
		var id = $("#id").val();
		//var tableid = $("#tableid").val();
		var chinesename = $("#chinesename").val();
		var fieldtype = $("#fieldtype").val();
		var fieldsize = $("#fieldsize").val();
		if (fieldtype == 'VARCHAR') {
			if (fieldsize == "" || fieldsize < 30 || fieldsize > 2000) {
				fieldsize = 300;
			}
		}
		else if (fieldtype == 'INT') {
			fieldsize = 11;
		}
		
		var defaultvalue = $("#defaultvalue").val();
		
		if (fieldtype == 'INT') {
			if(isNaN(defaultvalue)){
				defaultvalue = 0;
			}
		}
		/* var sort = $("#sort").val();
		if (sort == "") {
			sort = 1;
		} */
		/* var isrequire = 0;
		if ($("#isrequire").is(":checked")) {
			isrequire = 1;
		}
		var isunique = 0;
		if ($("#isunique").is(":checked")) {
			isunique = 1;
		} */
		var issearch = 0;
		if ($("#issearch").is(":checked")) {
			issearch = 1;
		}
		var isgridshow = 0;
		if ($("#isgridshow").is(":checked")) {
			isgridshow = 1;
		}
		var iscopy = 0;
		if ($("#iscopy").is(":checked")) {
			iscopy = 1;
		}
		var orderby = $("#orderby").val();
		
		if (chinesename == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		var d = {};
		d.id = id;
		//d.tableid = tableid;
		d.chinesename = chinesename;
		//d.englishname = englishname;
		//d.fieldtype = fieldtype;
		d.fieldsize = fieldsize;
		d.defaultvalue = defaultvalue;
		//d.sort = sort;
		/* d.isrequire = isrequire;
		d.isunique = isunique; */
		d.issearch = issearch;
		d.isgridshow = isgridshow;
		d.iscopy = iscopy;
		d.orderby = orderby;
		//d.ispk = 0;
		//d.isedit = 1;
		//d.iscode = 0;
		//d.issystem = 1;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/update.do",
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
<title>修改字段</title>
</head>
<body>
	<form action="" method="post">
		<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
			<tbody>
				<tr>
	                <td class="biaoti" colspan="2" align="center">修改字段</td>
	            </tr>
				<tr class="tr1">
					<td class="txt1">中文名称 :</td>
					<td><input type="text" id="chinesename" name="chinesename" value="${field.chinesename }" reg="^.+$" tip="中文名称[必须填写] "> * </td>
				</tr>
				<tr class="tr1">
					<td class="txt1">名称 :</td>
					<td>${field.englishname } *<input type="hidden" id="id" name="id" value="${field.id }"></td>
				</tr>
				<tr class="tr1">
					<td class="txt1">类型 :</td>
					<td><input type="hidden" id="fieldtype" name="fieldtype" value="${field.fieldtype }">
						<c:choose>
							<c:when test="${field.fieldtype=='VARCHAR' }">字符串</c:when>
							<c:when test="${field.fieldtype=='INT' }">整数型</c:when>
							<c:otherwise>未知</c:otherwise>
						</c:choose>
						
					</td>
				</tr>
				<tr class="tr1">
					<td class="txt1">长度 :</td>
					<td><input type="text" id="fieldsize" name="fieldsize" value="${field.fieldsize }" reg="^\d+$" tip="大小[必须填写，必须数字,范围［30-2000］，如果不在范围内，系统将取值300] "> *</td>
				</tr>
				<tr class="tr1">
					<td class="txt1">默认值 :</td>
					<td><input type="text" id="defaultvalue" name="defaultvalue" value="${field.defaultvalue }"></td>
				</tr>
				<!-- <tr class="tr1">
					<td class="txt1">必著 :</td>
					<td><input type="checkbox" id="isrequire" name="isrequire" ></td>
				</tr>
				<tr class="tr1">
					<td class="txt1">唯一 :</td>
					<td><input type="checkbox" id="isunique" name="isunique" ></td>
				</tr> -->
				<tr class="tr1">
					<td class="txt1">检索字段 :</td>
					<td><input type="checkbox" id="issearch" name="issearch" ${field.issearch==1?'checked':'' }></td>
				</tr>
				
				<tr class="tr1">
					<td class="txt1">列表显示 :</td>
					<td><input type="checkbox" id="isgridshow" name="isgridshow" ${field.isgridshow==1?'checked':'' }></td>
				</tr>
				<tr class="tr1">
					<td class="txt1">顺带 :</td>
					<td><input type="checkbox" id="iscopy" name="iscopy" ${field.iscopy==1?'checked':'' }>顺带最近一次录入的值</td>
				</tr>
				<tr class="tr1">
					<td class="txt1">数据排序 :</td>
					<td>
						<select id="orderby" name="orderby" style="width: 150px">
							<option value="">可选择</option>
							<option value="ASC" ${field.orderby=='ASC'?'selected':'' }>正序排序</option>
							<option value="DESC" ${field.orderby=='DESC'?'selected':'' }>倒序排序</option>
						</select>
					</td>
				</tr>
				<%-- <tr class="tr1">
					<td class="txt1">排序 :</td>
					<td><input type="text" id="sort" name="sort" value="${field.sort }" reg="^\d+$" tip="大小[必须填写，必须数字] "></td>
				</tr> --%>
				<tr>
					<td class="caozuo" colspan="2" align="center">
						<button type="button" onclick="update()">保存</button>
						<button type="button" onclick="closepage()">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>