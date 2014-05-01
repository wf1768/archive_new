<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table_main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.shiftcheckbox.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>

<script>
	var selectTreeid = 0;
	var setting = {
		data : {
			
		},
		callback : {
			onClick : onClick
		}
	};
	var nodes = ${result};

	function selectNode(treeid) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id", treeid, null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			treeObj.expandNode(node);
		}
	}

	function onClick(event, treeId, nodes) {
		if (nodes.templettype == 'T') {
			window.location.href = "${pageContext.request.contextPath}/templet/list.do?selectid="
					+ nodes.id;
		}
	};

	$(function() {
		$.fn.zTree.init($("#treeDemo"), setting, nodes);

		var treeid = "${selectid}";
		selectTreeid = treeid;
		selectNode(treeid);
		
	});
	
	function callback() {
		var n = $(".scrollTable").height()-$(".aa").height();
		$('.data_table').fixHeader({
			height : n
		});
		
		/*$('#checkall').click(function(){
		    $('input[name="checkbox"]').attr("checked",this.checked);
		});
		//页面加载时，去掉所有checkbox的选择
		$('input[type="checkbox"]').removeAttr("checked");
		
		$('.shiftCheckbox').shiftcheckbox();*/
	}
	
	function refresh() {
		window.location.reload(true);
	}

	function addT() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (typeof (nodes[0]) == "undefined") {
			alert("请选择左侧父档案夹，再创建档案类型夹。");
			return;
		}
		if (nodes[0].templettype != 'T') {
			alert("请选择左侧父档案夹，再创建档案类型夹。");
			return;
		}
		var url = "${pageContext.request.contextPath}/templet/addT.do?parentid="
				+ nodes[0].id + "&time=" + Date.parse(new Date());
		var whObj = {
			width : 500,
			height : 200
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true);
	}
	
	function add() {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (typeof (nodes[0]) == "undefined") {
			alert("请选择左侧父档案夹，再创建档案类型。");
			return;
		}
		if (nodes[0].templettype != 'T') {
			alert("请选择左侧父档案夹，再创建档案类型。");
			return;
		}
		var url = "${pageContext.request.contextPath}/templet/add.do?parentid=" + nodes[0].id + "&m=c&time=" + Date.parse(new Date());
		var whObj = {
			width : 600,
			height : 300
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true); // 刷新窗体
	}
	
	function edit(id) {
		var url = "${pageContext.request.contextPath}/templet/edit.do?id="
				+ id + "&time=" + Date.parse(new Date());
		var whObj = {
			width : 500,
			height : 200
		};
		var result = openShowModalDialog(url, window, whObj);
		window.location.reload(true); // 刷新窗体
	}
	
	function move(id) {
		var str = "";
		if (id=="") {
			$("input[name='checkbox']:checked").each(function () {
				str+=$(this).val()+ ",";
			});
			
			if (str == "") {
				alert("请先选择要移动的档案类型。");
				return;
			}

			str = str.substring(0,str.length-1);
		}
		else {
			str = id;
		}
		var url = "move.do?id="+str + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
		window.location.reload(true);
	}
	
	function sort(id) {
		if (id=="") {
			alert("请先选择要排序的档案类型。");
			return;
		}
		var url = "sort.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 300 };
		var result = openShowModalDialog(url,window,whObj);
		window.location.reload(true);
	}
	
	function del(id,type) {
		if (id == "") {
			alert("没有获得要删除的数据，请重新尝试，或与管理员联系。");
			return;
		}
		var str = "";
		if (type == "T") {
			str = "确定要删除选择的档案类型夹吗？将同时删除该档案类型夹下包含的所有档案类型，及档案数据、电子全文。请谨慎操作。"
		}
		else {
			str = "确定要删除选择的档案类型吗？将同时删除该档案类型下包含的所有档案数据、电子全文。请谨慎操作。"
		}
		
		if (confirm(str)) {
			$.blockUI({
				message:"正在进行删除，请稍候...",
				css: {
                    padding: '15px',
                    width:"300px"
                } 
            }); 
            //setTimeout($.unblockUI, 5000);
			//return;
			setTimeout(function () {
				$.ajax({
					async : false,
					url : "${pageContext.request.contextPath}/templet/delete.do",
					type : 'post',
					data : {
						id : id
					},
					dataType : 'text',
					success : function(data) {
						$.unblockUI();
						if (data == "success") {
							alert("删除完毕。");
						} else if(data == "error") {
							alert("当前删除的档案夹下，还有档案夹或档案类型，请先删除下级。");
						} else {
							alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
						}
						
						window.location.reload(true);
					},
					error: function (err) {  
						$.unblockUI();
	                    alert("error:" + err);  
	                    return false;  
	                }
				});
			},100)
		};
	}
</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img
					src="${pageContext.request.contextPath}/images/i1_03.png"
					width="29" height="22" class="tubiao" /> <span>档案库维护</span> </a>
			</dt>
			<dd>
				<ul id="treeDemo" class="ztree"></ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right" >
		<div class="top_dd" style="margin-bottom: 10px;position:relative;z-index:5555;">
			<div class="dqwz_l">当前位置：系统配置－档案库维护 </div>
			<div class="caozuoan">
				<input type="button" value="添加档案夹" class="btn" onClick="addT()" />
				<input type="button" value="添加档案类型" class="btn" onClick="add()" />
				<input type="button" value="移动" class="btn" onClick="move('')" />
				<input type="button" value="刷新" class="btn" onClick="refresh()" />
			</div>
			<div style="clear: both"></div>
		</div>
		<div class="scrollTable" align="left" style="padding-left:5px; " >
			<table id="data_table"   class="data_table table-Kang" aline="left" width="98%" 
				 border=0 cellspacing="1" cellpadding="4" >
				 <thead >
					<tr class="tableTopTitle-bg">
						<td><input type="checkbox" id="checkall"></td>
						<td>序号</td>
						<td>档案库名称</td>
						<td>类型</td>
						<td>排序</td>
						<td style="border-right:0">操作</td>
						<td class="last_list" ></td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${templets}" varStatus="i" var="item">
						<tr class="table-SbgList">
							<td><input type="checkbox" name="checkbox"
								value="${item.id }" class="shiftCheckbox"></td>
							<td>${i.index+1 }</td>
							<td>${item.templetname}</td>
							<td><c:choose>
									<c:when test="${item.templettype=='A' }">
										<font color="blue">标准档案</font>
									</c:when>
									<c:when test="${item.templettype=='F' }">
										<font color="blue">文件级档案</font>
									</c:when>
									<c:when test="${item.templettype=='P' }">
										<font color="blue">多媒体档案</font>
									</c:when>
									<c:when test="${item.templettype=='T' }">
										档案夹
									</c:when>
								</c:choose></td>
							<td><a href="javascript:;" onclick="sort('${item.id}')">${item.sort}</a></td>
							<td>
								<a href="javascript:;" onclick="sort('${item.id}')">
									<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/up.png" />
									排序
								</a>
								<a href="javascript:;" onclick="edit('${item.id}')">
									<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/application_form_edit.png" />
									修改
								</a>
								<a href="javascript:;" onclick="del('${item.id}','${item.templettype }')">
									<img style="margin-bottom: -3px"
									src="${pageContext.request.contextPath}/images/icons/application_form_delete.png" />
									删除
								</a>
								<a href="javascript:;" onclick="move('${item.id}')">
									<img style="margin-bottom: -3px" src="${pageContext.request.contextPath}/images/icons/application_view_list.png" />
									移动
							    </a>
							 </td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<div class="aa" style="margin-left:5px" >
			<table class=" " aline="left" width="100%" 
				 border=0 cellspacing="0" cellpadding="0" >
				<tr class="table-botton" id="fanye" >
					<td colspan="14"><p>当前第 1 页，共 1 页，共 ${fn:length(templets) } 行</p></td>
					<td colspan="14" class="fenye" ></td>
				</tr>
			</table>
		</div>
	</div>
	<div style="clear: both"></div>
</div>
	<!--内容部分结束-->

	<%@ include file="/view/common/footer.jsp"%>
