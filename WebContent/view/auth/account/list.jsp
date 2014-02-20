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
		window.location.href="${pageContext.request.contextPath}/account/list.do?orgid="+nodes.id;
	};
	
	$(function(){
		$.fn.zTree.init($("#treeDemo"), setting, nodes);
		
		var treeid = "${orgid}";
		selectTreeid = treeid;
		selectNode(treeid);
		
		$('#checkall').click(function(){
		    $('input[name="checkbox"]').attr("checked",this.checked);
		});
		
		$('input[type="checkbox"]').removeAttr("checked");
	});


	function add(){
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getSelectedNodes();
		if (nodes.length != 1) {
			alert("请选择左侧组织机构树，再创建帐户。");
			return;
		}
		var url = "add.do?orgid="+nodes[0].id+"&time="+Date.parse(new Date());
		var whObj = { width: 340, height: 300 };
		var result = openShowModalDialog(url,window,whObj);
		//window.location.reload(true); // 刷新窗体
	}
	
	function del(id) {
		if (id == "") {
			alert("没有获得要删除的帐户，请重新尝试，或与管理员联系。");
			return;
		}
		
		if (confirm("确定要删除选择的帐户吗？删除该帐户，将同时删除该帐户的一切附属信息。请谨慎操作。")) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/account/delete.do",
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
	
	function edit(id){
		var url = "edit.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 340, height: 300 };
		var result = openShowModalDialog(url,window,whObj);
		//window.location.reload(true); // 刷新窗体
	}
	
	function refresh() {
		window.location.reload(true);
	}
	
	function move() {
		var str = "";
		
		$("input[name='checkbox']:checked").each(function () {
			str+=$(this).val()+ ",";
		});
		
		if (str == "") {
			alert("请先选择要移动的帐户。");
			return;
		}

		str = str.substring(0,str.length-1);
		var url = "move.do?id="+str + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
	}
	
	function updatepass(id) {
		var url = "updatepass.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 440, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
	}
	
	function updatestate(id,state) {
		if (id == "") {
			alert("没有获得要更改状态的帐户，请重新尝试，或与管理员联系。");
			return;
		}
		var str = "确定要将状态更改为［禁用］吗？禁用后该帐户将不能登录本系统。";
		if (state == 1) {
			str = "确定要将状态更改为［启用］吗？";
		}
		
		if (confirm(str)) {
		    $.ajax({
		        async : true,
		        url : "${pageContext.request.contextPath}/account/updatestate.do",
		        type : 'post',
		        data: {id:id,state:state},
		        dataType : 'text',
		        success : function(data) {
		            if (data == "success") {
		            	alert("更改完毕。");
		            	
		            } else {
		            	alert("可能因为您长时间没有操作，或读取数据时出错，请关闭浏览器，重新登录尝试或与管理员联系!！");
		            }
		            window.location.reload(true);
		        }
		    });
		}
	}
	
	function setrole(id) {
		var url = "setrole.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 740, height: 500 };
		var result = openShowModalDialog(url,window,whObj);
	}
	
	function setauth(id) {
		var url = "setauth.do?id="+id + "&time="+Date.parse(new Date());
		var whObj = { width: 800, height: 500 };
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
						帐户管理
					</span>
				</a>
			</dt>
			<dd>
				<ul id="treeDemo" class="ztree"></ul>
			</dd>
		</dl>
	</div>
	<div id="bodyer_right">
		<div class="dqwz">当前位置：权限管理－帐户管理</div>
		<div  class="caozuo">
            <div class="caozuo_left">
        	<ul>
            	<li></li>
            </ul>
            </div>
        	<div  class="caozuo_right">
        	<ul>
                <li><a href="javascript:;" onclick="add()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/user_add.png"  />
                    添加帐户</a>
                </li>
                <li><a href="javascript:;" onclick="move()"><img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/user_go.png"  />
                    移动帐户</a>
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
					<td><input type="checkbox" id="checkall"></td>
					<td><p>#</p></td>
					<td><p>帐户名称</p></td>
					<td><p>帐户状态</p></td>
					<td><p>帐户角色</p></td>
					<td><p>备注</p></td>
					<td><p>操作</p></td>
				</tr>
				<c:forEach items="${accounts}" varStatus="i" var="item">
					<tr class="textCt ertr  ">
						<td><input type="checkbox" name="checkbox" value="${item.id }"></td>
						<td>${i.index+1 }</td>
						<td>${item.accountcode}</td>
						<td>
						<c:choose>
							<c:when test="${item.accountstate==1 }">
								<a href="javascript:;" onclick="updatestate('${item.id}',0)" >启用</a>
							</c:when>
							<c:when test="${item.accountstate==0 }">
								<a href="javascript:;" onclick="updatestate('${item.id}',1)" ><font color="red">禁用</font></a>
							</c:when>
							<c:otherwise>
								<a href="javascript:;" onclick="updatestate('${item.id}',1)" ><font color="red">未知</font></a>
							</c:otherwise>
						</c:choose>
						</td>
						<td>${item.rolename}</td>
						<td>${item.accountmemo}</td>
						<td>
							<a href="javascript:;" onclick="edit('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_edit.png" />
								修改
							</a>
							<a href="javascript:;" onclick="updatepass('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_edit.png" />
								修改密码
							</a>
							<c:if test="${item.accountcode != 'admin' }">
								<a href="javascript:;" onclick="del('${item.id}')" class="juse">
									<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/page_delete.png" />
									删除
								</a>
							</c:if>
							| <a href="javascript:;" onclick="setrole('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/vcard_add.png" />
								角色
							</a>
							<a href="javascript:;" onclick="setauth('${item.id}')" class="juse">
								<img style="margin-bottom:-3px" src="${pageContext.request.contextPath}/images/icons/key_add.png" />
								权限
							</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="fanye" class="fanye1">
			<p>共 ${fn:length(accounts) } 条记录</p>
		</div>
	</div>
	<div style="clear: both"></div>
</div>
<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>