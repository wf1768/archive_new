package net.ussoft.archive.util.openoffice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 功能：开启openoffice服务 使用方法：直接生成该类对象 *****由于本机openoffice安装路径不同，需要更改openoffice的安装路径
 * 
 * @author guodh
 * */
public class CallOpenoffice {
	public final String LUCENE_URL = "LUCENE.properties";
	
	public CallOpenoffice() {
		Runtime rn = Runtime.getRuntime();
		Process p = null;
		try {
			String startService = "c:\\openoprenoffice.bat";
			File file = new File(startService); //"c:\\openoprenoffice.bat"
			if (false == file.exists()) {
				FileWriter writer = new FileWriter(startService); //"c:\\openoprenoffice.bat "
				writer.write("@echo   off ");
				writer.write("\r\n ");
				writer.write("C:");
				writer.write("\r\n ");
				// D:\\Program Files\\OpenOffice 4\\program： openoffice的安装路径路径
				String installURL = "C:\\Program Files (x86)\\OpenOffice 4\\program";
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
	 * 通过key获取.properties中的value
	 * @param key
	 * @return value
	 * */
	public String getValue(String key,String properties_file){
		String value="";
        try {
        	InputStream in = this.getClass().getClassLoader().getResourceAsStream(properties_file);
            Properties properties = new Properties();
			properties.load(in);
			value = properties.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
	/**
	 * 读取system.properties
	 * */
	public String getSystem(String key){
		String result = getValue(key, LUCENE_URL);
		return result;
	}
}
