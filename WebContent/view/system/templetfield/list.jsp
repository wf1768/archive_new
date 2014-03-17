<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>

<script>
	var selectTreeid = 0;
	var setting = {
			callback: {
				onClick: onClick
			}
	};
	var nodes = ${result};
	
	function selectNode(treeid) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id",treeid,null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			treeObj.expandNode(node);
		}
	}
	
	function onClick(event, treeId, nodes) {
		if (nodes.isParent) {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			treeObj.expandNode(nodes);
		}
		else {
			window.location.href="${pageContext.request.contextPath}/templetfield/list.do?selectid="+nodes.id+"&m=${m}";
		}
	};
	
	
	function addField(){
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (typeof(nodes[0]) == "undefined") {
			alert("请选择档案类型，再创建字段。");
			return;
		}
		if (!nodes[0].isleaf) {
			alert("请选择档案类型，再创建字段。");
			return;
		}
		
		var url = "${pageContext.request.contextPath}/templetfield/add.do?tableid="+nodes[0].id+"&time="+Date.parse(new Date());
		var whObj = { width: 600, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
		//window.location.reload(true); // 刷新窗体
	}
	
	$(function(){
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		var treeid = "${selectid}";
		selectTreeid = treeid;
		selectNode(treeid);
		initgrid();
	});
	
	function delField(id) {
		if (id == "") {
			alert("没有获得要删除的字段，请重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要删除选择的字段吗？删除该字段，将同时删除该字段已录入的信息。请谨慎操作。")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/templetfield/delete.do",
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
	
	function editField(id){
		var url = "${pageContext.request.contextPath}/templetfield/edit.do?id="+id + "&time="+Date.parse(new Date());
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
	
	function refresh() {
		window.location.reload(true);
	}
	
	
	function addT() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (typeof(nodes[0]) == "undefined") {
			alert("请选择左侧父档案夹，再创建档案类型夹。");
			return;
		}
		if (nodes[0].templettype != 'T') {
			alert("请选择左侧父档案夹，再创建档案类型夹。");
			return;
		}
		var url = "${pageContext.request.contextPath}/templet/addT.do?parentid="+nodes[0].id+"&time="+Date.parse(new Date());
		var whObj = { width: 600, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
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
	
	function fieldcode(id) {
		var url = "${pageContext.request.contextPath}/templetfield/fieldcode.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 640, height: 400 };
		var result = openShowModalDialog(url,window,whObj);
		window.location.reload(true);
	}
	
	function fieldcopy(id) {
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/fieldcopy.do",
	        type : 'post',
	        data: {id:id},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	alert("字段复制成功，任意档案类型下，点击［粘贴字段］。");
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
	            }
	        }
	    });
	}
	
	function fieldpaste() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (typeof(nodes[0]) == "undefined") {
			alert("请选择档案类型，再粘贴字段。");
			return;
		}
		if (!nodes[0].isleaf) {
			alert("请选择档案类型，再粘贴字段。");
			return;
		}
		
		$.ajax({
	        async : true,
	        url : "${pageContext.request.contextPath}/templetfield/fieldpaste.do",
	        type : 'post',
	        data: {tableid:nodes[0].id},
	        dataType : 'text',
	        success : function(data) {
	            if (data == "success") {
	            	
	            } else {
	            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!");
	            }
	        }
	    });
	}

	
	function initgrid() {
		var rowNum = '${fn:length(templetfields) }';
		if (rowNum == 0) {
			return;
		}
		var jstb1 = document.getElementById("tb1").rows[0];
		var jstb2 = document.getElementById("tb2").rows[0];
		//var divw = 0;
		/* for ( var i = jstb2.cells.length-1; i >0 ; i--) {// “-2”是减去每列左右的边宽
			
			if (i==jstb2.cells.length-1) {
				jstb2.cells[i].style.width = "230px";
				jstb1.cells[i].style.width = "228px";
			}
			else {
				jstb2.cells[i].style.width = parseInt(jstb1.cells[i].clientWidth)  + "px";
			} */
			for ( var i = 0; i < jstb2.cells.length; i++) {// “-2”是减去每列左右的边宽
				jstb1.cells[i].style.width = parseInt(jstb2.cells[i].clientWidth)  + "px";
			
			
			/* if (jstb2.cells[i].clientWidth < 10) {
				jstb2.cells[i].style.width = "80px";
				jstb1.cells[i].style.width = "80px";
			}
			else {
				jstb1.cells[i].style.width = parseInt(jstb2.cells[i].clientWidth)  + "px";
			} */
			
			
			/* if (jstb1.cells[i].clientWidth > jstb2.cells[i].clientWidth) {//如果标题的列宽比数据的列宽还大则用标题的列宽
				jstb2.cells[i].style.width = parseInt(jstb1.cells[i].clientWidth)  + "px";
				//divw += parseInt(jstb1.cells[i].clientWidth) - 2;
			} else {
				jstb1.cells[i].style.width = parseInt(jstb2.cells[i].clientWidth)  + "px";
				//divw += parseInt(jstb2.cells[i].clientWidth) - 2;
			} */
		}
	}
</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img src="${pageContext.request.contextPath}/images/i1_03.png" width="29" height="22" class="tubiao" />
					<span>
					<c:choose>
						<c:when test="${m=='c' }">
							档案模版维护
						</c:when>
						<c:otherwise>
							库结构维护
						</c:otherwise>
					</c:choose>
						
					</span>
				</a>
			</dt>
			<dd>
				<ul id="treeDemo" class="ztree"></ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="top_dd">
			<div class="dqwz_l">
				<c:choose>
					<c:when test="${m=='c' }">当前位置：系统配置－档案模版维护</c:when>
					<c:otherwise>
						当前位置：系统配置－库结构维护
					</c:otherwise>
				</c:choose>
			</div>
			<div class="caozuoan">
				<a href="javascript:;" onclick="addField()">
					<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/application_form_add.png"  />
                    添加字段
                </a>
				<a href="javascript:;" onclick="fieldpaste()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/paste.png"  />
                    粘贴字段</a>
                <a href="javascript:;" onclick="refresh()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/arrow_refresh.png"  />
                    刷新列表</a>
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="title_div" id="tdiv">
			<table class="table-Kang " id="tb1" align="center" width="98%"
				 border=0 cellspacing="0" cellpadding="4" style="margin-top: 10px">
				<tr class=tableTopTitle-bg>
					<td><input type="checkbox" id="checkall"></td>
					<td>序号</td>
					<td>中文名称</td>
					<td>名称</td>
					<td>类型</td>
					<td>长度</td>
					<td>默认值</td>
					<!-- <td><p>必著</p></td>
					<td><p>唯一</p></td> -->
					<td>检索</td>
					<td>列表</td>
					<td>顺带</td>
					<td>数据排序</td>
					<td>代码项</td>
					<td>字段排序</td>
					<td style="border-right:0">操作</td>
					<td class="last_list" ></td>
				</tr>
			</table>
		</div>
		<div class="data_div scrollTable" id="ddiv">
			<table class="title_tb table-Kang1" id="tb2" align="center" width="100%"
				 border=0 cellspacing="0" cellpadding="4">
				<c:forEach items="${templetfields}" varStatus="i" var="item">
					<tr class="textCt ertr  ">
						<td><input type="checkbox" name="checkbox" value="${item.id }"></td>
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
						<%-- <td>
							<c:if test="${item.isrequire==1 }">
								<a href="javascript:;"><img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png"></a>
							</c:if>
						</td>
						<td>
							<c:if test="${item.isunique==1 }">
								<a href="javascript:;"><img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png"></a>
							</c:if>
						</td> --%>
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
								<c:when test="${item.iscopy==1 }">
									<a href="javascript:;" onclick="updateOtherInfo('${item.id}','iscopy',0)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png"></a>
								</c:when>
								<c:when test="${item.iscopy==0 }">
									<a href="javascript:;" onclick="updateOtherInfo('${item.id}','iscopy',1)"><img alt="是" src="${pageContext.request.contextPath}/images/icons/delete.png"></a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" onclick="updateOtherInfo('${item.id}','iscopy',1)">未知</a>
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
							</c:choose>
						</td>
						<td>
							<c:if test="${item.iscode==1 }">
								<a href="javascript:;" onclick="fieldcode('${item.id}')"><img alt="是" src="${pageContext.request.contextPath}/images/icons/accept.png"></a>
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
							<a href="javascript:;" onclick="delField('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/application_form_delete.png" />
								删除
							</a>
							<a href="javascript:;" onclick="fieldcode('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/application_view_list.png" />
								代码
							</a>
							<a href="javascript:;" onclick="fieldcopy('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/copy.png" />
								复制
							</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="aa">
			<table class=" table-Kang" align="center" width="98%"
				 border=0 cellspacing="0" cellpadding="4" >
				<tr class="table-botton" id="fanye" class="fanye1">
					<TD colspan="14"><p>共 ${fn:length(templetfields) } 条记录</p></TD>
				</tr>
			</table>
		</div>
		<div style="clear: both"></div>
	</div>
	<div style="clear: both"></div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>