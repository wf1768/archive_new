// JavaScript Document
$(function() {
	$(window).resize(function() {
		setUI();
	});
	$(function() {
		setUI();
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
		$(".aa").width(rw);
		//$(".data_table").height(200);
		// $("#cssz_table").width(rw);
		// $("#tb2").height($("#bodyer_right").height() - 55);
		$(".scrollTable").height($("#bodyer_right").height() - 70);
		// $("#not").height($("#table_main").height()-60);
		// $("#ddiv").width(w*0.98-2);
		
		

		var n = $(".scrollTable").height()-$(".aa").height();
		$('.data_table').fixHeader({
			height : n
			
		});
		
	}

});
