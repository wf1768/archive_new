<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>


<div class="container" style="margin-top: 80px;">
	<div class="row">
		<div class="col-md-9">
			<form role="form" action="update.do" method="post">
				<input type="hidden" id="id" name="id" value="${account.id }">
				<div class="form-group">
					<label for="username">帐户名</label> <input type="text"
						class="form-control" id="username" name="username"
						placeholder="请输入帐户名" value="${account.username }">
				</div>
				<div class="form-group">
					<label for="password">密码</label> <input type="password"
						class="form-control" id="password" name="password"
						placeholder="密码" value="${account.password }">
				</div>
				<button type="submit" class="btn btn-default btn-sm">保存</button>
				<a href="${pageContext.request.contextPath}/user/list.do"
					class="btn btn-primary btn-sm">返回</a>
			</form>
		</div>
	</div>
</div>

<%@ include file="/view/common/footer.jsp"%>