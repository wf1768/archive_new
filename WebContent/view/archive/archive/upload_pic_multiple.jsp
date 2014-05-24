<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" type="text/css" />
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/plupload-2.1.1/js/jquery.plupload.queue/css/jquery.plupload.queue.css" media="screen" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/plupload-2.1.1/js/plupload.full.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/plupload-2.1.1/js/jquery.plupload.queue/jquery.plupload.queue.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/plupload-2.1.1/js/i18n/zh_CN.js"></script>
<base target="_self">

<script>

	$(function() {
	    // Setup html5 version
	    $("#uploader").pluploadQueue({
	        // General settings
	        runtimes : 'html5,flash,silverlight,html4',
	        url : "${pageContext.request.contextPath}/archive/upload_pic_multiple.do?treeid=${treeid}&tabletype=${tabletype}&parentid=${parentid}&status=${status}&slttype=${slttype}",
	        chunk_size : '2mb',
	        //rename : true,
	        //dragdrop: true,
	         
	        filters : {
	            // Maximum file size
	            max_file_size : '200mb'
	            // Specify what files to browse for
	           /* mime_types: [
	                {title : "Image files", extensions : "jpg,gif,png,bmp,tif,jpge"},
	                {title : "Video files", extensions : "avi,"}
	            ] */
	        },
	 
	        // Resize images on clientside if we can
	        /* resize: {
	            width : 200,
	            height : 200,
	            quality : 90,
	            crop: true // crop to exact dimensions
	        }, */
	 
	 
	        // Flash settings
	        flash_swf_url : '${pageContext.request.contextPath}/js/plupload-2.1.1/js/Moxie.swf',
	     
	        // Silverlight settings
	        silverlight_xap_url : '${pageContext.request.contextPath}/js/plupload-2.1.1/js/Moxie.xap'
	    });
	});
	
</script>
<title>上传多媒体文件</title>
</head>
<body>
	<div id="uploader">
    <p>Your browser doesn't have Flash, Silverlight or HTML5 support.</p>
</div>
</body>
</html>