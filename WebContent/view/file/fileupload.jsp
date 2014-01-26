<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>

<div class="container" style="margin-top: 80px;">
	<div class="row">
		<div class="col-md-12">
			<form method="post" action="file/upload.do" enctype="multipart/form-data">
				<input type="file" name="file" /> <input type="submit" class="btn btn-primary btn-xs" value="上传"/>
			</form>
		</div>
	</div>
</div>


<%@ include file="/view/common/footer.jsp"%>