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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jqprint-0.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/print.util.js"></script>
<base target="_self">

<script>

	var ajh = '${ajh}';
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	$(function(){
		$('.t').css("display","none");
		$('#AJML').removeAttr("style");
		$('#printcode').change(function(){
			var value=$(this).children('option:selected').val();//这就是selected的值
			$('.t').css("display","none");
			$('#'+value).removeAttr("style");
		})
	})
	
	function onPrint() {
        $(".my_show").jqprint({
            importCSS:false,
            debug:false
        });
    }
	
	//创建打印数据
	function create_print_data(printcode,data) {
		//获取打印字段对应
		var field = {};
		if (printcode == 'AJML') {
			field.ajh = $("#AJML_AJH").val();
			field.tm = $("#AJML_TM").val();
			field.ys = $("#AJML_YS").val();
			field.bz = $("#AJML_BZ").val();
		}
		else if (printcode == 'JNML') {
			field.jnxh = $("#JNML_JNXH").val();
			field.tm = $("#JNML_TM").val();
			field.ys = $("#JNML_YS").val();
			field.bz = $("#JNML_BZ").val();
		}
		var html = create_print_html(printcode,field,data,ajh);
		$("#print_div").html(html);
	}
	
	function print_() {
		$.blockUI({
			message:"正在准备打印数据，请稍候...",
			css: {
                padding: '15px',
                width:"300px"
            } 
        });
		
		var d = {};
		d.treeid = '${treeid }';
		d.tabletype = '${tabletype }';
		d.parentid = '${parentid }';
		d.ids = '${ids}';
		
		
		d.printcode = $("#printcode").val();
		d.printtype = $('input:radio[name="printtype"]:checked').val();
		
		if (d.printtype == "select_data") {
			if (d.ids == "") {
				$.unblockUI();
				alert("没有选择要打印的档案数据，请重新选择。");
				return;
			}
		}
		
		setTimeout(function () {  
			$.ajax({
		        async : false,
		        url : "${pageContext.request.contextPath}/archive/print.do",
		        type : 'post',
		        data:d,
		        dataType : 'text',
		        success : function(data) {
		        	create_print_data(d.printcode,data);
		        }
	    	});
			$.unblockUI();
			onPrint();
		},200); 
		
		
		/* setTimeout("onPrint();",3000);  */
	}
	
</script>
<title>打印</title>
</head>
<body>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td class="biaoti" colspan="2" align="center">
                	打印
                </td>
            </tr>
            <tr>
            	<td>打印项目</td>
            	<td>
            		<select id="printcode">
            			<option value="AJML">案卷目录</option>
            			<option value="JNML">文件卷内目录</option>
            		</select>
            	</td>
            </tr>
		</tbody>
	</table>
	<table class="t" id="AJML" width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
            <tr>
                <td class="biaoti" colspan="2" align="center">
                	打印项目配对
                </td>
            </tr>
            <tr >
            	<td>案卷号</td>
            	<td>
            		<select id="AJML_AJH">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'AJH'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr>
            	<td>题名</td>
            	<td>
            		<select id="AJML_TM">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'TM'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr>
            	<td>页数</td>
            	<td>
            		<select id="AJML_YS">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'YS'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr>
            	<td>备注</td>
            	<td>
            		<select id="AJML_BZ">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'BZ'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
		</tbody>
	</table>
	<table class="t" id="JNML" width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
            <tr>
                <td class="biaoti" colspan="2" align="center">
                	打印项目配对
                </td>
            </tr>
            <tr >
            	<td>卷内序号</td>
            	<td>
            		<select id="JNML_JNXH">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'JNXH'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr>
            	<td>题名</td>
            	<td>
            		<select id="JNML_TM">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'TM'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr>
            	<td>页数</td>
            	<td>
            		<select id="JNML_YS">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'YS'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr>
            	<td>备注</td>
            	<td>
            		<select id="JNML_BZ">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'BZ'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
		</tbody>
	</table>
	<table width="400" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td class="biaoti" colspan="2" align="center">
                	打印范围
                </td>
            </tr>
            <tr>
            	<td>打印范围</td>
            	<td>
            		<input type="radio" name="printtype" value="select_data" checked="checked" />选择的数据
					<input type="radio" name="printtype" value="all_data" />全部数据
            	</td>
            </tr>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="print_()">打印</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
</body>

<style>
    .print_font {
        font-size: 10px;
        margin:0px 0px 10px 0px;
    }
    .tr_height {
        height: 20px;
    }
    .foorer_font {
        font-size: 12px;
        font-weight:blod;
    }

    .content_tr {}

    .content_tr td {
        height: 10px;
        overflow: hidden;
        font-size: 12px;
    }
</style>
        
<div id="print_div" style="height:0px;width:0px;overflow:hidden">
    
</div>

</html>