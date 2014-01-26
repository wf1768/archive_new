<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>

<!-- 分页插件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/pagination/pagination.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pagination/jquery.pagination.js"></script>

<script type="text/javascript">
	$(function(){
		var total = ${pagebean.rowCount };
		var pagesize = ${pagebean.pageSize };
		var pageno = ${pagebean.pageNo };
		/* var url = "?treeid="+selectTreeid+"&page=__id__"; */
		$("#pagination").pagination(total, {
            'items_per_page'      : pagesize,
            'num_display_entries' : 7,
            'num_edge_entries'    : 2,
            'current_page'	      : pageno,
            'prev_text'           : "上一页",
            'next_text'           : "下一页",
            /* 'link_to'			  : url, */
            'callback'            : pageselectCallback
        });
		
	});
	
	function pageselectCallback(page_index, jq){
		var pageno = ${pagebean.pageNo };
		if (page_index != pageno) {
			window.location.href="${pageContext.request.contextPath}/column/list.do?&page="+page_index+"";
		}
	}; 
//-->
</script>
<div class="container" style="margin-top:80px;">
	<div class="row">
		<div class="col-md-9">
			<a href="${pageContext.request.contextPath}/dispatch.do?page=/view/column/add ">添加</a>
			<div>总行数：${pagebean.rowCount } 总页数：${pagebean.pageCount }
				当前页：${pagebean.pageNo } 下一页：${pagebean.nextNo }</div>

			<table class="table table-hover table-condensed table-bordered"><!-- table-striped table-hover table-condensed-->
				<tr>
					<td>序号</td>
					<td>栏目名称</td>
					<td>标签显示</td>
					<td>页面显示</td>
					<td>排序</td>
					<td>操作</td>
				</tr>
				<c:forEach items="${pagebean.getList()}" varStatus="i" var="item">

					<tr>
						<td>${pagebean.pageSize*(pagebean.pageNo-1) + i.index+1 }</td>
						<td>${item.column_name}</td>
						<td>
						<c:if test="${item.istab==1 }">
							<img alt="页签显示" src="${pageContext.request.contextPath }/images/icons/accept.png">
						</c:if>
						</td>
						<td>
						<c:if test="${item.islist==1}">
							<img alt="页面显示" src="${pageContext.request.contextPath }/images/icons/accept.png">
						</c:if>
						</td>
						<td>${item.sort}</td>
						<td>
							<a href="${pageContext.request.contextPath}/content/list.do?columnid=${item.id}" class="btn btn-primary btn-xs">信息列表</a>
							<a href="${pageContext.request.contextPath}/column/edit.do?id=${item.id}" class="btn btn-primary btn-xs">编辑</a>
							<a href="${pageContext.request.contextPath}/column/delete.do?id=${item.id}&page=${pagebean.pageNo }" onClick="if(confirm('确定要删除栏目吗?删除栏目将同时删除栏目下信息、图片、电子文件。')==false)return false;" class="btn btn-primary btn-xs">删除</a>
						</td>
					</tr>
				</c:forEach>
			</table>
			<div id="pagination" class="pagination"></div>
			总页数：${pagebean.pageCount } 总行数：${pagebean.rowCount } 当前页数：${pagebean.priorNo } 每页：${pagebean.pageSize } 行
		</div>
	</div>
</div>
<%@ include file="/view/common/footer.jsp"%>