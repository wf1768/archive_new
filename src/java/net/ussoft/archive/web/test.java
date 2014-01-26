package net.ussoft.archive.web;

import net.ussoft.archive.util.EncryptionDecryption;



public class test {

//	每个4位代表含义
//	1、第一个四位如果是1117，代表系统包含前端数字档案管页面
//	2、第二个四位如果是1117 ，代表系统是集团版
//	3、第三个四位如果是1117 代表系统是网络版
//	4、第四个四位如果是1117 代表系统是单机版
//	5、第五个四位如果是1117 代表系统包含多媒体管理
//	6、第六个四位如果是1117 代表系统包含全文检索
//	7、第七个四位如果是1117 代表系统包含全文浏览器
	public static void main(String[] args) {
		try {
            String test = "0000111711171117111711171117";
            EncryptionDecryption des = new EncryptionDecryption("zljt");// 自定义密钥
            System.out.println("加密前的字符：" + test);
            System.out.println("加密后的字符：" + des.encrypt(test));
            System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));

            System.out.println("解密后的字符："
                    + des.decrypt("fe14c7c41f7d2a94b07d7928b21346e7"));

        } catch (Exception e) {
            e.printStackTrace();
        }
		

	}

}
