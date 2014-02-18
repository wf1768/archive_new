<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/view/common/header.jsp"%>
<%@ include file="/view/common/top_menu.jsp"%>
<%@ include file="/view/common/top_second_menu.jsp"%>

<!--智能检索-内容部分开始-->
  	
    <div id="bodyer2">
        <div style="margin-top: 20px;" align="center">
        	<form action="search.do" method="get">
        		<table>
        			<tr>
        				<td><select name="">
			        		<option value="">全部分类</option>
			        		</select>
			        	</td>
	        			<td><input type="text" name="searchText" value="" /></td>
	        			<td><input type="submit" value="检 索"/></td>
        			</tr>
        		</table>
        	</form>
        </div>
    </div>

<!--内容部分结束-->

<%@ include file="/view/common/footer.jsp"%>