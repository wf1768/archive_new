<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>


<div class="container" style="margin-top: 80px;">
	<div class="row">
		<div class="col-md-9">
			<form role="form" action="update.do" method="post">
				<input type="hidden" id="id" name="id" value="${column.id }">
				<div class="form-group">
					<label for="column_name">栏目名称</label>
					<input type="text" class="form-control" id="column_name" name="column_name" placeholder="请输入栏目名称" value="${column.column_name}">
				</div>
				<div class="form-group">
					<label for="">标签显示</label>
					<input type="checkbox" id="_istab" name="_istab" value="1" ${column.istab==1?"checked":"" }>
				</div>
				<div class="form-group">
					<label for="">页面显示</label>
					<input type="checkbox" id="_islist" name="_islist" value="1" ${column.islist==1?"checked":"" }>
				</div>
				<div class="form-group">
					<label for="sort">排序</label>
					<input type="text" id="sort" name="sort" value="${column.sort }">
				</div>
				<button type="submit" class="btn btn-default btn-sm">保存</button>
				<a href="${pageContext.request.contextPath}/column/list.do"
					class="btn btn-primary btn-sm">返回</a>
			</form>
		</div>
	</div>
</div>

<%@ include file="/view/common/footer.jsp"%>