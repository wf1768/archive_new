<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>


<div class="container" style="margin-top: 80px;">
	<div class="row">
		<div class="col-md-9">
			<form role="form" action="update.do" method="post">
				<input type="hidden" name="id" value="${info.id }">
				<input type="hidden" name="columnid" value="${info.columnid }">
				<div class="form-group">
					<label for="title">信息标题</label>
					<input type="text" class="form-control" id="title" name="title" placeholder="请输入信息标题" value="${info.title }">
				</div>
				<div class="form-group">
					<label for="">信息内容</label>
					<textarea class="ckeditor" cols="80" id="content" name="content" rows="50">${info.content }</textarea>
				</div>
				<div class="form-group">
					<label for="">创建人</label>
					<input type="text" id="createman" name="createman" value="${info.createman }">
				</div>
				<button type="submit" class="btn btn-default btn-sm">保存</button>
				<a href="${pageContext.request.contextPath}/content/list.do?columnid=${info.columnid}"
					class="btn btn-primary btn-sm">返回</a>
			</form>
		</div>
	</div>
</div>

<%@ include file="/view/common/footer.jsp"%>

<script src="${pageContext.request.contextPath}/js/ckeditor/ckeditor.js"></script>