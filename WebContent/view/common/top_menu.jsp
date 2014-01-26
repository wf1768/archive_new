<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<script type="text/javascript">
	<!--
	function quit() {
		if(confirm("真的要退出系统吗?")) {
            window.location.href = "${pageContext.request.contextPath}/logout.do";
        };
    }
	//-->
	</script>
	
	
	<!--头部开始-->  	 
  	<div id="header">
    	<div id="header1">
           <div class="logo">
               <img src="${pageContext.request.contextPath}/images/logo_03.png" /><span>${sysname }</span><div style="clear:both"></div>
           </div>
           <div class="nav">
            <ul>
                <li class="${focus_first=='index'?'onthis':''}"><a href="${pageContext.request.contextPath}/main.do">首页</a></li>
                <c:forEach items="${functions}" varStatus="i" var="item">
					<c:if test="${item.funparent == '0' }">
					<li class="${item.funenglishname==focus_first?'onthis':''}"  ><a href="${pageContext.request.contextPath}/${item.funpath }">${item.funchinesename }</a></li>
					</c:if>
		        </c:forEach>
		        <li><a href="#">帮助</a></li>
            </ul>
            <div style="clear: both">
            </div>
           </div>
           <div class="out">
           		<img src="${pageContext.request.contextPath}/images/close_06.png" />
            	<a href="javascript:;" onclick="quit()">退出</a>
           </div>
           <div class="user">欢迎 <a href="#" title="点击修改帐户信息">${sessionScope.CURRENT_USER_IN_SESSION.accountcode } </a>。</div>
           <div style="clear: both"></div>
    </div>
    
<!--头部结束-->