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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dialog_util.js"></script>

<base target="_self">

<script>

	$(function(){
		var result = '${result}';
		if (result != "") {
			alert(result);
		}
		var parentWin=window.dialogArguments;
		//var buttonValue=parentWin.document.getElementByIdx("mybutton2").value;   //获取父窗口中的对象
		//var parentValue=parentWin.nodes;       //获取父窗口中的变量
		
	})
	
	function down(id) {
        var link = "${pageContext.request.contextPath}/doc/download.do?id=" + id;
        //window.open(link);
        window.location.href=link;
        return false;
	}
	
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function upload_pic_single(id) {
		var treeid = '${treeid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再上传多媒体文件。');
			return;
		}
		
		if (id == "") {
			alert('请选择要上传的多媒体文件。');
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/open_upload_pic_single.do?treeid="+treeid+"&tabletype=02&id="+id+"&time=" + Date.parse(new Date());
		var whObj = {
			width : 650,
			height : 300
		};
		var result = openShowModalDialog(url, window, whObj);
		reload();
	}
	
	function setCover(id) {
		var treeid = '${treeid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点，再操作。');
			return;
		}
		
		if (id == "") {
			alert("请先选择要设置为封面的多媒体文件。");
			return;
		}
		
		$.ajax({
			async : false,
			url : "${pageContext.request.contextPath}/archive/setCover.do",
			type : 'post',
			data : {
				'treeid':treeid,
				'tabletype':'02',
				'id' : id
			},
			dataType : 'text',
			success : function(data) {
				alert(data);
			}
		});
	}
	
	function showvideo(id) {
		var treeid = '${treeid}';
		
		if (treeid == '' || treeid == '0') {
			alert('请选择左侧档案节点。');
			return;
		}
		
		if (id == "") {
			alert("请先选择要播放的多媒体文件。");
			return;
		}
		
		var url = "${pageContext.request.contextPath}/archive/showvideo.do?treeid="+treeid+"&tabletype=02&id="+id+"&time=" + Date.parse(new Date());
		var whObj = {
			width : 850,
			height : 600
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
</script>
<title>查看多媒体</title>
</head>
<body>
	<a id="reload" href="" style="display:none">reload...</a>
	<table width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="2" align="center">
                	查看多媒体
                	<input type="hidden" id="id" name="id" value="${maps[0]['id']}">
                	<input type="hidden" id="treeid" name="treeid" value="${treeid }">
                	<input type="hidden" id="tabletype" name="tabletype" value="${tabletype }">
                </td>
            </tr>
            <c:forEach items="${fields}" varStatus="j" var="item">
				<c:if test="${(item.sort > 0) and (item.isedit == 1)}">
					<tr>
						<td width="140px;">${item.chinesename } :</td>
						<td>${maps[0][item.englishname]}</td>
					</tr>
				</c:if>
			</c:forEach>
			<tr>
				<td>多媒体文件名 :</td>
				<td>${maps[0].imgoldname}</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<button type="button" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
	<table width="600" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
				<td align="center">
					<button type="button" onclick="closepage()">关闭</button>
					<c:if test="${tabletype=='02' }">
						<button type="button" onclick="upload_pic_single('${maps[0]['id']}')">上传</button>
						<button type="button" onclick="setCover('${maps[0]['id']}')">设为相册封面</button>
						<c:if test="${maps[0].slttype=='VIDEO' }">
							<button type="button" onclick="showvideo('${maps[0]['id']}')">播放多媒体</button>
						</c:if>
					</c:if>
				</td>
			</tr>
			<tr style="width:500px">
                <td align="center">
                	<c:set var="slt" value="${maps[0].slt }"></c:set>
                   	<c:set var="slttype" value="${maps[0].slttype}"></c:set>
					<c:choose>
						<c:when test="${fn:length(slt) == 0 }">
							<img class="tip" style="z-index:1;" src="${pageContext.request.contextPath}/images/no_photo_135.png" height="170" width="220"/>
						</c:when>
						<c:when test="${slttype == 'VIDEO' }">
							<img class="tip" title="${maps[0].sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/file/pic/video.jpg" />
						</c:when>
						<c:when test="${slttype == 'OTHER' }">
							<img class="tip" title="${maps[0].sltname }" style="z-index:1;" src="${pageContext.request.contextPath}/images/no_photo_135.png" />
						</c:when>
						<c:otherwise>
							<img class="tip" title="${maps[0].sltname }" style="z-index:1;width: 80%;" src="${pageContext.request.contextPath}/${slt}" />
						</c:otherwise>
					</c:choose>
                </td>
            </tr>
			
		</tbody>
	</table>
</body>
</html>