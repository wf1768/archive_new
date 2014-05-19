<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jplayer/jquery.jplayer.min.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/jplayer/skin/blue.monday/jplayer.blue.monday.css">
<title>多媒体播放</title>

<script type="text/javascript">
	$(function(){
		/* var mediatype = "${mediatype}";
		var ob = "";
		if (mediatype == "video") {
			$("#jp_container_1_audio").hide();
			ob = "jquery_jplayer_1";
		}
		else {
			$("#jp_container_1").hide();
			ob = "jquery_jplayer_1_audio";
			$("#jquery_jplayer_1_audio").jPlayer({
	 	        ready: function (event) {
		 	            $(this).jPlayer("setMedia", {
		 	            	title: "${title}",
							'${supplied}':"${pageContext.request.contextPath}/${maps[0].imgnewname}"
		 	            });
		 	        },
		 	        swfPath : "${pageContext.request.contextPath}/js/jplayer",
		 	       supplied : "${supplied}"
		 	}).jPlayer("play");
		} */
		$("#jquery_jplayer_1").jPlayer({
			ready : function() {
				$(this).jPlayer("setMedia", {
					title: "${title}",
					'${supplied}':"${pageContext.request.contextPath}/${maps[0].imgnewname}",
					//m4v:"${pageContext.request.contextPath}/file/3.mp4",
					//m4v:"${pageContext.request.contextPath}/file/3.mp4",
					//m4v: "${pageContext.request.contextPath}/file/Big_Buck_Bunny_Trailer.m4v",
					//ogv: "http://www.jplayer.org/video/ogv/Big_Buck_Bunny_Trailer.ogv",
					//webmv: "${pageContext.request.contextPath}/file/Big_Buck_Bunny_Trailer.webm",
					//webmv: "${pageContext.request.contextPath}/file/p180d0v4nq8il1iu1ckr1u7urim3.webm",
					poster: "${pageContext.request.contextPath}/images/black.jpg"
					
				}); //如果要一开始就全屏，加.jPlayer("fullScreen")但只是全窗口而不是全屏
			},
			swfPath : "${pageContext.request.contextPath}/js/jplayer",
			solution: '${solution}',
			supplied : "${supplied}",
			size : {
				width : "640px",
				height : "360px",
				cssClass : "jp-video-360p"
			},
			smoothPlayBar : true
		});
	})
</script>

</head>
<body style="text-align:center;">
	<div id="jp_container_1" class="jp-video jp-video-360p" style="width:640px;margin-left:auto;margin-right:auto;"><!--jp-video-360p是在CSS里预设好的宽640高360的样式-->
	   <div class="jp-type-single">
	           <div id="jquery_jplayer_1" class="jp-jplayer"></div><!--播放画面flash窗口-->
	           <div class="jp-gui">
	                 <div class="jp-video-play"><a href="javascript:;" class="jp-video-play-icon" tabindex="1">play</a></div>
	              <div class="jp-interface">
	                  <div class="jp-progress">
	                         <div class="jp-seek-bar">
	                            <div class="jp-play-bar"></div>
	                       </div>
	                  </div>
	                  <div class="jp-current-time"></div>
	                  <div class="jp-duration"></div>
	                  
	                  <div class="jp-controls-holder">
	                       <ul class="jp-controls">
	                            <li><a href="javascript:;" class="jp-play" tabindex="1">play</a></li>
	                            <li><a href="javascript:;" class="jp-pause" tabindex="1">pause</a></li>
	                            <li><a href="javascript:;" class="jp-stop" tabindex="1">stop</a></li>
	                            <li><a href="javascript:;" class="jp-mute" tabindex="1" title="mute">mute</a></li>
	                            <li><a href="javascript:;" class="jp-unmute" tabindex="1" title="unmute">unmute</a></li>
	                            <li><a href="javascript:;" class="jp-volume-max" tabindex="1" title="max volume">max volume</a></li>
	                       </ul>
	                       <div class="jp-volume-bar"><div class="jp-volume-bar-value"></div></div>
	                       <ul class="jp-toggles">
	                            <li><a href="javascript:;" class="jp-full-screen" tabindex="1" title="full screen">full screen</a></li>
	                            <li><a href="javascript:;" class="jp-restore-screen" tabindex="1" title="restore screen">restore screen</a></li>
	                            <li><a href="javascript:;" class="jp-repeat" tabindex="1" title="repeat">repeat</a></li>
	                            <li><a href="javascript:;" class="jp-repeat-off" tabindex="1" title="repeat off">repeat off</a></li>
	                       </ul>
	                  </div>
	              </div>
	          </div>
	          <div class="jp-title"><ul><li>My Video</li></ul></div>
	          <div class="jp-no-solution">
	              <span>更新提示</span>要播放这个多媒体文件，需要升级您的浏览器<a href="http://get.adobe.com/flashplayer/" target="_blank">Flash插件</a>到最新版本.
	          </div>
	   </div>
	</div>
	<div>多媒体文件播放支持视频格式：mp4、ogg、webm、flv</div>
	<div>多媒体文件播放支持音频格式：mp3、wav、flv</div>
</body>
</html>