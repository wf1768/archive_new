// JavaScript 公共工具类  
//为弹出页刷新问题。解决弹出页保存后，刷新本页时，ie不支持。


function reload() {
	var reload = document.getElementById("reload");
	reload.click();
}
	
$(function(){
	var url = window.location.href;
	$("#reload").attr("href",url);
})

