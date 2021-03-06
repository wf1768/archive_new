package net.ussoft.archive.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.archive.base.BaseConstroller;
import net.ussoft.archive.model.Sys_account;
import net.ussoft.archive.model.Sys_account_tree;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_init;
import net.ussoft.archive.model.Sys_org_tree;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templet;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IAccountService;
import net.ussoft.archive.service.ICodeService;
import net.ussoft.archive.service.IDocService;
import net.ussoft.archive.service.IDocserverService;
import net.ussoft.archive.service.IDynamicService;
import net.ussoft.archive.service.IInitService;
import net.ussoft.archive.service.IOrgService;
import net.ussoft.archive.service.ITableService;
import net.ussoft.archive.service.ITempletService;
import net.ussoft.archive.service.ITreeService;
import net.ussoft.archive.util.CommonUtils;
import net.ussoft.archive.util.FileOperate;
import net.ussoft.archive.util.FtpUtil;
import net.ussoft.archive.util.openoffice.CallOpenoffice;
import net.ussoft.archive.util.openoffice.DocConverter;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;


/**
 * 电子文件
 * 
 * @author wangf
 * 
 */

@Controller
@RequestMapping(value = "doc")
public class DocController extends BaseConstroller {

	@Resource
	private IDocService docService;
	@Resource
	private ITableService tableService;
	@Resource
	private IDocserverService docserverService;
	@Resource
	private IDynamicService dynamicService;
	@Resource
	private ITreeService treeService;
	@Resource
	private ITempletService templetService;
	@Resource
	private IAccountService accountService;
	@Resource
	private IOrgService orgService;
	@Resource
	private IInitService initService;
	
	@Resource
    private ICodeService codeService;
	
	private static final int BUFFER_SIZE = 2 * 1024;
	private Process process = null;
	
	@Autowired
    CommonsMultipartResolver multipartResolver;
	
	/**
     * 返回文件下载类型
     * @param docType
     * @return
     */
    private String getContentType(String docType) {
    	if ("XLS".equals(docType.toUpperCase()) || "XLSX".equals(docType.toUpperCase())) {
            return "application/vnd.ms-excel;charset=utf-8";
    	}
    	else if("DOC".equals(docType.toUpperCase()) || "DOCX".equals(docType.toUpperCase())) {
            return "application/msword;charset=utf-8";
    	}
        else if("PPT".equals(docType.toUpperCase()) || "PPTX".equals(docType.toUpperCase())) {
            return "application/vnd.ms-powerpoint;charset=utf-8";
        }
    	else if("PDF".equals(docType.toUpperCase())) {
            return "application/pdf;charset=utf-8";
    	}
    	else if ("JPG".equals(docType.toUpperCase()) || "JPEG".equals(docType.toUpperCase())) {
    		return "image/jpeg;charset=utf-8";
    	}
    	else if ("BMP".equals(docType.toUpperCase())) {
    		return "image/bmp;charset=utf-8";
    	}
    	else if ("TIF".equals(docType.toUpperCase()) || "TIFF".equals(docType.toUpperCase())) {
    		return "image/tiff;charset=utf-8";
    	}
    	else {
    		return "text/plain;charset=utf-8";
    	}
    }
    
    @RequestMapping(value = "/download")
	public void download(String id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		//获取doc对象
		Sys_doc doc = docService.selectById(id);
		
		if (null == doc ) {
			return;
		}
		//获取docserver
		Sys_docserver docserver = docserverService.selectById(doc.getDocserverid());
		
		String filename = doc.getDocoldname();
	    
        //获取doc类型
      	String docContentType = getContentType(doc.getDocext());
        response.setContentType(docContentType);
        
        String userAgent = request.getHeader("User-Agent");
        response.reset();
        if(userAgent != null && userAgent.indexOf("MSIE") == -1) {
            // FF
        	String enableFileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(filename.getBytes("UTF-8")))) + "?=";  
            response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName); 
//            String enableFileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(filename.getBytes("UTF-8")))) + "?=";  
//            response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName);
//        	response.setHeader("Content-disposition", "attachment; filename="
//                    + new String(filename.getBytes("utf-8"), "ISO8859-1"));  
        }else{
            // IE   
            String enableFileName = new String(filename.getBytes("GBK"), "ISO-8859-1");   
            response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName);
        }
        
    	String serverPath = docserver.getServerpath();
        if (!serverPath.substring(serverPath.length()-1,serverPath.length()).equals("/")) {
            serverPath += "/";
        }
        String downLoadPath = serverPath + doc.getDocpath() + doc.getDocnewname();
        
//        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");  
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;  
  
  
        long fileLength = new File(downLoadPath).length();  
        response.setHeader("Content-Length", String.valueOf(fileLength));
        
    	bis = new BufferedInputStream(new FileInputStream(downLoadPath));  
        bos = new BufferedOutputStream(response.getOutputStream());  
        byte[] buff = new byte[2048];  
        int bytesRead;  
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
            bos.write(buff, 0, bytesRead);  
        }  
        bis.close();  
        bos.close(); 
         
	}
    
	@RequestMapping(value = "/delete")
	public void delete(String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		ResultInfo info = docService.delete(id);
		out.print(info.getMsg());
	}
	
	@RequestMapping(value = "/show_upload", method = RequestMethod.GET)
	public ModelAndView show_upload(String treeid,String tabletype,String archiveid,ModelMap modelMap) {
		modelMap.put("treeid", treeid);
		modelMap.put("tabletype", tabletype);
		modelMap.put("archiveid", archiveid);
		// 获取数据
		return new ModelAndView("/view/archive/archive/upload_doc", modelMap);
	}
	
	/**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
    
    
    /**
     * 将原文件，拼接到目标文件dst
     * @param file
     * @param dst
     */
    private void cat(MultipartFile file, File dst) {
        InputStream in = null;
        OutputStream out = null;
        try {
            if (dst.exists()) {
                out = new BufferedOutputStream(new FileOutputStream(dst, true),BUFFER_SIZE);
            } else {
                out = new BufferedOutputStream(new FileOutputStream(dst),BUFFER_SIZE);
            }
            in = new BufferedInputStream(file.getInputStream(), BUFFER_SIZE);

            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @RequestMapping("/upload")
	public void upload(HttpServletRequest request,HttpServletResponse response) throws IllegalStateException, IOException{
		
    	CommonsMultipartResolver multipartResolver  = new CommonsMultipartResolver(request.getSession().getServletContext());
		if(multipartResolver.isMultipart(request)){
			MultipartHttpServletRequest  multiRequest = (MultipartHttpServletRequest)request;
			//获取参数
			String treeid = request.getParameter("treeid");
			String tabletype = request.getParameter("tabletype");
			String archiveid = request.getParameter("archiveid");
			
			//获取plupload参数
			Integer chunks = Integer.valueOf(request.getParameter("chunks"));
			String name = request.getParameter("name");
			Integer chunk = Integer.valueOf(request.getParameter("chunk"));
			//获取文件列表
			Iterator<String>  iter = multiRequest.getFileNames();
			while(iter.hasNext()){
				//获取文件对象
				MultipartFile file = multiRequest.getFile((String)iter.next());
				//获取临时文件的绝对路径
				String contextPath = getProjectRealPath() + "file" +File.separator + "upload" + File.separator;
				//生成临时文件
		        String dstPath =  contextPath + name;
		        File dstFile = new File(dstPath);
		        // 文件已存在（上传了同名的文件）
		        if (chunk == 0 && dstFile.exists()) {
		            dstFile.delete();
		            dstFile = new File(dstPath);
		        }
		        //合并文件
		        cat(file, dstFile);
		        // 完成一整个文件;
		        if (chunk == chunks - 1) {
		        	//获取临时文件对象
		        	File newFile =new File(contextPath+name);
		        	
		        	if(newFile != null){
		        		uploadSaveData(archiveid, tabletype, treeid, newFile);
//						String fileName = name;
//						String docExt = "";
//						if (fileName.lastIndexOf(".") >= 0) {
//				            docExt = fileName.substring(fileName.lastIndexOf("."));
//				        }
//						String newname = UUID.randomUUID().toString();
//						long filesize = newFile.length();
//						String tmppath = uploadSaveData(archiveid, tabletype, treeid, fileName, newname, filesize);
//						String path = tmppath + newname + docExt;
//						
//						newFile.renameTo(new File(path));
					}
		        }
			}
		}
	}
    
    //spring mvc 下载方法，也可以实现上传文件，就是不能大文件分割上传（暂时不用，留做参考）
    @RequestMapping("/upload2")
	public void uploadFile2(HttpServletRequest request,HttpServletResponse response) throws IllegalStateException, IOException{
		CommonsMultipartResolver multipartResolver  = new CommonsMultipartResolver(request.getSession().getServletContext());
		if(multipartResolver.isMultipart(request)){
			MultipartHttpServletRequest  multiRequest = (MultipartHttpServletRequest)request;
			String treeid = request.getParameter("treeid");
			String tabletype = request.getParameter("tabletype");
			String archiveid = request.getParameter("archiveid");
			
			Iterator<String>  iter = multiRequest.getFileNames();
			while(iter.hasNext()){
				MultipartFile file = multiRequest.getFile((String)iter.next());
				if(file != null){
					String fileName = file.getOriginalFilename();
					String docExt = "";
					if (fileName.lastIndexOf(".") >= 0) {
			            docExt = fileName.substring(fileName.lastIndexOf("."));
			        }
					String newname = UUID.randomUUID().toString();
					long filesize = file.getSize();
//					String tmppath = uploadSaveData(archiveid, tabletype, treeid, fileName, newname, filesize);
//					String path = "/Users/wangf/develop/testdoc/3/" + fileName;
//					String path = tmppath + newname + docExt;
//					File localFile = new File(path);
//					file.transferTo(localFile);
				}
				
			}
		}
	}
    
    /**
     * 保存上传电子全文的信息到数据库
     * @param archiveid
     * @param tabletype
     * @param treeid
     * @throws IOException 
     */
	private String uploadSaveData(String archiveid,String tabletype,
    		String treeid,File tmpFile) throws IOException {
    	Sys_account sessionAccount = getSessionAccount();
    	if (null == sessionAccount) {
            return null;
        }
    	
    	String sql = "";
//        List<Object> values = new ArrayList<Object>();
        
        //获取tree 对象
		Sys_tree tree = treeService.getById(treeid);
		//获得tree对应的templet
		Sys_templet templet = templetService.getByid(tree.getTempletid());
		//获取table
		Sys_table table = new Sys_table();
		table.setTempletid(templet.getId());
		table.setTabletype(tabletype);
		table = tableService.selectByWhere(table);
		
		//声明一个标识，是否是单个文件挂接。如果fileid有值，应该把档案条目的isdos设置为1
    	boolean isdoc = false;
    	//获取文件服务器
    	Sys_docserver docserver = new Sys_docserver();
    	docserver.setServerstate(1);
    	docserver = docserverService.selectByWhere(docserver);
        
        String docExt = "";//扩展名
        //文件的doc的id
        String docId = UUID.randomUUID().toString();
        
        
        String filename = tmpFile.getName();
        long filesize = tmpFile.length();
        //获取扩展名
        if (filename.lastIndexOf(".") >= 0) {
            docExt = filename.substring(filename.lastIndexOf("."));
        }
        //文件的新名字
        String newname = docId + docExt;
        Sys_doc doc = new Sys_doc();
        doc.setId(docId);
        doc.setDocnewname(newname);
        doc.setDoclength(CommonUtils.formatFileSize(filesize));
        doc.setDocoldname(filename);
        doc.setDoctype("0");
        doc.setDocauth("1");
        doc.setDocext(docExt.substring(1).toUpperCase());
        doc.setCreaterid(sessionAccount.getId());
        doc.setCreater(sessionAccount.getAccountcode());
        doc.setCreatetime(CommonUtils.getTimeStamp());
        if (null != archiveid && !"".equals(archiveid)) {
        	doc.setFileid(archiveid);
        	isdoc = true;
        }
        else {
        	doc.setFileid("");
        }
        
        if (null != table) {
        	doc.setTableid(table.getId());
        }
        else {
        	doc.setTableid("");
        }
        
        if (null != treeid && !"".equals(treeid)) {
        	doc.setTreeid(treeid);
        }
        else {
        	doc.setTreeid("");
        }
        
//        doc.setDocpath( docserver.getServerpath() + newName);
        //生成路径
        String docpath = "";
        String node = tree.getTreenode();
        String serverpath = docserver.getServerpath();
        if (null == serverpath || "".equals(serverpath)) {
        	serverpath = "/";
        }
        else if (!serverpath.substring(serverpath.length()-1,serverpath.length()).equals("/")) {
			serverpath += File.separator;
        }
        String tmppath = serverpath;
        FtpUtil ftpUtil = new FtpUtil();
        //按treenode来生成电子文件的文件夹
        if (null != node && !node.equals("")) {
        	String[] nodeArr = node.split("#");
        	for (String str : nodeArr) {
				if (!str.equals("0")) {
					if (docserver.getServertype().equals("LOCAL")) {
						//如果是本地服务器
						tmppath += str + File.separator;
						FileOperate.isExist(tmppath);
					}
					else if (docserver.getServertype().equals("FTP")) {
						tmppath += str + "/";
						ftpUtil.connect(docserver.getServerip(),
						        docserver.getServerport(),
						        docserver.getFtpuser(),
						        docserver.getFtppassword(),
						        docserver.getServerpath());
						Boolean existDir = ftpUtil.existDirectory(tmppath);
						if (!existDir) {
							ftpUtil.createDirectory(tmppath);
						}
						ftpUtil.closeServer();
					}
					docpath += str + "/";
					
				}
			}
        }
        
        if ("FTP".equals(docserver.getServertype())) {
            try {
            	ftpUtil.connect(docserver.getServerip(),
				        docserver.getServerport(),
				        docserver.getFtpuser(),
				        docserver.getFtppassword(),
				        docserver.getServerpath());
            	ftpUtil.changeDirectory(docpath);
				FileInputStream s = new FileInputStream(tmpFile);
				ftpUtil.uploadFile(s, newname);
				ftpUtil.closeServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
            //ftp上传完成，删除临时文件
//            deleteFile(newFile.getPath());
        } else if ("LOCAL".equals(docserver.getServertype())){
            String serverPath = docserver.getServerpath();
            String savePath = docserver.getServerpath();
            if (null == serverPath || "".equals(serverPath)) {
                    return null;
            }
            else {
                if (!serverPath.substring(serverPath.length()-1,serverPath.length()).equals("/")) {
                    savePath += "/";
                }
            }
            System.out.println("上传文件路径+Name=："+savePath+docpath+newname);
            tmpFile.renameTo(new File(savePath + docpath + newname));
            System.out.println("上传文件路径+Name=："+savePath+newname+"上传文件结束，upload file over");
            //删除临时文件.renameTO 的同时，已经删除了，这里再删除一次，避免
//            deleteFile(newFile.getPath());
        }
        
        doc.setDocpath(docpath);
        doc.setDocserverid(docserver.getId());
        docService.insert(doc);
        //把档案条目的isdoc字段设置
        if (isdoc) {
        	sql = "update " + table.getTablename() + " set isdoc = 1 where id='" + archiveid + "'";
        	dynamicService.exeSql(sql);
        }
        
        return tmppath;
    }
    
	
	@RequestMapping(value="/multiple",method=RequestMethod.POST)
	public void multiple(String data,String sys,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		if (null == data || data.equals("")) {
			out.print("未获得数据，请重新操作或与管理员联系。");
			return;
		}
		
		List<Map<String, String>> docs = (List<Map<String, String>>) JSON.parse(data);
		Map<String, String> sysMap = (Map<String, String>) JSON.parse(sys);
		
		ResultInfo info = docService.multiple(docs, sysMap);
		
		out.print(info.getMsg());
	}
	
	
	////////////////////////////////////////////////////guodh-up////////////////////////////////////////////////////////////////////////////
	
	/**
     * 全文检索，文件预览
     * @throws IOException 
     * */
	@RequestMapping(value="/preview",method=RequestMethod.POST)
    public void filePreview(HttpServletRequest request,HttpServletResponse response,String docid,String treeid) throws IOException{
    	PrintWriter out = response.getWriter();
    	int isAuth = isAuthority("fileview", treeid);
    	if(isAuth != 0){
    		out.write("1"); //有权限
    	}else{
    		out.write("0"); //没权限
    	}
    }
    /**
     * 在线预览电子全文
     * @param request
     * @param response
     * @param docid
     * @param treeid
     * */
    @RequestMapping(value="/showSwfFile",method=RequestMethod.POST)
    public void showSwfFile(HttpServletRequest request,HttpServletResponse response,String docid,String treeid) throws Exception{
    	PrintWriter out = response.getWriter();
    	int isAuth = isAuthority("fileview", treeid);
    	if(isAuth != 0){
    		DocConverter docConverter = new DocConverter();
        	Sys_doc doc = docService.selectById(docid);
        	//判断文件所属服务器
        	String serverid = doc.getDocserverid();
        	//得到所属服务器的信息
        	Sys_docserver docServer = docserverService.selectById(serverid);
        	//判断服务器类型。根据不同类型，执行不同的操作
        	String serverType = docServer.getServertype();
        	String savePath = "";
        	if ("LOCAL".equals(serverType)) {
        		savePath = docServer.getServerpath();
        	}//else if ("FTP".equals(serverType)) {
        		//savePath = docServer.getServerpath();
        	//}
        	//openoffice安装地址
        	Sys_init init_openofficeurl = initService.selectById("3");
        	//文件转换bat路径
        	Sys_init init_serviceurl = initService.selectById("4");
        	//开启OpenOffice服务
        	DistorySoffice(init_serviceurl.getInitvalue(),init_openofficeurl.getInitvalue());
        	
        	String pdf2swfPath = request.getSession().getServletContext().getRealPath("/WEB-INF/tools/swftools/pdf2swf.exe ");
        	docConverter.setPdf2swfPath(pdf2swfPath);
        	String fileName = savePath + "/" + doc.getDocpath() + doc.getDocnewname();
        	docConverter.setFile(fileName);
        	String temp = "/file/SWFFILE/" + doc.getDocpath();
			String tableIndexDir = request.getSession().getServletContext().getRealPath(temp)+ File.separator;
			File file =new File(tableIndexDir);    
			//如果文件夹不存在则创建    
			if(!file .exists()  && !file .isDirectory()){       
			    file .mkdirs();
			}
        	docConverter.setOutputPath(tableIndexDir + doc.getId());
        	boolean flag = docConverter.conver();
        	if(flag){
	        	String path = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
	        	path = path+temp+"/"+doc.getId()+".swf";
	        	out.write(path);
        	}else{
        		out.write("1");
        	}
    	}else{
    		out.write("0");
    	}
    }
    
    /**
     * 全文检索-打印
     * @throws IOException 
     * */
    @RequestMapping(value="/filePrint",method=RequestMethod.POST)
    public void filePrint(HttpServletResponse response,String treeid) throws IOException{
    	PrintWriter out = response.getWriter();
    	int isAuth = isAuthority("fileprint", treeid);
    	if(isAuth != 0){
    		out.write("1"); //有权限
    	}else{
    		out.write("0"); //没权限
    	}
    }
    
    /**
     * 全文检索-下载
     * @throws IOException 
     * */
    @RequestMapping(value="/fileDown",method=RequestMethod.POST)
    public void fileDown(HttpServletResponse response,String treeid) throws IOException{
    	PrintWriter out = response.getWriter();
    	int isAuth = isAuthority("filedown", treeid);
    	if(isAuth != 0){
    		out.write("1"); //有权限
    	}else{
    		out.write("0"); //没权限
    	}
    }
    
    /**
     * 是否有（查看、下载、打印）权限
     * @param fileType :fileview(查看) filedown(下载) fileprint(打印)
     * @return
     * */
    public int isAuthority(String fileType,String treeid){
    	int filescan = 0;
    	int filedown = 0;
    	int fileprint = 0;
    	
		//先查看账户本身是否有权限
    	Sys_account account = getSessionAccount();
		Sys_account_tree accountTree =  accountService.getTreeAuth(account.getId(), treeid);
		if(accountTree != null){
			filescan = accountTree.getFilescan();
			filedown = accountTree.getFiledown();
			fileprint = accountTree.getFileprint();
		}else{
			//否则查看该账户所在组的权限
		 	Sys_org_tree orgTree = orgService.getTreeAuth(account.getOrgid(), treeid);
		 	if(orgTree != null){
		 		filescan = orgTree.getFilescan();
				filedown = orgTree.getFiledown();
				fileprint = orgTree.getFileprint();
		 	}
		}
		if(fileType.equals("fileview")){
			return filescan;
		}else if(fileType.equals("filedown")){
			return filedown;
		}else{
			return fileprint;
		}
    }
    
    /**
	 * 开启soffice进程
	 * */
	public void DistorySoffice(String startService,String installURL) {
		try {
			// 显示进程
			process = Runtime.getRuntime().exec("tasklist");
			Scanner in = new Scanner(process.getInputStream());
			boolean flag = false;
			while (in.hasNextLine()) {
				String processString = in.nextLine();
				if (processString.contains("soffice.exe")) {
					flag = true;
					// 关闭soffice进程的命令
//					String cmd = "taskkill /f /im soffice.exe";
//					process = Runtime.getRuntime().exec(cmd);
//					System.out.println("openoffice正常关闭.......");
					System.out.println("OpenOffice服务已开启。。。");
				}
			}
			if(!flag){
				CallOpenoffice(startService, installURL);
				DistorySoffice(startService, installURL);
				System.out.println("进行开启OpenOffice服务。。。");
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 开启openoffice服务
	 * */
	public void CallOpenoffice(String startService,String installURL) {
		Runtime rn = Runtime.getRuntime();
		Process p = null;
		try {
			//String startService = "f:\\openoprenoffice.bat";
			File file = new File(startService); //"c:\\openoprenoffice.bat"
			if (false == file.exists()) {
				FileWriter writer = new FileWriter(startService); //"c:\\openoprenoffice.bat "
				writer.write("@echo   off ");
				writer.write("\r\n ");
				writer.write("C:");
				writer.write("\r\n ");
				// D:\\Program Files\\OpenOffice 4\\program： openoffice的安装路径路径
//				String installURL = "C:\\Program Files (x86)\\OpenOffice 4\\program";
				writer.write("cd "+installURL); //C:\\Program Files\\OpenOffice 4\\program
				writer.write("\r\n ");
//				writer.write("soffice -headless -accept="
//						+ "socket,host=127.0.0.1,port=8100;urp;"
//						+ " -nofirststartwizard");
				writer.write("soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard");
				writer.write("\r\n ");
				writer.write("@echo   on ");
				writer.close();
			}
			p = rn.exec("cmd.exe /C "+startService); //c:\\openoprenoffice.bat
		}catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 打开设置电子全文权限页面
	 * @param ids
	 */
	@RequestMapping(value="/openSetDocauth",method=RequestMethod.GET)
	public ModelAndView setDocauth(String ids,ModelMap modelMap) {
		modelMap.put("ids",ids);
		//获取电子全文代码
		Sys_code code = new Sys_code();
		code.setTempletfieldid("DOCAUTH");
		List<Sys_code> codes = codeService.selectByWhere(code);
		modelMap.put("docauth", codes);
		return new ModelAndView("/view/archive/archive/setDocauth",modelMap);
	}
	
	@RequestMapping(value="/setDocauth",method=RequestMethod.POST)
	public void updateDocauth(String ids,String authid,HttpServletResponse response) throws IOException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		if (authid == null || "".equals(authid) || ids == null || "".equals(ids) ) {
			result = "failure";
			out.print(result);
			return;
		}
		
		
		String[] idArr = ids.split(",");
		
		for (String id : idArr) {
			Sys_doc doc = new Sys_doc();
			doc.setId(id);
			doc.setDocauth(authid);
			docService.update(doc);
		}
		
		out.print(result);
	}
}
