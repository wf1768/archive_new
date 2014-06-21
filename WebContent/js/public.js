// JavaScript Document
$(function() {
	$(window).resize(function() {
		if ($(this).width() >1024) {
            setUI();
            callback();
        }
        else {
            setUI2();
        }
	});
	$(function() {
		setUI();
		//执行回调函数，callback函数执行一些辅助操作，例如checkbox事件的绑定，在list页面，如果没有辅助操作，也要加上个空的callback
		callback();
	});
	setUI();
	function setUI() {
		var t_html = $(".table-wrapper").html();
	    if(t_html) {
	    	var b_html = $(".body-wrapper").html();
	    	var par = $(".table-wrapper").parent();
	    	$(".table-wrapper").remove();
	    	par.html(b_html);
	    }
		
		var winW = $(window).width();
		var winH = $(window).height();
		var leftW = $("#bodyer_left").outerWidth();
		var leftH = $("#bodyer_left").outerHeight();
		var bottH = $("#footer").outerHeight();
		var topH1 = $("header").outerHeight();

		var w = winW - leftW;
		var rw = w -28 + "px";
		
		var h = winH - topH1 - bottH - 80;

		$("#bodyer_right").height(leftH);
		$("#bodyer_left").height(h);
		// $("#fanye").width($("#shuju").height());
		$("#bodyer_right").width(w);
		$(".data_table").width(rw);
		$(".xiangce").width(rw);
		$(".aa").width(rw);
		//$(".data_table").height(200);
		// $("#cssz_table").width(rw);
		// $("#tb2").height($("#bodyer_right").height() - 55);
		$(".scrollTable").height($("#bodyer_right").height() - 79);
		$(".xiangce").height($(".scrollTable").height() - 10);
		// $("#not").height($("#table_main").height()-60);
		// $("#ddiv").width(w*0.98-2);
		
		
		
	}
	
	function setUI2() {
		
		var t_html = $(".table-wrapper").html();
	    if(t_html) {
	    	var b_html = $(".body-wrapper").html();
	    	var par = $(".table-wrapper").parent();
	    	$(".table-wrapper").remove();
	    	par.html(b_html);
	    }
	    
	    
        $("#bodyer_right").height(755);
        $("#bodyer_left").height(755);
        $("#bodyer_right").width(810);
        $(".data_table").width(786);
        $(".xiangce").width(786);
        $(".aa").width(786);
        $(".scrollTable").height($("#bodyer_right").height() - 70);
        $(".xiangce").height($(".scrollTable").height() - 10);
//		var n = $(".scrollTable").height()-$(".aa").height();
//		$('.data_table').fixHeader({
//			height : n
//			
//		});
		//执行回调函数，callback函数执行一些辅助操作，例如checkbox事件的绑定，在list页面，如果没有辅助操作，也要加上个空的callback
        callback();

    }

});
