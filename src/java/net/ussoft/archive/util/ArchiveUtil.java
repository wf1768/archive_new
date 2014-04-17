package net.ussoft.archive.util;

import java.util.List;

import net.ussoft.archive.model.Sys_templetfield;

public class ArchiveUtil {
	
	public static void main(String[] args) throws Exception {
		
	}
	
	public ArchiveUtil() {
		
	}
	
	public static String createSql(String tablename,String searchTxt,List<Sys_templetfield> fields) {
		
		StringBuffer sb = new StringBuffer();
		String like = "";
		if (null != searchTxt && !"".equals(searchTxt)) {
			searchTxt = searchTxt.trim();
			if (!searchTxt.equals("")) {
				String[] txts = searchTxt.split("\\s+");
	//			String[] txts = searchTxt.split(" ");
				
				for (String str : txts) {
					str = str.trim();
					sb.append(" (");
					
					for(int i=0;i<fields.size();i++){
						if (fields.get(i).getSort() > 0 && fields.get(i).getIssearch() == 1 && !fields.get(i).getFieldtype().equals("INT")) {
	//						if(i < fields.size()-1){
								sb.append(fields.get(i).getEnglishname() + " LIKE '%" + str + "%' OR ");
	//						}else{
	//							sb.append(fields.get(i).getEnglishname() + " LIKE '%" + str + "%'");
	//						}
						}
					}
					String tmp = sb.substring(0, sb.length()-4);
					sb.setLength(0);
					sb.append(tmp);
					sb.append(")");
					sb.append(" and ");
				}
				like = sb.toString();
				like = like.substring(0, sb.length()-5);
	//			like += ")";
	//			sb.append(")");
			}
		}
		
		//得到字段
		String fieldString = "";
		if (null != fields && fields.size() > 0) {
			for (Sys_templetfield field : fields) {
				if (field.getSort() < 0 || field.getIsedit() == 1 ) {
					fieldString += field.getEnglishname() + ",";
				}
			}
			if (!"".equals(fieldString)) {
				fieldString = fieldString.substring(0, fieldString.length()-1);
			}
		}
		
		if ("".equals(fieldString)) {
			fieldString = "*";
		}
		String sql = "SELECT "+fieldString+" FROM " + tablename;
		if (!sb.toString().equals("")) {
			sql += " WHERE " + like;
		}
		
		return sql;
	}

}
