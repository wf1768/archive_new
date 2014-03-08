<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
		var tableid = $("#tableid").val();
		var chinesename = $("#chinesename").val();
		var englishname = $("#englishname").val();
		
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
		
		if (tableid == "" || chinesename == "" || englishname=="") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		var d = {};
		d.tableid = tableid;
		d.chinesename = chinesename;
		d.englishname = englishname;
		d.fieldtype = fieldtype;
		d.fieldsize = fieldsize;
		d.defaultvalue = defaultvalue;
		//d.sort = sort;
		d.isrequire = 0;
		d.isunique = 0;
		d.issearch = issearch;
		d.isgridshow = isgridshow;
		d.ispk = 0;
		d.isedit = 1;
		d.iscode = 0;
		d.issystem = 1;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/save.do",
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
<title>添加档案类型夹</title>
</head>
<body>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td class="biaoti" colspan="2" align="center">
                	添加档案类型夹
                	<input type="hidden" id="parentid" name="parentid" value="${parentid }">
                </td>
            </tr>
			<tr class="tr1">
				<td class="txt1">夹名称 :</td>
				<td><input type="text" id=templetname name="templetname" >* </td>
			</tr>
			
			<!-- <tr class="tr1">
				<td class="txt1">排序 :</td>
				<td><input type="text" id="sort" name="sort" value="1" reg="^\d+$" tip="大小[必须填写，必须数字] "></td>
			</tr> -->
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