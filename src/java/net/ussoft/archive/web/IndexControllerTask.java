package net.ussoft.archive.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_init;
import net.ussoft.archive.service.IDocService;
import net.ussoft.archive.service.IDocserverService;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.service.IIndexerService;
import net.ussoft.archive.service.IInitService;
import net.ussoft.archive.util.ReadFile;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexControllerTask extends BaseConstroller{

	@Resource
	private IDocService docService;
	@Resource
	private IDocserverService docserverService;
	@Resource
	private IIndexerService indexerService;
	@Resource
	private IInitService initService;
	@Resource
    private IEncryService encryService;
	
	/**
	 * 创建电子全文索引
	 * 0 0 12 * * ? 每天12点触发
	 * 0 15 10 ? * * 每天10点15分触发
	 * 0 15 10 * * ? 每天10点15分触发
	 * 0 15 10 * * ? * 每天10点15分触发
	 * @throws Exception 
	 */
//	@Scheduled(cron = "0 0 12 * * ?")  
	//@Scheduled(fixedRate = 1000*9)  //启动出发，每隔9秒执行一次
	public void createFilesIndexer() throws Exception{
		//获取有没有全文浏览器，如果没有，前台不显示查看按钮
		if (encryService.getInit(20)) {
			
			//此处应创建所有服务器下的电子。。。。。暂时本地
			//取得当前开启的服务器
			List<Sys_docserver> docserverList = docserverService.list();
			System.out.println(docserverList.size());
			for(int i=0;i<docserverList.size();i++){
				Sys_docserver docserver = docserverList.get(i);
				
				//取得当前开启服务器下的文本型全文
		    	List<Sys_doc> docList = new ArrayList<Sys_doc>();
				//获取档案的电子全文  TODO 要获取当前帐户电子全文权限范围的
				String sql = "select * from sys_doc where docserverid=?";
				List<Object> values = new ArrayList<Object>();
				values.clear();
				values.add(docserver.getId());
				docList = docService.exeSql(sql, values);
		    	
		    	List<Sys_doc> list = new ArrayList<Sys_doc>();
		    	
		    	HashMap<String,String> map = new HashMap<String, String>();
		    	if ("LOCAL".equals(docserver.getServertype())) {
		    		for (Sys_doc doc :docList) {
		        		String content = "";
		        		String ext = doc.getDocext();
		        		ReadFile read = new ReadFile();
		        		String serverPath = docserver.getServerpath(); //服务器路径
		        		if (!serverPath.substring(serverPath.length()-1,serverPath.length()).equals("/")) {
		            		serverPath += "/";
		                }
		        		//文件路径
		        		String path = serverPath + doc.getDocpath() + doc.getDocnewname();
		        		File filePath = new File(path);
		        		//判断文件是否存在
		        		if(filePath.exists()){
			        		try {
								//处理doc文档
								if ("DOC".equals(ext)) {
									content = read.readWord(path);
									list.add(doc);
								}//处理doc2007格式
								else if ("DOCX".equals(ext)) {
									content = read.readWord2007(path);
									list.add(doc);
								}//处理xls格式
								else if("XLS".equals(ext)) {
									content = read.ReadExcel(path);
									list.add(doc);
								}//处理xlsx格式  2007格式
								else if("XLSX".equals(ext)) {
									content = read.readExcel2007(path);
									list.add(doc);
								}//处理txt格式
								else if("TXT".equals(ext)) {
									content = read.readTxt(path);
									list.add(doc);
								}
								else if("PDF".equals(ext)) {
									content = read.readPdf(path);
									list.add(doc);
								}
								else if("PPT".equals(ext)) {
									content = read.readPowerPoint(path);
									list.add(doc);
								}
								else {
									content = "";
								}
								
								if (!"".equals(content)) {
									map.put(doc.getId(), content);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
		        		}
		        	}
		    	}else {
		    		//TODO 处理ftp类型的
		    	}
		    	
		    	if (!map.isEmpty()) {
		    		Sys_init init = initService.selectById("2");
		    		indexerService.createIndex(docserver.getId(), list, map, "CREATE",init.getInitvalue());
		    	}
			}
		}
	}
	
}
