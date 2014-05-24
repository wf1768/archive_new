package net.ussoft.archive.util.openoffice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

public class DocConverter {
	
	private static final int environment = 1;// 环境 1：windows 2:linux(只涉及pdf2swf路径问题)  
    private static String fileString;  
    private static String outputPath;// 输入路径 ，如果不设置就输出在默认的位置  
    private static String fileName;  
    private static File pdfFile;  
    private static File swfFile;  
    private static File docFile;  
  
    private static String psd2swfPath;		//pdf2swf.exe文件的路径
    
//    public DocConverter(String fileString) {  
//        ini(fileString);  
//    }  
  
    /** 
     * 重新设置file 
     *  
     * @param fileString 
     */  
    public static void setFile(String fileString) {  
        ini(fileString);  
    }  
  
    /**
     * 设置pdf2swf.exe 的路径
     * */
    public static void setPdf2swfPath(String path){
    	psd2swfPath = path;
    }
    /** 
     * 初始化 
     *  
     * @param fileString 
     */  
    private static void ini(String fileString) {  
//        this.fileString = fileString;  
        fileName = fileString.substring(0, fileString.lastIndexOf("."));  
        docFile = new File(fileString);  
        pdfFile = new File(fileName + ".pdf");  
        swfFile = new File(fileName + ".swf");  
    }  
  
    /** 
     * 转为PDF 
     *  
     * @param file 
     */  
    private static void doc2pdf() throws Exception {  
        if (docFile.exists()) {
        	if(docFile.getName().indexOf(".pdf")> 0){
        		//copyFile(docFile.toString(),pdfFile.toString());
        		pdfFile = docFile;
        	}
            if (!pdfFile.exists()) {  
            	DistorySoffice dis = new DistorySoffice();
            	Thread.sleep(5000);
                OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);  
                try {  
                    connection.connect();  
                    DocumentConverter converter = new OpenOfficeDocumentConverter(connection);  
                    converter.convert(docFile, pdfFile);  
                    // close the connection  
                    connection.disconnect();  
                    System.out.println("****pdf转换成功，PDF输出：" + pdfFile.getPath()  
                            + "****");  
                } catch (java.net.ConnectException e) {  
                    e.printStackTrace();  
                    System.out.println("****swf转换器异常，openoffice服务未启动！****");  
                    throw e;  
                } catch (com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException e) {  
                    e.printStackTrace();  
                    System.out.println("****swf转换器异常，读取转换文件失败****");  
                    throw e;  
                } catch (Exception e) {  
                    e.printStackTrace();  
                    throw e;  
                }  
            } else {  
                System.out.println("****已经转换为pdf，不需要再进行转化****");  
            }  
        } else {  
            System.out.println("****swf转换器异常，需要转换的文档不存在，无法转换****");  
        }  
    }  
  
    /** 
     * 转换成 swf 
     */  
    private static void pdf2swf() throws Exception {  
        Runtime r = Runtime.getRuntime();  
        if (!swfFile.exists()) {  
            if (pdfFile.exists()) {  
                if (environment == 1) {// windows环境处理  
                    try {  
//                        Process p = r.exec("D:/soft/swftools/pdf2swf.exe "+ pdfFile.getPath() + " -o "+ swfFile.getPath() + " -T 9");  
                    	Process p = r.exec(psd2swfPath + pdfFile.getPath() + " -o "+ swfFile.getPath() + " -T 9");
                        System.out.print(loadStream(p.getInputStream()));  
                        System.err.print(loadStream(p.getErrorStream()));  
                        System.out.print(loadStream(p.getInputStream()));  
                        System.err.println("****swf转换成功，文件输出："  
                                + swfFile.getPath() + "****");  
                        if(docFile.getName().indexOf(".pdf") < 0){
	                        if (pdfFile.exists()) {  
	                            pdfFile.delete();  
	                        }  
                        }
  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                        throw e;  
                    }  
                } else if (environment == 2) {// linux环境处理  
                    try {  
                        Process p = r.exec("pdf2swf " + pdfFile.getPath()  
                                + " -o " + swfFile.getPath() + " -T 9");  
                        System.out.print(loadStream(p.getInputStream()));  
                        System.err.print(loadStream(p.getErrorStream()));  
                        System.err.println("****swf转换成功，文件输出："  
                                + swfFile.getPath() + "****");  
                        if(docFile.getName().indexOf(".pdf") < 0){
	                        if (pdfFile.exists()) {  
	                            pdfFile.delete();  
	                        }  
                        }  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                        throw e;  
                    }  
                }  
            } else {  
                System.out.println("****pdf不存在,无法转换****");  
            }  
        } else {  
            System.out.println("****swf已经存在不需要转换****");  
        }  
    }  
  
    public static String loadStream(InputStream in) throws IOException {  
  
        int ptr = 0;  
        in = new BufferedInputStream(in);  
        StringBuffer buffer = new StringBuffer();  
  
        while ((ptr = in.read()) != -1) {  
            buffer.append((char) ptr);  
        }  
  
        return buffer.toString();  
    }  
  
    /** 
     * 转换主方法 
     */  
    public static boolean conver() {  
  
        if (swfFile.exists()) {  
//            System.out.println("****swf转换器开始工作，该文件已经转换为swf****");  
            return true;  
        }  
  
//        if (environment == 1) {  
//            System.out.println("****swf转换器开始工作，当前设置运行环境windows****");  
//        } else {  
//            System.out.println("****swf转换器开始工作，当前设置运行环境linux****");  
//        }  
        try {  
            doc2pdf();  
            pdf2swf();  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
  
        if (swfFile.exists()) {  
            return true;  
        } else {  
            return false;  
        }  
    }  
  
    /** 
     * 返回文件路径 
     *  
     * @param s 
     */  
    public static String getswfPath() {  
        if (swfFile.exists()) {  
            String tempString = swfFile.getPath();  
            tempString = tempString.replaceAll("\\\\", "/");  
            return tempString;  
        } else {  
            return "";  
        }  
  
    }  
  
    /** 
     * 设置输出路径 
     */  
    public static void setOutputPath(String outputPath) {
    	pdfFile = new File(outputPath + ".pdf");
    	swfFile = new File(outputPath + ".swf");
//        this.outputPath = outputPath;  
//        if (!outputPath.equals("")) {  
//            String realName = fileName.substring(fileName.lastIndexOf("\\"),  
//                    fileName.lastIndexOf("."));  
//            if (outputPath.charAt(outputPath.length()) == '/') {  
//                swfFile = new File(outputPath + realName + ".swf");  
//            } else {  
//                swfFile = new File(outputPath + realName + ".swf");  
//            }  
//        }  
    }  
    
    private static void copyFile(String src,String dest) throws IOException{
        FileInputStream in=new FileInputStream(src);
        File file=new File(dest);
        if(!file.exists())
            file.createNewFile();
        FileOutputStream out=new FileOutputStream(file);
        int c;
        byte buffer[]=new byte[1024];
        while((c=in.read(buffer))!=-1){
            for(int i=0;i<c;i++)
                out.write(buffer[i]);        
        }
        in.close();
        out.close();
    }
    
//    public static void main(String s[]) {  
//        DocConverter d = new DocConverter("F:/Dowln/e4c577be/1B578520/41ea12e7-e9bf-4b9e-b8ee-cdf987d0839f.doc");  
//        d.conver();
//    	CallOpenoffice callOpenoffice = new CallOpenoffice();
//    	DistorySoffice dis = new DistorySoffice();
//    } 
}
