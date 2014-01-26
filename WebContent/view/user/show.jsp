
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>


<div class="container" style="margin-top: 80px;">
	<div class="row">
		<div class="col-md-9">
			id:${user.id}<br>
			name:${user.username}<br>
			pass:${user.password}<br>
		</div>
	</div>
	<a href="${pageContext.request.contextPath}/user/list.do">返回列表</a>
</div>
</body>
</html>