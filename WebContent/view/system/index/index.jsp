<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%-- <%@ include file="/view/common/top_second_menu.jsp"%> --%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript">

	function lodding(){
		$.blockUI({
			message:"正在进行加载，请稍候...",
			css: {
		        padding: '15px',
		        width:"300px"
		    } 
		}); 
	}
	function submit() {
		lodding(); 
		setTimeout(function () { 
			$.ajax({
				async : false,
				url: "${pageContext.request.contextPath}/index/createIndex.do",
				type : 'post',
				dataType : 'text',
				success : function(data) {
					$.unblockUI();
				}
			});
		},200);  
	}
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
					<li><a href="${pageContext.request.contextPath}/config/list.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_07.png"
							width="18" height="15" class="tubiao1" /><span>参数设置</span></a></li>
					<li><a href="${pageContext.request.contextPath}/docserver/list.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_10.png"
							width="18" height="13" class="tubiao1" /><span>文件服务器</span></a></li>
					<li><a href="${pageContext.request.contextPath}/config/docauthlist.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_10.png"
							width="18" height="13" class="tubiao1" /><span>电子文件权限</span></a></li>
					<li><a href="${pageContext.request.contextPath}/index/index.do" class="txt2 on"><img
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
			<a href="javascript:;" onclick="submit();" style=" width:120px; height:30px; color:#FFF; text-align:center; line-height:30px; background:#08408B; display:block; border-radius:3px; margin-left:5px;">创建索引</a>
		</div>
	</div>
	<div style="clear: both"></div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>