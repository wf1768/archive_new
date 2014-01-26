<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>

<div class="container" style="margin-top: 80px;">
	<div class="row">
		<div class="col-md-9">
			<form role="form" action="user/toinsert.do" method="post">
				<div class="form-group">
					<label for="username">帐户名</label>
					<input type="text" class="form-control" id="username" name="username" placeholder="请输入帐户名"">
				</div>
				<div class="form-group">
					<label for="password">密码</label>
					<input type="text" class="form-control" id="password" name="password" placeholder="请输入密码"">
				</div>
				<button type="submit" class="btn btn-default btn-sm">保存</button>
				<a href="${pageContext.request.contextPath}/user/list.do" class="btn btn-primary btn-sm">返回</a>
			</form>
		</div>
	</div>
</div>


<%@ include file="/view/common/footer.jsp"%>