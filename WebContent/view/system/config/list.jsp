<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" href="${pageContext.request.contextPath}/css/template.css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/util.js"></script>
<title>${sysname }</title>
<!--[if IE 6]>
<script src="${pageContext.request.contextPath}/js/DD_belatedPNG.js"></script>
<script>
  DD_belatedPNG.fix('.login_bottom,.logo,.l_m_b, tubiao, tubiao1,');
</script>
<![endif]-->
</head>
<body>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>

<script type="text/javascript">
<!--
$(function () {
    $(window).resize(function () {
        setUI();
    }); 
	$(function(){
			   	setUI();
			   });
    setUI();
    function setUI() {

    	var winW = $(window).width();
		var winH = $(window).height();
        var leftW = $("#bodyer_left").outerWidth();
        var leftH = $("#bodyer_left").outerHeight();
		var bottH = $("#footer").outerHeight();
		var topH1 = $("header").outerHeight();
		
        var w = winW - leftW;
        var rw = w -2 + "px";
		var h = winH-topH1-bottH-80;
 
        $("#bodyer_right").height(leftH);
		$("#bodyer_left").height(h);
		$("#fanye").width($("#shuju").height());
		$("#bodyer_right").width(w);
        $("#cssz_table").width(rw);
		$("#sj").height($("#bodyer_right").height() - $("#fanye").height() - 34);
    }
});

function openwindow(id){  
	//var result = window.showModalDialog("edit.do?id="+id,"查看窗口","toolbars=0;location=no;status=no;resizable=no;dialogWidth=700px;dialogHeight=400px;scrollbars=0");
	var url = "edit.do?id="+id + "&time="+Date.parse(new Date());
	var whObj = { width: 600, height: 300 };
	var result = openShowModalDialog(url,null,whObj);
	window.location.reload(true); // 刷新窗体
	
	//if(result==null || result=="undefined"){ // 判断是否异常关闭（点击窗体“关闭”按钮)
	//}else{
	//    window.location.reload(true); // 刷新窗体
	//}
}


//-->
</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img
					src="${pageContext.request.contextPath}/images/i1_03.png"
					width="29" height="22" class="tubiao" /><span>系统配置</span></a>
			</dt>
			<dd>
				<ul>
					<li><a href="${pageContext.request.contextPath}/config/list.do" class="txt2 on"><img
							src="${pageContext.request.contextPath}/images/i_07.png"
							width="18" height="15" class="tubiao1" /><span>参数设置</span></a></li>
					<li><a href="${pageContext.request.contextPath}/docserver/list.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_10.png"
							width="18" height="13" class="tubiao1" /><span>文件服务器</span></a></li>
					<li><a href="#" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_14.png"
							width="18" height="13" class="tubiao1" /><span>索引维护</span></a></li>
				</ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="dqwz">当前位置：系统维护-系统配置-参数设置</div>
		<div class="shuju" id="sj">
			<table id="cssz_table">
				<tr class="textCt ertr  hui title1">
					<td><p>#</p></td>
					<td><p>属性名称</p></td>
					<td><p>属性值</p></td>
					<td><p>属性描述</p></td>
					<td><p>操作</p></td>
				</tr>
				<c:forEach items="${configList}" varStatus="i" var="item">
					<tr class="textCt ertr  ">
						<td>${i.index+1 }</td>
						<td>${item.configname}</td>
						<td><font color="blue">${item.configvalue}</font></td>
						<td>${item.configmemo}</td>
						<td>
							<p>
								<a href="#" onclick="openwindow('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_edit.png" />
								修改</a>
							</p>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="fanye" class="fanye1">
			<p></p>
		</div>
	</div>
	<div style="clear: both"></div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>