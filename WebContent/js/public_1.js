// JavaScript Document
 $(function () {
            $(window).resize(function () {
                setUI();
            }); 
			$(function(){
					   	setUI();
					   });
            setUI();
            function setUI() {

            	var winW = $(window).width();
				var winH = $(window).height();
                var leftW = $("#bodyer_left").outerWidth();
                var leftH = $("#bodyer_left").outerHeight();
				var bottH = $("#footer").outerHeight();
				var topH1 = $("header").outerHeight();
				
                var w = winW - leftW;
                var rw = w -2 + "px";
				var h = winH-topH1-bottH-80;
         
                $("#bodyer_right").height(leftH);
				$("#bodyer_left").height(h);
				$("#fanye").width($("#shuju").height());
				$("#bodyer_right").width(w);
                $("#cssz_table").width(rw);
				$("#sj").height($("#bodyer_right").height() - $("#fanye").height() - 66);
				

            }


        });
 
 
 
 
