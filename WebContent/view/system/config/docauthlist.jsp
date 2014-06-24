<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">

<script type="text/javascript">

function add(){  
	var url = "docauthadd.do?time="+Date.parse(new Date());
	var whObj = { width: 600, height: 300 };
	var result = openShowModalDialog(url,null,whObj);
	window.location.reload(true); // 刷新窗体
	
}

function edit(id){  
	var url = "docauthedit.do?id="+id + "&time="+Date.parse(new Date());
	var whObj = { width: 600, height: 300 };
	var result = openShowModalDialog(url,null,whObj);
	window.location.reload(true); // 刷新窗体
	
}

function refresh() {
	window.location.reload(true);
}

function del(id) {
	if (id == "") {
		alert("没有获得要删除的电子全文浏览全县代码，请重新尝试，或与管理员联系。");
		return;
	}
	
	if (confirm("确定要删除选择的电子全文浏览权限代码吗？删除该代码，将移除组或帐户已设置的全文浏览权限代码。请谨慎操作。")) {
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/config/docauthdelete.do",
	        type : 'post',
	        data: {id:id},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("删除完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	            window.location.reload(true);
	        }
	    });
	}
}


//-->
</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img
					src="${pageContext.request.contextPath}/images/i1_03.png"
					width="29" height="22" class="tubiao" /><span>系统配置</span></a>
			</dt>
			<dd>
				<ul>
					<li><a href="${pageContext.request.contextPath}/config/list.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_07.png"
							width="18" height="15" class="tubiao1" /><span>参数设置</span></a></li>
					<li><a href="${pageContext.request.contextPath}/docserver/list.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_10.png"
							width="18" height="13" class="tubiao1" /><span>文件服务器</span></a></li>
					<li><a href="${pageContext.request.contextPath}/config/docauthlist.do" class="txt2 on"><img
							src="${pageContext.request.contextPath}/images/i_10.png"
							width="18" height="13" class="tubiao1" /><span>电子文件权限</span></a></li>
					<li><a href="${pageContext.request.contextPath}/index/index.do" class="txt2"><img
							src="${pageContext.request.contextPath}/images/i_14.png"
							width="18" height="13" class="tubiao1" /><span>索引维护</span></a></li>
				</ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:999; ">
			<div class="dqwz_l">当前位置：系统维护-系统配置-电子文件权限代码</div>
			<div  class="caozuoan">
	        	<a href="javascript:;" onclick="add()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/add.png"  />
	                    添加电子文件权限代码</a>
	            <a href="javascript:;" onclick="refresh()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/arrow_refresh.png"  />
	                    刷新列表</a>
	        </div>
	            <div style="clear:both"></div>
       	</div>
		<div class="scrollTable" align="left" style="padding-left:5px; ">
			<table id="data_table" class="data_table table-Kang" aline="left" width="98%"
				border=0 cellspacing="1" cellpadding="4">
				<thead>
					<tr class="tableTopTitle-bg">
						<td width="40px">#</td>
						<td>代码名称</td>
						<td>代码值</td>
						<td>操作</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${docauth}" varStatus="i" var="item">
					<tr class="table-SbgList">
						<td>${i.index+1 }</td>
						<td>${item.columnname}</td>
						<td><font color="blue">${item.columndata}</font></td>
						<td>
							<c:if test="${item.id != '1' }">
								<a href="#" onclick="edit('${item.id}')" class="juse">
									<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_edit.png" />
									修改
								</a>
								<a href="#" onclick="del('${item.id}')" class="juse">
									<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_delete.png" />
									删除
								</a>
							</c:if>
							
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="aa" style="margin-left:5px" >
			<table class=" " aline="left" width="100%" 
				 border=0 cellspacing="0" cellpadding="0" >
				<tr class="table-botton" id="fanye" >
					<td colspan="14"><p>当前第 1 页，共 1 页，共 ${fn:length(docauth) } 行</p></td>
					<td colspan="14" class="fenye" ></td>
				</tr>
			</table>
		</div>
	<div style="clear: both"></div>
</div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>