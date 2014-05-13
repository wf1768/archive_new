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