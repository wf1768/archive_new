<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%-- <%@ include file="/view/common/top_second_menu.jsp"%> --%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<script type="text/javascript">

function openwindow(id){  
	//var result = window.showModalDialog("edit.do?id="+id,"查看窗口","toolbars=0;location=no;status=no;resizable=no;dialogWidth=700px;dialogHeight=400px;scrollbars=0");
	var url = "edit.do?id="+id + "&time="+Date.parse(new Date());
	var whObj = { width: 600, height: 300 };
	var result = openShowModalDialog(url,null,whObj);
	window.location.reload(true); // 刷新窗体
	
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
					<li><a href="${pageContext.request.contextPath}/config/docauthlist.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_10.png"
							width="18" height="13" class="tubiao1" /><span>电子文件权限</span></a></li>
					<li><a href="${pageContext.request.contextPath}/index/index.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_14.png"
							width="18" height="13" class="tubiao1" /><span>索引维护</span></a></li>
				</ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:999; ">
			<div class="dqwz">当前位置：系统维护-系统配置-参数设置</div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; "> 
			<a href="${pageContext.request.contextPath}/index/createIndex.do">创建索引</a>
		</div>
	</div>
	<div style="clear: both"></div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>