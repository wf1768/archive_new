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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dialog_util.js"></script>
<base target="_self">

<style type="text/css">
    .panel-container { margin-bottom: 10px; }
    
    .notcss {border: 0}
    
    .selected {
		background:#b8e4f9;
	}
</style>

<script>
	
	$(function() {
		var info = '${failure}';
		if (info != "") {
			alert(info);
		}
		$('#checkall').click(function(){
		    $('input[name="checkbox"]').attr("checked",this.checked);
		    if (this.checked) {
		    	$('input[name="checkbox"]').parents('tr').addClass('selected');
		    }
		    else {
		    	$('input[name="checkbox"]').parents('tr').removeClass("selected");
		    }
		});
		
		$('input[type="checkbox"]').removeAttr("checked");
		
		$('.shiftCheckbox').shiftcheckbox();
		
		$('input[name="checkbox"]').click(function(){
		    if (this.checked) {
		    	$(this).parents('tr').addClass('selected');
            }  
            else {  
            	$(this).parents('tr').removeClass("selected");
            }
		});
	})

	/**
	 * 去掉空格
	 * 
	 * @param str
	 * @returns
	 */
	function Trims(str) {
		return str.replace(/(^\s*)|(\s*$)/g, "");
	}

	function closepage() {
		window.returnValue = "ok";
		window.close();
	}
	
	function update(d) {
		//判断是更新选择的数据，还是全部更新
		var confirmStr = "";
		//获取选择的数据
		var str = "";
		$("input[name='checkbox']:checked").each(function() {
			str += $(this).val() + ",";
		});

		if (str == "") {
			confirmStr = "[全部]";
			$("input[name='checkbox']").each(function() {
				str += $(this).val() + ",";
			});
		} else {
			confirmStr = "[选择的部分]";
		}

		if (str != "") {
			str = str.substring(0, str.length - 1);
		}
		
		d.ids = str;
		d.treeid = '${treeid}';
		d.tabletype = '${tabletype}';

		var myData = JSON.stringify(d);

		if (confirm("确定要批量修改" + confirmStr + "档案吗？修改将是不可逆转的。请谨慎操作。")) {
			$.blockUI({
				message : "正在进行批量修改，请稍候...",
				css : {
					padding : '15px',
					width : "300px"
				}
			});

			setTimeout(
					function() {
						$.ajax({
							async : false,
							url : "${pageContext.request.contextPath}/archive/update_multiple.do",
							type : 'post',
							data : {
								'data' : myData
							},
							dataType : 'text',
							success : function(data) {
								alert(data);
							}
						});
						$.unblockUI();
						reload();
					}, 200);
		}
	}
	//验证数字
	function validate(value) {
		var reg = new RegExp("^[0-9]*$");
		if (!reg.test(value)) {
			return false;
		}
		if (!/^[0-9]*$/.test(value)) {
			return false;
		}
		return true;
	}

	function update_xl() {
		var d = {};
		var xl_th_field = $("#xl_th_field").val();
		d.tc_th_field = xl_th_field;
		d.tc_radio = "xl";

		var xl_begin = $("#xl_begin").val();
		var xl_size = $("#xl_size").val();

		if (!validate(xl_begin)) {
			alert("起始值只能为数字。");
			$("#xl_begin").focus();
			return;
		}
		
		if (!validate(xl_size)) {
			alert("步长只能为数字。");
			$("#xl_size").focus();
			return;
		}
		d.xl_begin = xl_begin;
		d.xl_size = xl_size;
		update(d);
	}

	function update_tc() {

		var tc_radio = $('input[name="tc_radio"]:checked').val();

		var d = {};
		var tc_th_field = $("#tc_th_field").val();
		d.tc_th_field = tc_th_field;
		d.tc_radio = tc_radio;

		if (tc_radio == "tc") {
			var tc = $("#tc").val();

			if (Trims(tc) == "") {
				alert("请输入填充的字符。");
				$("#tc").focus();
				return;
			}
			d.tc = tc;
		} else if (tc_radio == "zk") {
			var tc = "";
		} else if (tc_radio == "th") {
			var th_key = $("#th_key").val();
			var th_value = Trims($("#th_value").val());
			if (Trims(th_key) == "") {
				alert("请输入需要替换的字符。");
				$("#th_key").focus();
				return;
			}

			d.th_key = th_key;
			d.th_value = th_value;
		} else if (tc_radio == "gj") {
			var gj_first = $("#gj_first").val();
			var gj_txt = $("#gj_txt").val();
			var gj_second = $("#gj_second").val();

			if (gj_first == "" && gj_txt == "" && gj_second == "") {
				alert("高级修改，至少要选择一项修改内容。");
				return;
			}
			d.firstField = gj_first;
			d.txt = gj_txt;
			d.secondField = gj_second;
		}

		update(d);
	}
</script>
<title>Excel导入数据</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<div>
		<h4>请上传Excel数据文件(注意：仅支持Excel2003格式，即扩展名为xls文件，如果是xlsx格式，请先另存为xls格式。)</h4>
		<form method="post" action="upload.do" enctype="multipart/form-data">
		<table class="notcss" border="0" cellpadding="4" cellspacing="1">
			<tr>
				<td class="notcss">选择Excel文件:</td>
				<td class="notcss">
					<input type="file" name="file" />
					<input type="hidden" value="${treeid }" id="treeid" name="treeid" />	
					<input type="hidden" value="${tabletype }" id="tabletype" name="tabletype" />	
					<input type="hidden" value="${status }" id="status" name="status" />	
					<input type="hidden" value="${parentid }" id="parentid" name="parentid" />	
				</td>
				<td class="notcss">
					<button type="submit">上传</button>
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</table>
		</form>
	</div>
	<div style="width: 98%;height: 300px;min-width:600px;overflow: auto;">
		<table id="data_table" class="data_table table-Kang" aline="left" width="98%"
				border=0 cellspacing="1" cellpadding="4">
			<thead>
				<tr class="tableTopTitle-bg">
					<td width="30px"><input type="checkbox" id="checkall"></td>
					<td width="40px">行号</td>
					<td width="40px">全文</td>
					<c:forEach items="${fields}" varStatus="i" var="item">
						<c:if test="${(item.sort > 0) and (item.isgridshow == 1)}">
							<td>${item.chinesename }</td>
						</c:if>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${maps}" varStatus="i" var="archiveitem">
					<tr class="table-SbgList">
						<td><input type="checkbox" name="checkbox" value="${archiveitem.id }" class="shiftCheckbox"></td>
						<td>${pagebean.pageSize*(pagebean.pageNo-1) + i.index+1 }</td>
						<c:choose>
							<c:when test="${archiveitem['isdoc'] == 1 }">
								<td><a title="电子全文" href="javascript:;"><img src="${pageContext.request.contextPath }/images/icons/attach.png" ></a></td>
							</c:when>
							<c:otherwise>
								<td></td>
							</c:otherwise>
						</c:choose>
						
						<c:forEach items="${fields}" varStatus="j" var="fielditem">
							<c:if test="${(fielditem.sort > 0) and (fielditem.isgridshow == 1)}">
							<td title="${archiveitem[fielditem.englishname] }">
							<c:choose>
								<c:when test="${fielditem.fieldtype =='VARCHAR' }">
									<c:set var="subStr" value="${archiveitem[fielditem.englishname]}"></c:set>
									<c:choose>
										<c:when test="${fn:length(subStr) > subString }">
											${fn:substring(archiveitem[fielditem.englishname], 0, subString)}..
										</c:when>
										<c:otherwise>
									      	${archiveitem[fielditem.englishname]}
									    </c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									${archiveitem[fielditem.englishname]}
								</c:otherwise>
							</c:choose>
								
							</td>
							</c:if>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>