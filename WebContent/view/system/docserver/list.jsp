<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">

<script type="text/javascript">
<!--
function add(){  
	var url = "add.do?time="+Date.parse(new Date());
	var whObj = { width: 340, height: 400 };
	var result = openShowModalDialog(url,window,whObj);
	window.location.reload(true); // 刷新窗体
}

function del(id) {
	if (id == "") {
		alert("没有获得要删除的服务器id，请重新尝试，或与管理员联系。");
		return;
	}
	
	if (confirm("确定要删除选择的电子全文服务器吗？")) {
		var par = "id=" + id;
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/docserver/delete.do",
	        type : 'post',
	        data:par,
	        dataType : 'text',  //靠，这里的参数有很多种。如果是script。那么后台传来的string类型就没有反应
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

function status(id) {
	if (id == "") {
		alert("没有获得要启用的服务器id，请重新尝试，或与管理员联系。");
		return;
	}
	
	if (confirm("确定要启用选择的电子全文服务器吗？启用后，其他服务器将处于非启用状态。")) {
		var par = "id=" + id;
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/docserver/status.do",
	        type : 'post',
	        data:par,
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新状态成功。");
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	            window.location.reload(true);
	        }
	    });
	}
}

function edit(id){  
	var url = "edit.do?id="+id + "&time="+Date.parse(new Date());
	var whObj = { width: 340, height: 400 };
	var result = openShowModalDialog(url,window,whObj);
	//window.location.reload(true); // 刷新窗体
}

function refresh() {
	window.location.reload(true);
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
					<li><a href="${pageContext.request.contextPath}/docserver/list.do" class="txt2 on"><img
							src="${pageContext.request.contextPath}/images/i_10.png"
							width="18" height="13" class="tubiao1" /><span>文件服务器</span></a></li>
					<li><a href="${pageContext.request.contextPath}/config/docauthlist.do" class="txt2"><img
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
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:5555;">
			<div class="dqwz_l">当前位置：系统维护-系统配置-文件服务器</div>
			<div  class="caozuoan">
	        	<a href="javascript:;" onclick="add()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/add.png"  />
	                    添加文件服务器</a>
	            <a href="javascript:;" onclick="refresh()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/arrow_refresh.png"  />
	                    刷新列表</a>
	        </div>
	        <div style="clear:both"></div>
	    </div>
		<div class="scrollTable" align="left" style="padding-left:5px; " >
			<table id="data_table"   class="data_table table-Kang" aline="left" width="98%" 
				 border=0 cellspacing="1" cellpadding="4" >
				<thead >
					<tr class="tableTopTitle-bg">
						<td width="40px">#</td>
						<td>服务器名称</td>
						<td>服务器类型</td>
						<td>服务器路径</td>
						<!-- <td>服务器IP</td>
						<td>FTP帐户</td>
						<td>FTP密码</td>
						<td>FTP端口</td> -->
						<td>状态</td>
						<td>描述</td>
						<td>操作</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${docserverList}" varStatus="i" var="item">
					<tr class="table-SbgList">
						<td>${i.index+1 }</td>
						<td>${item.servername}</td>
						<td>${item.servertype}</td>
						<td>${item.serverpath}</td>
						<%-- <td>${item.serverip}</td>
						<td>${item.ftpuser}</td>
						<td>${item.ftppassword}</td>
						<td>${item.serverport}</td> --%>
						<td>
						<c:if test="${item.serverstate==1 }">
							<font color="red">已启用</font>
						</c:if>
						</td>
						<td>${item.servermemo}</td>
						<td>
							<a href="javascript:;" onclick="status('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_gear.png" />
								启用
							</a>
							<a href="#" onclick="edit('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_edit.png" />
								修改
							</a>
							<a href="javascript:;" onclick="del('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_delete.png" />
								删除
							</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="aa" style="margin-left:5px" >
			<table class=" " aline="left" width="100%" 
				 border=0 cellspacing="0" cellpadding="0" >
				<tr class="table-botton" id="fanye" >
					<td colspan="14"><p>当前第 1 页，共 1 页，共 ${fn:length(docserverList) } 行</p></td>
					<td colspan="14" class="fenye" ></td>
				</tr>
			</table>
		</div>
	</div>
	<div style="clear: both"></div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>