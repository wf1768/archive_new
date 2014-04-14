/*
 * 打印公共类。
 * wangf
 */

/**
 * printcode:	打印类型，要打印什么。例如案卷目录代码 AJML
 */
function create_print_html(printcode,field,data,ajh) {
	if (printcode == 'AJML') {
		return ajml_html(field,data);
	}
	else if (printcode == 'JNML') {
		return jnml_html(field,data,ajh);
	}
}

function ajml_html(field,data) {
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
        html += "<td style=\"font-size: 26px;padding-bottom: 25px;padding-top: 0px\">&nbsp;&nbsp;案 卷 目 录</td>";
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
				html += "<tr class=\"content_tr\" style=\"height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\">"+ (j+1) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.ajh == 'NOTHING' ? '': data_json[j][field.ajh]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.tm == 'NOTHING' ? '' : data_json[j][field.tm]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.ys == 'NOTHING' ? '' : data_json[j][field.ys]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.bz == 'NOTHING' ? '' : data_json[j][field.bz]) +"</td>";
			}
			else {
				html += "<tr class=\"content_tr\" style=\"height:60px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
			}
		}
		html += "</tbody>";
		html += "</table>";
        html += "</div>";
	}
	
	return html;
}

//打印卷内目录
function jnml_html(field,data,ajh) {
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
        html += "<td style=\"font-size: 26px;padding-bottom: 25px;padding-top: 0px\">卷 内 目 录</td>";
        html += "</tr>";
        html += "</table>";
        html += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" class=\"print_font\">";
        html += "<tr>";
        if (data_json.length >0) {
        	html += "<td colspan=\"2\" style=\"font-size:14px;text-align: right\">案卷号：<span>"+ajh+"</span></td>";
        }
        else {
        	html += "<td colspan=\"2\" style=\"font-size:14px;text-align: right\">案卷号：<span></span></td>";
        }
        html += "</tr>";
        html += "</table>";
		html += "</th>";
		html += "</tr>";
		html += "<tr style=\"border:1px #000 solid;text-align: center;height: 50px\">";
		html += "<th style=\"border:1px #000 solid;\">序号</th>";
		html += "<th style=\"border:1px #000 solid;\">卷内序号</th>";
		html += "<th style=\"border:1px #000 solid;\">题名</th>";
		html += "<th style=\"border:1px #000 solid;\">页数</th>";
		html += "<th style=\"border:1px #000 solid;\">备注</th>";
		html += "</tr>";
		html += "</thead>";
		html += "<tbody >";
		
		for (var j=i*16;j<i*16+16;j++) {
			if (j<data_json.length) {
				html += "<tr class=\"content_tr\" style=\"height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\">"+ (j+1) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.jnxh == 'NOTHING' ? '' : data_json[j][field.jnxh])  +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.tm == 'NOTHING' ? '' : data_json[j][field.tm]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.ys == 'NOTHING' ? '' : data_json[j][field.ys]) +"</td>";
				html += "<td style=\"border:1px #000 solid;\">"+ (field.bz == 'NOTHING' ? '' : data_json[j][field.bz]) +"</td>";
			}
			else {
				html += "<tr class=\"content_tr\" style=\"height:50px;border:1px #000 solid;text-align: center\">";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
				html += "<td style=\"border:1px #000 solid;\"></td>";
			}
		}
		html += "</tbody>";
		html += "</table>";
        html += "</div>";
	}
	
	return html;
}