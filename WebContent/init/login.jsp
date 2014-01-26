<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统初始化登录</title>
</head>
<body>

	<form action="onlogin.do" method="post" />
	<fieldset>
		<div class="control-group">
			<label class="control-label" for="username">帐户名</label>
			<div class="controls">
				<input type="text" class="" name="username" placeholder="请输入帐户名"
					value="admin" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="password">密码</label>
			<div class="controls">
				<input type="password" class="" name="password" placeholder="请输入密码。"
					value="password" />
			</div>
		</div>
	</fieldset>
	<div id="remember-me" class="pull-left">
		<p style="color: red;">${result }</p>
	</div>
	<div class="pull-right">
		<button type="submit" class="btn btn-success">登录</button>
	</div>
	</form>
</body>
</html>