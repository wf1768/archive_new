<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>

<div class="container" style="margin-top: 80px;">
	<div class="row">
		<div class="col-md-9">
			<form role="form" action="column/toinsert.do" method="post">
				<div class="form-group">
					<label for="columnname">栏目名称</label>
					<input type="text" class="form-control" id="column_name" name="column_name" placeholder="请输入栏目名称"">
				</div>
				<div class="form-group">
					<label for="">标签显示</label>
					<input type="checkbox" id="_istab" name="_istab" value="1">
				</div>
				<div class="form-group">
					<label for="">页面显示</label>
					<input type="checkbox" id="_islist" name="_islist" value="1">
				</div>
				<div class="form-group">
					<label for="sort">排序</label>
					<input type="text" id="sort" name="sort" value="1">
				</div>
				<button type="submit" class="btn btn-default btn-sm">保存</button>
				<a href="${pageContext.request.contextPath}/column/list.do" class="btn btn-primary btn-sm">返回</a>
			</form>
		</div>
	</div>
</div>

<%@ include file="/view/common/footer.jsp"%>