package net.ussoft.archive.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDocService;
import net.ussoft.archive.service.IDocserverService;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.DatabaseManager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.yapu.archive.entity.SysTempletfield;


@Controller
@RequestMapping(value="index")
public class ReadOaController extends BaseConstroller{
	
	private DatabaseManager manager = null;
	
	@Resource
	private ITreeService treeService;
	@Resource
    private IDocService docService;
	@Resource
	private IDynamicService dynamicService;
	@Resource
    private ITableService tableService;
	@Resource
	private IDocserverService docserverService;
	
	 /**
     * 读取OA数据
     * @param treeid
     * @param tableType
     * @param edoc_property 文档类型EDOC_Property，1.发文，2.收文，3.内请
     * */
	@RequestMapping(value="/read",method=RequestMethod.GET)
    public String readOaList(String treeid,String tableType,String edoc_property){
    	//文档类型EDOC_Property，1.发文，2.收文，3.内请
    	String sql = "SELECT Archive_ID AS id,DocNO AS wjh,Title AS tm,SubmitUnit AS cbdw,SubmitUser AS jbr,SubmitDate AS wjrq,CenterName AS zxmc,signuser AS cbswqbr,signcomment AS cbswqbyj,checkuser AS cbswhgr,checkcomment AS cbswhgyj,meetcomment AS hq,leadercomment AS ldps,doccomeunit AS lwdw,sender AS cs, '"+
    	treeid+"' as treeid,2 as status FROM EDoc_Archive where isend=? and EDOC_Property=?";
    	System.out.println("====="+sql+"======");
    	Object[] param = new Object[2];
    	param[0] = 1;
    	param[1] = edoc_property;
    	List list = manager.queryForList(sql, param);
    	if(list.size()>0){
    		//获取当前treeid下数据
			Sys_tree tree = treeService.getById(treeid);
			//获取table
			Sys_table table = new Sys_table();
			table.setTempletid(tree.getTempletid());
			table.setTabletype(tableType);
			table = tableService.selectByWhere(table);
			
	    	List<Sys_templetfield> fieldList = treeService.geTempletfields(treeid, tableType);
	    	
	    	
	    	//添加到本库
	    	dynamicService.exeSql(archive_insert_sql);
	    	//修改OA库
	    	Object[] param_up = new Object[3];
	    	param_up[0] = 0;
	    	param_up[1] = 1;
	    	param_up[2] = edoc_property;
	    	String oa_sql_up = "UPDATE EDoc_Archive SET isend=? WHERE isend=? AND EDOC_Property=?";
	    	manager.updateObject(oa_sql_up, param_up);
	    	//读取文件
	    	for(int i=0;i<list.size();i++){
	    		Map data = (Map) list.get(i);
	    		String archiveId = String.valueOf(data.get("id"));
	    		readSysDoc(archiveId,table.getTablename());
	    	}
    	}
    	return null;
    }
	
	 /**
     * 读取物理文件信息入库
     * @param archiveId
     * @param tableName
     * */
    private void readSysDoc(String archiveId,String tableName){
    	Object[] param = new Object[1];
    	param[0] = archiveId;
    	String sql ="SELECT id,archive_id,file_name,file_type,content,file_size FROM Doc_Content where archive_id=?";
//    	List sysList = manager.queryForList(sql, param);
    	ResultSet rset = manager.preExecuteSelect(sql, param);
    	try {
			while(rset.next()){
				Sys_doc sysDoc = new Sys_doc();
	        	String docid = UUID.randomUUID().toString();
	        	sysDoc.setId(docid);
	    		String fileName = rset.getString("file_name"); //文件名称
	    		String file_type = rset.getString("file_type");	//文件类型
	    		
	    		sysDoc.setDocoldname(fileName);
	    		sysDoc.setDocext(file_type); //扩展名   
	    		sysDoc.setDocnewname(docid + file_type); //新的文件名
	    		sysDoc.setDocpath(docid + file_type); //
	    		sysDoc.setDoclength(rset.getString("file_size")); //文件大小
	    		InputStream in = rset.getBinaryStream("content"); //文件内容
	    		
	    		
	    		Map<String, String> docServer = getDocServer();
	        	sysDoc.setDocserverid(docServer.get("serverId"));
	        	sysDoc.setDoctype("0");
	        	sysDoc.setCreatetime(CommonUtils.getTimeStamp());
	        	sysDoc.setFileid(archiveId);
	        	sysDoc.setTableid("a189736b-90b0-4ff2-92e9-8de1195d036c");
	        	//
	        	String newFileName = docid + file_type;
	        	writeFile(docServer.get("serverPath"), newFileName, in);
	        	
	        	docService.insertDoc(sysDoc);
	        	
	        	String archive_sql = "update " + tableName + " set isdoc = 1 where id='" + archiveId + "'";
	        	List<String> sqlList = new ArrayList<String>();
	        	sqlList.add(archive_sql);
	        	
	        	dynamicService.update(sqlList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    }
    //写物理文件
    private void writeFile(String filePath,String fileName,InputStream in ){
    	File file=new File(filePath+"/"+fileName);//可以是任何图片格式.jpg,.png等
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			int tmp =0;
			while((tmp=in.read())!=-1){
				fos.write(tmp);
			}
			in.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 获取DocServer
     * */
    private Map<String, String> getDocServer(){
    	Map<String, String> docServer = new HashMap<String, String>();
    	List<Sys_docserver> docserverList = docserverService.list();
		if(null!=docserverList && docserverList.size()>0){
			for (Sys_docserver sysDoc : docserverList) {
				if(sysDoc.getServerstate()==1){
					docServer.put("serverId", sysDoc.getId());
					docServer.put("serverPath", sysDoc.getServerpath());
				}
			}
		}
		return docServer;
    }
    
    public boolean makeInsertSql(List<HashMap<String, String>> list,String tableName,List<Sys_templetfield> fieldList) {

        //sb存储insert语句values前的
        StringBuffer sb = new StringBuffer();
        //value 存储values之后的
        StringBuffer value = new StringBuffer();
        try {
            for (int z=0;z<list.size();z++) {
                //创建insert sql
                HashMap<String,String> row = (HashMap<String,String>) list.get(z);
                sb.append("insert into ").append(tableName);

                sb.append(" (");
                value.append(" (");
                for (Sys_templetfield field : fieldList) {
                    sb.append(field.getEnglishname()).append(",");
                    Object col_value = row.get(field.getEnglishname().toLowerCase());
                   
                    if (field.getFieldtype().contains("VARCHAR")) {
                        if (col_value == null) {
                            value.append("'',");
                        }
                        else {
                            String tmp = String.valueOf(col_value);;
                            tmp = tmp.replace("\n"," ");
                            tmp = tmp.replaceAll("\\\\","\\\\\\\\");
                            tmp = tmp.replaceAll("[\\t\\n\\r]", "");
                            tmp = tmp.replace("'","\\\'");
                            value.append("'").append(tmp).append("',");
                        }
                    }
                    else if (field.getFieldtype().contains("timestamp")) {
                        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String s = format1.format(new Date());
                        value.append("'").append(s).append("',");
                    }
                    else {
                        if (col_value == null || col_value.equals("")) {
                            value.append("'0',");
                        }
                        else {
                            value.append(String.valueOf(col_value)).append(",");
                        }

                    }
                }
                sb.deleteCharAt(sb.length() -1).append(" ) values ");
                value.deleteCharAt(value.length() - 1).append(" )");

                sb.append(value.toString());
                System.out.println(value.toString());
                
                boolean b = dynamicDao.insert(sb.toString());
                if (!b) {
                    return false;
                }
                //清空sb和value ，进行创建下一条sql
                sb.setLength(0);
                value.setLength(0);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
        return true;
    }
}
