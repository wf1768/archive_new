/*
 * 打印公共类。
 * wangf
 */

/**
 * printcode:	打印类型，要打印什么。例如案卷目录代码 AJML
 */
function create_print_html(printcode,field,otherPar) {
	//案卷目录
	if (printcode == 'AJML') {
		return ajml_html(field,otherPar.data,otherPar.path);
	}
	else if (printcode == 'JNML') {
		//卷内目录
		return jnml_html(field,otherPar.data,otherPar.ajh,otherPar.bgqx,otherPar.path);
	}
	else if (printcode == 'JNBKB') {
		//卷内备考表
		return jnbkb_html(otherPar.data,otherPar.path);
	}
	else if (printcode == 'YJDJB') {
		//移交登记表
		return yjdjb_html(field,otherPar);
	}
}

function ajml_html(field,data,path) {
	var data_json = JSON.parse(data);
	var html = "";
	for (var i=0;i<Math.ceil(data_json.length/16);i++) {
		if (i == (Math.ceil(data_json.length/16) -1)) {
			html += "<div class=\"my_show\">";
		}
		else {
			html += "<div class=\"my_show\" style=\"page-break-after: always;\">";
		}
		html += "<table  cellspacing=\"0\" style=\"border-collapse: collapse; border-spacing: 0;background-color: transparent;max-width: 100%\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
        html += "<thead>";
        html += "<tr>";
        html += "<th colspan=\"5\">";
        html += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">";
        html += "<tr align=\"center\">";
        html += "<td style=\"font-size: 26px;padding-bottom: 45px;padding-top: 0px\">&nbsp;&nbsp;案 卷 目 录</td>";
        html += "</tr>";
        html += "</table>";
		html += "</th>";
		html += "</tr>";
		html += "<tr style=\"border:1px #000 solid;text-align: center;height: 50px\">";
		html += "<th style=\"border:1px #000 solid;\">序号</th>";
		html += "<th style=\"border:1px #000 solid;\">案卷号</th>";
		html += "<th style=\"border:1px #000 solid;\">题名</th>";
		html += "<th style=\"border:1px #000 solid;\">页数</th>";
		html += "<th style=\"border:1px #000 solid;\">备注</th>";
		html += "</tr>";
		html += "</thead>";
		html += "<tbody >";
		
		for (var j=i*16;j<i*16+16;j++) {
			if (j<data_json.length) {
				html += "<tr class=\"content_tr\" style=\"font-size:12px;height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\">"+ (j+1) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.ajh == 'NOTHING' ? '': data_json[j][field.ajh]) +"</td>";
				html += "<td align=\"left\" style=\"border:1px #000 solid;\">"+ (field.tm == 'NOTHING' ? '' : data_json[j][field.tm]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.ys == 'NOTHING' ? '' : data_json[j][field.ys]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.bz == 'NOTHING' ? '' : data_json[j][field.bz]) +"</td>";
				html += "</tr>";
			}
			else {
				html += "<tr class=\"content_tr\" style=\"height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "</tr>";
			}
		}
		html += "</tbody>";
		html += "</table>";
        html += "</div>";
	}
	
	return html;
}

function yjdjb_html(field,otherPar) {
	var data_json = JSON.parse(otherPar.data);
	var html = "";
	for (var i=0;i<Math.ceil(data_json.length/13);i++) {
		if (i == (Math.ceil(data_json.length/13) -1)) {
			html += "<div class=\"my_show\">";
		}
		else {
			html += "<div class=\"my_show\" style=\"page-break-after: always;\">";
		}
		html += "<table  cellspacing=\"0\" style=\"border-collapse: collapse; border-spacing: 0;background-color: transparent;max-width: 100%\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
        html += "<thead>";
        html += "<tr>";
        html += "<th colspan=\"5\">";
        html += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">";
        html += "<tr align=\"center\">";
        html += "<td style=\"font-size: 26px;padding-bottom: 25px;padding-top: 0px;padding-left:50px\">档案移交登记表</td>";
        html += "</tr>";
        html += "</table>";
        html += "<table border=\"0\" style=\"padding-bottom: 10px;\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
        html += "<tr>";
        html += "<td style=\"font-size:14px;text-align: left\">年度：<span>"+field.nd+"</span></td>";
        html += "<td style=\"font-size:14px;text-align: left\">档案移交部门:<span>"+field.yjbm+"</span></td>";
        html += "</tr>";
        html += "</table>";
		html += "</th>";
		html += "</tr>";
		html += "<tr style=\"border:1px #000 solid;text-align: center;height: 50px\">";
		html += "<th style=\"border:1px #000 solid;\">序号</th>";
		html += "<th style=\"border:1px #000 solid;\">案卷号</th>";
		html += "<th style=\"border:1px #000 solid;\">题名</th>";
		html += "<th style=\"border:1px #000 solid;\">保管期限</th>";
		html += "<th style=\"border:1px #000 solid;\">件数</th>";
		html += "<th style=\"border:1px #000 solid;\">录入系统</th>";
		html += "</tr>";
		html += "</thead>";
		html += "<tbody >";
		
		for (var j=i*13;j<i*13+13;j++) {
			if (j<data_json.length) {
				html += "<tr class=\"content_tr\" style=\"font-size:12px;height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\">"+ (j+1) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.ajh == 'NOTHING' ? '': data_json[j][field.ajh]) +"</td>";
				html += "<td align=\"left\" style=\"border:1px #000 solid;\">"+ (field.tm == 'NOTHING' ? '' : data_json[j][field.tm]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.bgqx == 'NOTHING' ? '' : data_json[j][field.bgqx]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.js == 'NOTHING' ? '' : data_json[j][field.js]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">是</td>";
				html += "</tr>";
			}
			else {
				html += "<tr class=\"content_tr\" style=\"height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "</tr>";
			}
		}
		html += "</tbody>";
		html += "</table>";
		html += "<table border=\"0\" style=\"margin-top:10px;padding-bottom: 10px;\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
        html += "<tr style=\"height:10px;\">";
        html += "<td style=\"font-size:14px;text-align: left;width: 60px; \">交接日期:</td>";
        html += "<td style=\"font-size:14px;text-align: left;width: 60px; \">档案移交人:<span></span></td>";
        html += "<td style=\"font-size:14px;text-align: left;width: 60px; \">档案室接收人:<span></span></td>";
        html += "</tr>";
        html += "<tr style=\"height:10px;\">";
        html += "<td colspan=\"3\" style=\"font-size:14px;text-align: left; \">注：此登记表一式两份，一份由移交档案的部门保存，一份由档案室保存。</td>";
        html += "</tr>";
        html += "</table>";
        html += "</div>";
	}
	
	return html;
}

//打印卷内目录
function jnml_html(field,data,ajh,bgqx,path) {
	var data_json = JSON.parse(data);
	var html = "";
	for (var i=0;i<Math.ceil(data_json.length/15);i++) {
		if (i == (Math.ceil(data_json.length/15) -1)) {
			html += "<div class=\"my_show\">";
		}
		else {
			html += "<div class=\"my_show\" style=\"page-break-after: always;\">";
		}
		html += "<table  cellspacing=\"0\" style=\"border-collapse: collapse; border-spacing: 0;background-color: transparent;max-width: 100%\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
        html += "<thead>";
        html += "<tr>";
        html += "<th colspan=\"5\">";
        html += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">";
        html += "<tr align=\"center\">";
        html += "<td style=\"font-size: 26px;padding-bottom: 25px;padding-top: 0px\">卷 内 文 件 目 录</td>";
        html += "</tr>";
        html += "</table>";
        html += "<table border=\"0\" style=\"padding-bottom: 15px;\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
        html += "<tr>";
        if (data_json.length >0) {
        	html += "<td colspan=\"2\" style=\"font-size:14px;text-align: left\">案卷号（全宗号）：<span>"+ajh+"</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 保管期限 : " +bgqx+ "</td>";
        }
        else {
        	html += "<td colspan=\"2\" style=\"font-size:14px;text-align: left\">案卷号（全宗号）：<span></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  保管期限 :</td>";
        }
        html += "</tr>";
        html += "</table>";
		html += "</th>";
		html += "</tr>";
		html += "<tr style=\"border:1px #000 solid;text-align: center;height: 50px\">";
		html += "<th width='32px' style=\"border:1px #000 solid;\">件号</th>";
		html += "<th  style=\"border:1px #000 solid;\">文 号</th>";
		html += "<th style=\"border:1px #000 solid;\">题名</th>";
		html += "<th width='70px' style=\"border:1px #000 solid;\">文件日期</th>";
		html += "<th width='32px' style=\"border:1px #000 solid;\">页数</th>";
		
		html += "</tr>";
		html += "</thead>";
		html += "<tbody >";
		
		for (var j=i*15;j<i*15+15;j++) {
			if (j<data_json.length) {
				html += "<tr class=\"content_tr\" style=\"font-size:12px;height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.jh == 'NOTHING' ? '' : data_json[j][field.jh])  +"</td>";
				html += "<td style=\"border:1px #000 solid;\"> "+ (field.wjh == 'NOTHING' ? '' : data_json[j][field.wjh])  +"</td>";
				html += "<td align=\"left\" style=\"border:1px #000 solid;\"> "+ (field.tm == 'NOTHING' ? '' : data_json[j][field.tm]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.wjrq == 'NOTHING' ? '' : data_json[j][field.wjrq]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.ys == 'NOTHING' ? '' : data_json[j][field.ys]) +"</td>";
				html += "</tr>";
			}
			else {
				html += "<tr class=\"content_tr\" style=\"height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "</tr>";
			}
		}
		html += "</tbody>";
		html += "</table>";
        html += "</div>";
	}
	
	return html;
}

//打印卷内备考表
function jnbkb_html(data,path) {
	var html = "";
	html += "<div class=\"my_show\">";
	html += "<div style=\"margin-top: 100px;margin-right: 510px;float: right;\">本卷情况说明:</div>";
	html += "<table  cellspacing=\"0\" style=\"border-collapse: collapse; border-spacing: 0;background-color: transparent;max-width: 100%\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
    html += "<thead>";
    html += "<tr>";
    html += "<th>";
    html += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">";
    html += "<tr align=\"center\">";
    html += "<td style=\"font-size: 26px;padding-bottom: 55px;padding-top: 0px\">卷内备考表</td>";
    html += "</tr>";
    html += "</table>";
	html += "</th>";
	html += "</tr>";
	html += "</thead>";
	html += "<tbody >";
	html += "<tr class=\"content_tr\" style=\"height:750px;border:1px #000 solid;\" >";
	html += "<td>";
	
    html += "</td>";
	html += "</tr>";
	
	html += "</tbody>";
	html += "</table>";
	html += "<div style=\"font-size:13px;line-height:20px;width:90%;margin-top: -640px;margin-left: 30px;\" >"+data+"</div>";
	html += "<div style=\"margin-top: 150px;margin-left:400px;\">立 卷 人________________</div>";
	html += "<div style=\"margin-top: 10px;margin-left:400px; \">检 查 人________________</div>";
	html += "<div style=\"margin-top: 10px;margin-left:396px\">立卷时间________________</div>";
	html += "</div>";
	return html;
}