<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<link href="${pageContext.request.contextPath}/js/umeditor1_2_2/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jqprint-0.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/print.util.js"></script>
<%-- <script src="${pageContext.request.contextPath}/js/ckeditor/ckeditor.js"></script> --%>
<!-- 配置文件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/umeditor1_2_2/umeditor.config.js"></script>
<!-- 编辑器源码文件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/umeditor1_2_2/umeditor.min.js"></script>
<!-- 语言包文件(建议手动加载语言包，避免在ie下，因为加载语言失败导致编辑器加载失败) -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/umeditor1_2_2/lang/zh-cn/zh-cn.js"></script>
<base target="_self">

<script>

	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	$(function(){
		//$('.t').css("display","none");
		//$('#AJML').removeAttr("style");
		$('#printcode').change(function(){
			var value=$(this).children('option:selected').val();//这就是selected的值
			$('.t').css("display","none");
			$('#'+value).removeAttr("style");
		})
		
		$('.t').css("display","none");
		var templettype = '${templet.templettype}';
		var tabletype = '${tabletype }';
		if (templettype == 'F') {
			$('#JNML').removeAttr("style");
		}
		else if (tabletype=='01') {
			$('#AJML').removeAttr("style");
		}
		else if (tabletype=='02') {
			$('#JNML').removeAttr("style");
		}
		
		//实例化编辑器
	    var um = UM.getEditor('myEditor');
	})
	
	function onPrint() {
        $(".my_show").jqprint({
            importCSS:false,
            debug:false
        });
    }
	
	//创建打印数据
	function create_print_data(printcode,data) {
		var path = '${pageContext.request.contextPath}';
		//辅助参数
		var otherPar = {};
		otherPar.path = path;
		otherPar.data = data;
		//获取打印字段对应
		var field = {};
		if (printcode == 'AJML') {
			field.ajh = $("#AJML_AJH").val();
			field.tm = $("#AJML_TM").val();
			field.ys = $("#AJML_YS").val();
			field.bz = $("#AJML_BZ").val();
		}
		else if (printcode == 'JNML') {
			field.jh = $("#JNML_JH").val();
			field.wjh = $("#JNML_WJH").val();
			field.tm = $("#JNML_TM").val();
			field.wjrq = $("#JNML_WJRQ").val();
			field.ys = $("#JNML_YS").val();
			otherPar.ajh = '${ajh}';
			otherPar.bgqx = '${bgqx}';
		}
		else if (printcode == 'JNBKB') {
			//otherPar.data = data;
		}
		else if (printcode == 'YJDJB') {
			field.nd = $("#YJDJB_ND").val();
			field.yjbm = $("#YJDJB_YJBM").val();
			field.ajh = $("#YJDJB_AJH").val();
			field.tm = $("#YJDJB_TM").val();
			field.bgqx = $("#YJDJB_BGQX").val();
			field.js = $("#YJDJB_JS").val();
		}
		//======
		
		var html = create_print_html(printcode,field,otherPar);
		$("#print_div").html(html);
	}
	
	function print_() {
		
		var printcode = $("#printcode").val();
		if (printcode == 'JNBKB') {
			//var data = CKEDITOR.instances.JNBKB_SM.getData();
			var data = UM.getEditor('myEditor').getContent();
			create_print_data(printcode,data);
			onPrint();
			return;
		}
		
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
		d.searchTxt = '${searchTxt}';
		
		
		d.printcode = $("#printcode").val();
		d.printtype = $('input:radio[name="printtype"]:checked').val();
		
		if (d.printtype == "select_data") {
			if (d.ids == "") {
				$.unblockUI();
				alert("没有选择要打印的档案数据，请重新选择。");
				return;
			}
		}
		else {
			d.ids = "";
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
	<table width="80%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
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
            			<c:choose>
            				<c:when test="${templet.templettype == 'F' }">
            					<option value="JNML">卷内文件目录</option>
            					<option value="JNBKB">卷内备考表</option>
            				</c:when>
            				<c:when test="${tabletype=='01' }">
            					<option value="AJML">案卷目录</option>
            					<option value="YJDJB">档案移交登记表</option>
            					
            				</c:when>
            				<c:when test="${tabletype=='02' }">
            					<option value="JNML">卷内文件目录</option>
            					<option value="JNBKB">卷内备考表</option>
            				</c:when>
            			</c:choose>
            			
            			
            		</select>
            	</td>
            </tr>
		</tbody>
	</table>
	<table class="t" id="AJML" width="80%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
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
	<table class="t" id="YJDJB" width="80%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
            <tr>
                <td class="biaoti" colspan="2" align="center">
                	打印项目配对
                </td>
            </tr>
            <tr >
            	<td>年度</td>
            	<td><input type="text" id="YJDJB_ND" /></td>
            </tr>
            <tr >
            	<td>移交部门</td>
            	<td><input type="text" id="YJDJB_YJBM" /></td>
            </tr>
            <tr >
            	<td>案卷号（卷宗号）</td>
            	<td>
            		<select id="YJDJB_AJH">
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
            	<td>卷宗名称（题名）</td>
            	<td>
            		<select id="YJDJB_TM">
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
            	<td>保管期限</td>
            	<td>
            		<select id="YJDJB_BGQX">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'BGQX'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr>
            	<td>件数</td>
            	<td>
            		<select id="YJDJB_JS">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'JS'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
		</tbody>
	</table>
	<table class="t" id="JNML" width="80%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
            <tr>
                <td class="biaoti" colspan="2" align="center">
                	打印项目配对
                </td>
            </tr>
            <tr >
            	<td>件号（序号）</td>
            	<td>
            		<select id="JNML_JH">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'JH'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
            <tr >
            	<td>文 号</td>
            	<td>
            		<select id="JNML_WJH">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'WJH'?'selected':'' }>${item.chinesename }</option>
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
            	<td>文件日期</td>
            	<td>
            		<select id="JNML_WJRQ">
            			<option value="NOTHING" >空白</option>
						<c:forEach items="${fields}" varStatus="i" var="item">
							<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
								<option value="${item.englishname }" ${item.englishname == 'GDRQ'?'selected':'' }>${item.chinesename }</option>
							</c:if>
						</c:forEach>
					</select>
            	</td>
            </tr>
		</tbody>
	</table>
	<table class="t" id="JNBKB" width="80%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
            <tr>
                <td class="biaoti" colspan="2" align="center">
                	填写卷内备考表打印项目
                </td>
            </tr>
            <tr >
            	<td>本卷情况说明</td>
            	<td>
            		<!--style给定宽度可以影响编辑器的最终宽度-->
				<script type="text/plain" id="myEditor" style="width:500px;height:240px;">
    				<p>请输入备考表内容</p>
				</script>
            		<!-- <textarea id="JNBKB_SM" class="ckeditor" style="height: 20px" cols="30" rows="10"></textarea> -->
            	</td>
            </tr>
		</tbody>
	</table>
	<table width="80%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
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