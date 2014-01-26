package net.ussoft.archive.util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;


public class MyRSACoderTest {
 
    public static void main(String[] args) throws Exception {
         
        Map<String, Object> keyMap = MyRSACoder.initKeys("0");
        PublicKey publicKey = (PublicKey) keyMap.get("publicKey");
        PrivateKey privateKey = (PrivateKey) keyMap.get("privateKey");
         
        String str = "您好！";
        String str1 = "a8:20:66:54:1d:eb";
        byte[] encoderData = MyRSACoder.encryptRSA(str1.getBytes(), privateKey);
        String sign = MyRSACoder.sign(encoderData, privateKey);
        boolean status = MyRSACoder.verify(encoderData, sign, publicKey);
         
        System.out.println("原文：" + str1);
        System.out.println("密文：" + new String(encoderData));
        System.out.println("签名：" + sign);
        System.out.println("验证结果：" + status);
        

    }
}