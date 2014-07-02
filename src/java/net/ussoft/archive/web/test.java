package net.ussoft.archive.web;

import net.ussoft.archive.util.EncryptionDecryption;



public class test {
	
//	测试用的
//	1、网络版不带前台数字档案馆页面
//		0000222211171117111711171117
//		df215b68ef5189e80178b430efb92b450178b430efb92b457c6fd1e8f6767646
//	2、集团版全部功能不带前台数字档案馆页面
//		0000111711171117111711171117
//		398da0402c01b2f70178b430efb92b450178b430efb92b457c6fd1e8f6767646
	
	
//	每个4位代表含义
//	1、第一个四位如果是1117，代表系统包含前端数字档案管页面
//	2、第二个四位如果是1117 ，代表系统是集团版
//	3、第三个四位如果是1117 代表系统是网络版
//	4、第四个四位如果是1117 代表系统是单机版
//	5、第五个四位如果是1117 代表系统包含多媒体管理
//	6、第六个四位如果是1117 代表系统包含全文检索
//	7、第七个四位如果是1117 代表系统包含全文浏览器
	/**
	 * 生成系统类型代码
	 * @return
	 */
	public static String getSystemtype(String str) {
		String result= "";
		try {
            EncryptionDecryption des = new EncryptionDecryption();// 自定义密钥
            result = des.encrypt(str);
            System.out.println("加密前的字符：" + str);
            System.out.println("加密后的字符：" + result);
            System.out.println("解密后的字符：" + des.decrypt(des.encrypt(str)));

        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
	}
	/**
	 * 根据系统生成的机器码，生成系统注册码
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String getRegistCode(String str) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption();
		String registCode = des.encrypt(str);
		
		System.out.println("注册码：" + registCode);
		
		return registCode;
	}
	
	public static void main(String[] args) {
		try {
			//生成系统类型代码
            String test = "0000000000001117000000000000";
            getSystemtype(test);
            
            //生成系统注册码
            getRegistCode("2382956be5d8baae90453e7f7455c079558ca2c8fc31d610");

        } catch (Exception e) {
            e.printStackTrace();
        }
		

	}

}
