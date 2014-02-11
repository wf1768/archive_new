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
			data: {
				keep: {
					parent: true
				},
				key:{
					name:"orgname"
				},
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "parentid"
				}
			},
			callback: {
				onClick: onClick
			}
	};
	var nodes = ${orgList};
	
	for (var i=0;i<nodes.length;i++) {
		
		if (nodes[i].parentid == '0') {
			nodes[i].open = true;
		}
		nodes[i].isParent = true;
		nodes[i].iconOpen = "${pageContext.request.contextPath}/images/folder-open.gif";
		nodes[i].iconClose = "${pageContext.request.contextPath}/images/folder.gif";
	}
	
	function selectNode(treeid) {
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var node = treeObj.getNodeByParam("id",treeid,null);
		treeObj.selectNode(node);
		if (node != null && node.id != 1) {
			treeObj.expandNode(node);
		}
		
	}
	
	function onClick(event, treeId, nodes) {
		window.location.href="${pageContext.request.contextPath}/org/list.do?orgid="+nodes.id;
	};
	
	$(function(){
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		var treeid = "${orgid}";
		selectTreeid = treeid;
		selectNode(treeid);
	});


	function add(){
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (nodes.length != 1) {
			alert("请选择左侧组织机构树，再创建下级单位。");
			return;
		}
		var url = "add.do?parentid="+nodes[0].id+"&time="+Date.parse(new Date());
		var whObj = { width: 340, height: 300 };
		var result = openShowModalDialog(url,window,whObj);
		//window.location.reload(true); // 刷新窗体
	}
	
	function del(id) {
		if (id == "") {
			alert("没有获得要删除的组，请重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要删除选择的组吗？删除该组，将同时删除该组下包含的全部组、帐户、档案类型及档案树节点。请谨慎操作。")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/org/delete.do",
		        type : 'post',
		        data: {orgid:id},
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
	
	function edit(id){
		var url = "edit.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 340, height: 300 };
		var result = openShowModalDialog(url,window,whObj);
		//window.location.reload(true); // 刷新窗体
	}
	
	function refresh() {
		window.location.reload(true);
	}
	
	function move(id) {
		var url = "move.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
	}
	
	function setowner(id) {
		var url = "setowner.do?orgid="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 740, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
	}
	
	function setrole(id) {
		var url = "setrole.do?orgid="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 740, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
	}


</script>


<!--内容部分开始-->

<div id="bodyer">
	<div id="bodyer_left">
		<dl>
			<dt>
				<a href="#" class="blue"><img src="${pageContext.request.contextPath}/images/i1_03.png" width="29" height="22" class="tubiao" />
					<span>
						组织机构管理
					</span>
				</a>
			</dt>
			<dd>
				<ul id="treeDemo" class="ztree"></ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="dqwz">当前位置：权限管理－组织机构管理</div>
		<div  class="caozuo">
            <div class="caozuo_left">
        	<ul>
            	<li></li>
            </ul>
            </div>
        	<div  class="caozuo_right">
        	<ul>
                <li><a href="javascript:;" onclick="add()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/add.png"  />
                    添加组织机构</a>
                </li>
                <li><a href="javascript:;" onclick="refresh()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/arrow_refresh.png"  />
                    刷新列表</a>
                </li>
            </ul>
            </div>
            <div style="clear:both"></div>
         
        </div>
		<div class="shuju" id="sj">
			<table id="cssz_table">
				<tr class="textCt ertr  hui title1">
					<td><p>#</p></td>
					<td><p>机构名称</p></td>
					<c:if test="${version=='group' }">
						<td><p>管理者</p></td>
					</c:if>
					<td><p>角色</p></td>
					<td><p>操作</p></td>
				</tr>
				<c:forEach items="${childList}" varStatus="i" var="item">
					<tr class="textCt ertr  ">
						<td>${i.index+1 }</td>
						<td>${item['orgname']}</td>
						<c:if test="${version=='group' }">
						<td>${item['ownerString']}</td>
						</c:if>
						<td>${item['roleString']}</td>
						<td>
							<c:if test="${version=='group' }">
								<a href="javascript:;" onclick="setowner('${item.id}')" class="juse">
									<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/user.png" />
									管理者
								</a>
							</c:if>
							<a href="javascript:;" onclick="edit('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_edit.png" />
								修改
							</a>
							<a href="javascript:;" onclick="del('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_delete.png" />
								删除
							</a>
							<a href="javascript:;" onclick="move('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_refresh.png" />
								移动
							</a> | 
							<a href="javascript:;" onclick="setrole('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/vcard_add.png" />
								角色
							</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="fanye" class="fanye1">
			<p>共 ${fn:length(childList) } 条记录</p>
		</div>
	</div>
	<div style="clear: both"></div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>