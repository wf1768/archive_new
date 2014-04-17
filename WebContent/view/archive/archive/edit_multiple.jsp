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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs/jquery.easytabs.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dialog_util.js"></script>
<base target="_self">

<style type="text/css">
	.etabs { margin: 0; padding: 0; }
    .tab { display: inline-block; zoom:1; *display:inline; background: #eee; border: solid 1px #999; border-bottom: none; -moz-border-radius: 4px 4px 0 0; -webkit-border-radius: 4px 4px 0 0; }
    .tab a { font-size: 14px; line-height: 2em; display: block; padding: 0 10px; outline: none; text-decoration: none;}
    .tab a:hover { text-decoration: underline; }
    .tab.active { background: #fff; padding-top: 6px; position: relative; top: 1px; border-color: #666; }
    .tab a.active { font-weight: bold; }
    .tab-container .panel-container { background: #fff; border: solid #666 1px; padding: 10px; -moz-border-radius: 0 4px 4px 4px; -webkit-border-radius: 0 4px 4px 4px; }
    .panel-container { margin-bottom: 10px; }
    
    .notcss {border: 0}
    
    .selected {
		background:#b8e4f9;
	}
</style>

<script>
	
	$(function() {
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
		$('#tab-container').easytabs();
		
		$('#tc_date').calendar({ id:'#tc' });
		
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
<title>批量修改档案</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<div id="tab-container" class="tab-container">
		<ul class='etabs'>
			<li class='tab'><a href="#tabs-tc">填充</a></li>
			<li class='tab'><a href="#tabs-xl">序列</a></li>
		</ul>
		<div class='panel-container'>
			<div id="tabs-tc">
				<h4>为选择的字段属性，填充内容。(可以选择部分档案进行批量修改，不选择等于全部修改)</h4>
				<table class="notcss" border="0" cellpadding="4" cellspacing="1">
					<tr>
						<td class="notcss">选择字段:</td>
						<td class="notcss">
							<select id="tc_th_field">
								<c:forEach items="${fields}" varStatus="i" var="item">
									<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
										<option value="${item.englishname }">${item.chinesename }</option>
									</c:if>
								</c:forEach>
							</select>
						</td>
						<td class="notcss">
							<input type="radio" name="tc_radio" value="tc" checked>填充 <input type="text" id="tc">
							<img id="tc_date" align="absmiddle" src="${pageContext.request.contextPath}/images/icons/iconDate.gif">
						</td>
						<td class="notcss">
							<button type="button" onclick="update_tc()">更改</button>
							<button type="button" onclick="closepage()">关闭</button>
						</td>
					</tr>
					<tr>
						<td class="notcss"></td>
						<td class="notcss"></td>
						<td class="notcss">
							<input type="radio" name="tc_radio" value="th">替换
							<input type="text" id="th_key" > 为 <input type="text" id="th_value" >
						</td>
						<td class="notcss"></td>
					</tr>
					<tr>
						<td class="notcss"></td>
						<td class="notcss"></td>
						<td class="notcss">
							<input type="radio" name="tc_radio" value="zk">置空
						</td>
						<td class="notcss"></td>
					</tr>
					<tr>
						<td class="notcss"></td>
						<td class="notcss"></td>
						<td class="notcss">
							<input type="radio" name="tc_radio" value="gj">高级
							<select id="gj_first">
								<option value="" >请选择</option>
								<c:forEach items="${fields}" varStatus="i" var="item">
									<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
										<option value="${item.englishname }">${item.chinesename }</option>
									</c:if>
								</c:forEach>
							</select> + 
							<input type="text" id="gj_txt"> + 
							<select id="gj_second">
								<option value="" >请选择</option>
								<c:forEach items="${fields}" varStatus="i" var="item">
									<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
										<option value="${item.englishname }">${item.chinesename }</option>
									</c:if>
								</c:forEach>
							</select>
						</td>
						<td class="notcss"></td>
					</tr>
				</table>
			</div>
			<div id="tabs-xl">
				<h4>为选择的字段属性，生成序列流水编号。(可以选择部分档案进行批量修改，不选择等于全部修改。起始值和步长只能为数字)</h4>
				<table class="notcss" border="0" cellpadding="4" cellspacing="1">
					<tr>
						<td class="notcss">选择字段:</td>
						<td class="notcss">
							<select id="xl_th_field">
								<c:forEach items="${fields}" varStatus="i" var="item">
									<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
										<option value="${item.englishname }">${item.chinesename }</option>
									</c:if>
								</c:forEach>
							</select>
						</td>
						<td class="notcss">
							起始值 
							<input type="text" value="1" id="xl_begin">
							 步长
							<input type="text" value="1" id="xl_size">
							
						</td>
						<td class="notcss">
							<button type="button" onclick="update_xl()">更改</button>
							<button type="button" onclick="closepage()">关闭</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
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