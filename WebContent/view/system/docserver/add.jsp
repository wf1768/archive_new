<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/easyvalidator/css/validate.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/easy_validator.pack.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyvalidator/js/jquery.bgiframe.min.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	$(function() {
		
		//获取传来的值
		var result = '${result }';
		//如果返回值不为空，说明保存了，弹出提示，刷新父页面
		if (result != "") {
			alert(result);
		}
		//如果返回了对象
		var serverType = '${docserver.servertype}';
		
		if (serverType != "") {
			//服务器类型为local,显示local的form
			if (serverType == "LOCAL") {
				$("#ftpform").css("display","none");
				$('#localform').removeAttr("style");
				$("#servertype_l").val("LOCAL");
			}
			else if (serverType == "FTP") {
				$("#localform").css("display","none");
				$('#ftpform').removeAttr("style");
				$("#servertype_f").val("FTP");
			}
			else {
				
			}
		}
		else {
			$("#servertype_l").val("LOCAL");
		}
		
		
		
		$('.s').change(function(){
			var value=$(this).children('option:selected').val();//这就是selected的值
			if (value == "FTP") {
				$("#localform").css("display","none");
				$('#ftpform').removeAttr("style");
				$("#servertype_f").val("FTP");
			}
			else if (value == "LOCAL") {
				$("#ftpform").css("display","none");
				$('#localform').removeAttr("style");
				$("#servertype_l").val("LOCAL");
			}
			else {
				
			}
		})
	})
	
	
</script>
<title>添加电子文件服务器</title>
</head>
<body>
	<form id="localform" action="save.do" method="post">
		<table width="90%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
			<tbody>
				<tr >
	                <td colspan="2" align="center">添加服务器</td>
	            </tr>
				<tr>
					<td>服务器类型 :</td>
					<td>
						<select class="s" id="servertype_l" name="servertype" reg="[^0]" tip="请先选择创建服务器类型">
							<option value="FTP">FTP服务器</option>
							<option value="LOCAL">服务器本地目录</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>服务器名称 :</td>
					<td>
						<input name="servername" type="text" id="servername" value="${docserver.servername }" reg="^.+$" tip="服务器名称[必须填写] " />
					</td>
				</tr>
				<tr>
					<td>服务器路径 :</td>
					<td>
						<input name="serverpath" type="text" id="serverpath" value="${docserver.serverpath }" reg="^.+$" tip="服务器路径[必须填写] " />
					</td>
				</tr>
				<tr>
					<td>服务器描述 :</td>
					<td>
						<input name="servermemo" type="text" id="servermemo" value="${docserver.servermemo }" tip="服务器描述[不必须填写] " />
					</td>
				</tr>
				<tr>
					<td  colspan="2" align="center">
						<input type="submit" value="保存" class="save" />
						<input type="button" value="关闭" class="close" onclick="closepage()">
					</td>
				</tr>
			</tbody>
		</table>
	</form>
	<form id="ftpform" style="display: none;" action="save.do" method="post">
		<table width="90%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
			<tbody>
				<tr >
	                <td colspan="2" align="center">添加服务器</td>
	            </tr>
				<tr>
					<td>服务器类型 :</td>
					<td>
						<select class="s" id="servertype_f" name="servertype" reg="[^0]" tip="请先选择创建服务器类型">
							<option value="FTP">FTP服务器</option>
							<option value="LOCAL">服务器本地目录</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>服务器名称 :</td>
					<td>
						<input name="servername" type="text" id="servername" value="${docserver.servername }" reg="^.+$" tip="服务器名称[必须填写] " />
					</td>
				</tr>
				<tr>
					<td>服务器IP :</td>
					<td>
						<input name="serverip" type="text" id="serverip" value="${docserver.serverip }" reg="^.+$" tip="服务器IP地址[必须填写] " />
					</td>
				</tr>
				<tr>
					<td>FTP帐户 :</td>
					<td>
						<input name="ftpuser" type="text" id="ftpuser" value="${docserver.ftpuser }" reg="^.+$" tip="FTP服务器登录帐户名[必须填写] " />
					</td>
				</tr>
				<tr>
					<td>FTP密码 :</td>
					<td>
						<input name="ftppassword" type="text" id="ftppassword" value="${docserver.ftppassword }" reg="^.+$" tip="FTP服务器登录密码[必须填写] " />
					</td>
				</tr>
				<tr>
					<td>FTP端口 :</td>
					<td>
						<input name="serverport" type="text" id="serverport" value="${docserver.serverport }" reg="^.+$" value="21" tip="FTP服务器端口[必须填写] " />
					</td>
				</tr>
				<tr>
					<td>服务器描述 :</td>
					<td>
						<input name="servermemo" type="text" id="servermemo" value="${docserver.servermemo }" tip="服务器描述[不必须填写] " />
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="submit" value="保存" class="save"/>
						<input type="button" value="关闭" class="close" onclick="closepage()">
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>