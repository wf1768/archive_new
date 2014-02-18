<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>

<!--全文检索-内容部分开始-->
  	
    <div id="bodyer2">
    	<div class="xtgl">
        	<P>${sessionScope.CURRENT_USER_IN_SESSION.accountcode },欢迎您进入<b>【${pageName }】</b>界面</P>
        </div>
    </div>

<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>