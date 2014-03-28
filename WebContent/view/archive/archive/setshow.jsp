<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/util.js"></script>
<base target="_self">

<script>
	function closepage() {
		window.returnValue="ok";
		window.close();
	}
	
	function save(id,ob) {
		var ob = $("#"+ob).val();
		
		if (id == "" || ob == "") {
			alert("没有获取足够数据，请退出后，重新尝试，或与管理员联系。");
			return;
		}
		
		if(isNaN(ob)){
			alert("只能为是数字,请输入数字。");
			$("#ob").focus();
			return;
		}
		
		var d = {};
		d.id = id;
		d.configvalue = ob;
		
	    $.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/config/updateSetShow.do",
	        type : 'post',
	        data:d,
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("更新完毕。");
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!");
	            }
	        }
	    });
	}
	
	function editField(id){
		var url = "${pageContext.request.contextPath}/archive/fieldedit.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 600, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
		//window.location.reload(true); // 刷新窗体
	}
	
	function sort(id,type) {
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/sort.do",
	        type : 'post',
	        data: {id:id,type:type},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	            window.location.reload(true);
	        }
	    });
	}
	
	//更新字段的一些属性。例如：检索字段、列表显示等的0和1修改。
	function updateOtherInfo(id,type,value) {
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/updateOtherInfo.do",
	        type : 'post',
	        data: {id:id,type:type,value:value},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	            window.location.reload(true);
	        }
	    });
	}
	//=====以前的，没用后，删除以下
	
	function fieldcode(id) {
		var url = "${pageContext.request.contextPath}/templetfield/fieldcode.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 640, height: 400 };
		var result = openShowModalDialog(url,window,whObj);
		window.location.reload(true);
	}
	
</script>
<title>档案显示设置</title>
</head>
<body>
	<table width="99%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<tbody>
			<tr>
                <td colspan="4" align="center">
                	列表显示设置
                </td>
            </tr>
            <c:forEach items="${configs}" varStatus="i" var="config">
            	<c:if test="${config.configkey=='PAGE'}">
            		<tr>
						<td>${config.configname }</td>
						<td>
							<input type="text" id="page" style="width:80px" name="" value="${config.configvalue }">
						</td>
						<td>${config.configmemo }</td>
						<td>
							<button type="button" onclick="save('${config.id}','page')">保存</button>
						</td>
						
					</tr>
            	</c:if>
            	<c:if test="${config.configkey=='SUBSTRING'}">
            		<tr>
						<td>${config.configname }</td>
						<td>
							<input type="text" id="substring" style="width:80px" name="" value="${config.configvalue }">
						</td>
						<td>${config.configmemo }</td>
						<td>
							<button type="button" onclick="save('${config.id}','substring')">保存</button>
						</td>
					</tr>
            	</c:if>
            </c:forEach>
            <tr>
				<td colspan="4" align="center">
					<button type="button" style="width: 100px;" onclick="closepage()">关闭</button>
				</td>
			</tr>
		</tbody>
	</table>
	
	<table width="99%" cellspacing="0" cellpadding="8" align="center" style="margin-top:20px">
		<thead >
			<tr>
	           <td colspan="12" align="center">
	           	字段属性显示设置
	           </td>
	        </tr>
			<tr>
				<td>序号</td>
				<td>中文名称</td>
				<td>名称</td>
				<td>类型</td>
				<td>长度</td>
				<td>默认值</td>
				<td>检索</td>
				<td>列表</td>
				<td>著录编辑</td>
				<td>数据排序</td>
				<td>代码项</td>
				<td>字段排序</td>
				<td>操作</td>
			</tr>
			</thead>
		<tbody>
           <c:forEach items="${templetfields}" varStatus="i" var="item">
           		<tr>
           			<td>${i.index+1 }</td>
           			<td>${item.chinesename}</td>
					<td>${item.englishname}</td>
					<td>
						<c:choose>
							<c:when test="${item.fieldtype=='VARCHAR' }">
								字符串
							</c:when>
							<c:when test="${item.fieldtype=='DATE' }">
								日期型
							</c:when>
							<c:when test="${item.fieldtype=='INT' }">
								整数型
							</c:when>
						</c:choose>
					</td>
					<td>${item.fieldsize}</td>
					<td>${item.defaultvalue}</td>
					<td>
						<c:choose>
							<c:when test="${item.issearch==1 }">
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','issearch',0)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png"></a>
							</c:when>
							<c:when test="${item.issearch==0 }">
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','issearch',1)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/delete.png"></a>
							</c:when>
							<c:otherwise>
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','issearch',1)">未知</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${item.isgridshow==1 }">
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','isgridshow',0)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png"></a>
							</c:when>
							<c:when test="${item.isgridshow==0 }">
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','isgridshow',1)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/delete.png"></a>
							</c:when>
							<c:otherwise>
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','isgridshow',1)">未知</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${item.isedit==1 }">
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','isedit',0)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png"></a>
							</c:when>
							<c:when test="${item.isedit==0 }">
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','isedit',1)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/delete.png"></a>
							</c:when>
							<c:otherwise>
								<a href="javascript:;" onclick="updateOtherInfo('${item.id}','isedit',1)">未知</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${item.orderby=='ASC' }">
								正序排序
							</c:when>
							<c:when test="${item.orderby=='DESC' }">
								倒序排序
							</c:when>
							<c:when test="${item.orderby=='GBK' }">
								中文排序
							</c:when>
						</c:choose>
					</td>
					<td>
						<c:if test="${item.iscode==1 }">
							<img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png">
						</c:if>
					</td>
					<td>
					<c:if test="${!i.first }">
						<a href="javascript:;" onclick="sort('${item.id}','up')"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/up.png" /></a>
					</c:if>
					<c:if test="${!i.last }">
						<a href="javascript:;" onclick="sort('${item.id}','down')"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/down.png" /></a>
					</c:if>
					</td>
					<td>
							<a href="javascript:;" onclick="editField('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/application_form_edit.png" />
								修改
							</a>
							<a href="javascript:;" onclick="fieldcode('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/application_view_list.png" />
								代码
							</a>
						</td>
           		</tr>
           </c:forEach>
		</tbody>
	</table>
</body>
</html>