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
		searchTxt = searchTxt.trim();
		if (!searchTxt.equals("")) {
			String[] txts = searchTxt.split("\\s+");
//			String[] txts = searchTxt.split(" ");
			sb.append(" (");
			for (String str : txts) {
				str = str.trim();
				
				for(int i=0;i<fields.size();i++){
					if (fields.get(i).getSort() > 0 && fields.get(i).getIssearch() == 1 && !fields.get(i).getFieldtype().equals("INT")) {
//						if(i < fields.size()-1){
							sb.append(fields.get(i).getEnglishname() + " LIKE '%" + str + "%' OR ");
//						}else{
//							sb.append(fields.get(i).getEnglishname() + " LIKE '%" + str + "%'");
//						}
					}
				}
			}
			like = sb.toString();
			like = like.substring(0, sb.length()-4);
			like += ")";
//			sb.append(")");
		}
		
		//得到字段
		String fieldString = "";
		for (Sys_templetfield field : fields) {
			fieldString += field.getEnglishname() + ",";
		}
		fieldString = fieldString.substring(0, fieldString.length()-1);
		
		String sql = "SELECT "+fieldString+" FROM " + tablename;
		if (!sb.toString().equals("")) {
			sql += " WHERE " + like;
		}
		
		return sql;
	}

}
