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
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	$(function(){
	})
	
	function onPrint() {
        $(".my_show").jqprint({
            importCSS:false,
            debug:false
        });
    }
	
	//创建打印数据
	function create_print_data(data) {
		var data_json = JSON.parse(data);
		var html = "";
		for (var i=0;i<Math.ceil(data_json.length/10);i++) {
			if (i == (Math.ceil(data_json.length/10) -1)) {
				html += "<div class=\"my_show\">";
			}
			else {
				html += "<div class=\"my_show\" style=\"page-break-after: always;\">";
			}
			//html += "<div class=\"my_show\" style=\"page-break-after: always;\">";
			html += "<table  cellspacing=\"0\" style=\"border-collapse: collapse; border-spacing: 0;background-color: transparent;max-width: 100%\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
	        html += "<thead>";
	        html += "<tr>";
	        html += "<th colspan=\"5\">";    
	        html += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">";
	        html += "<tr align=\"center\">";
	        html += "<td style=\"text-align:left; font-size: 26px;padding-bottom: 5px;padding-top: 0px\">&nbsp;&nbsp;案 卷 目 录</td>";
	        html += "</tr>";
	        html += "</table>";
			html += "</th>";
			html += "</tr>";
			html += "<tr style=\"border:1px #000 solid;text-align: center;height: 40px\">";
			html += "<th style=\"border:1px #000 solid;\">序号</th>";
			html += "<th style=\"border:1px #000 solid;\">案卷号</th>";
			html += "<th style=\"border:1px #000 solid;\">题名</th>";
			html += "<th style=\"border:1px #000 solid;\">页数</th>";
			html += "<th style=\"border:1px #000 solid;\">备注</th>";
			html += "</tr>";
			html += "</thead>";
			html += "<tbody >";
			
			for (var j=i*10;j<i*10+10;j++) {
				if (j<data_json.length) {
					html += "<tr class=\"content_tr\" style=\"height:30px;border:1px #000 solid;text-align: center\">";
					html += "<td style=\"border:1px #000 solid;\">"+ (j+1) +"</td>";
					html += "<td style=\"border:1px #000 solid;\">"+ data_json[j].AJH +"</td>";
					html += "<td style=\"border:1px #000 solid;\">"+ data_json[j].TM +"</td>";
					html += "<td style=\"border:1px #000 solid;\">"+ data_json[j].YS +"</td>";
					html += "<td style=\"border:1px #000 solid;\"></td>";
				}
				else {
					html += "<tr class=\"content_tr\" style=\"height:30px;border:1px #000 solid;text-align: center\">";
					html += "<td style=\"border:1px #000 solid;\"></td>";
					html += "<td style=\"border:1px #000 solid;\"></td>";
					html += "<td style=\"border:1px #000 solid;\"></td>";
					html += "<td style=\"border:1px #000 solid;\"></td>";
					html += "<td style=\"border:1px #000 solid;\"></td>";
				}
			}
			html += "</tbody>";
			html += "</table>";
	        html += "</div>";
		}
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
				alert("没有选择要打印的档案数据，请重新选择。");
				return;
			}
		}
		
		$.ajax({
	        async : false,
	        url : "${pageContext.request.contextPath}/archive/print.do",
	        type : 'post',
	        data:d,
	        dataType : 'text',
	        success : function(data) {
	        	create_print_data(data);
	        }
	    });
		
		$.unblockUI();
		onPrint();
		/* setTimeout("onPrint();",3000);  */
	}
	
	function print_11() {
		//档案数据对象 
		var d = {};
		//var fieldArray = ${fieldjson};
		
		for (var i=0;i<fieldArray.length;i++) {
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
		
		/* d["treeid"] = $("#treeid").val();
		d["status"] = $("#status").val();
		d["tabletype"] = $("#tabletype").val();
		d["parentid"] = $("#parentid").val(); */
		
		//系统字段对象
		var s = {};
		s.treeid = $("#treeid").val();
		s.status = $("#status").val();
		s.tabletype = $("#tabletype").val();
		s.parentid = $("#parentid").val();
		
		var data = JSON.stringify(d);
		var sys = JSON.stringify(s);
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/archive/save.do",
	        type : 'post',
	        data:{'data':data,'sys':sys},
	        dataType : 'text',
	        success : function(data) {
	        	alert(data);
	            //window.dialogArguments.location.reload();
	        }
	    });
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