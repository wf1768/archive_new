package net.ussoft.archive.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @className: SystemTool
 * @description: 获取MAC地址
 * @author:
 */
public class SystemTool {

	/**
	 * 获取当前操作系统名称. return 操作系统名称 例如:windows xp,mac os,unix，linux 等.
	 */
	public static String getOSName() {
		return System.getProperty("os.name").toLowerCase();
	}

	/**
	 * 获取mac os网卡的mac地址.
	 * 
	 * @return mac地址
	 */
	public static String getMacosMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			// mac os下的命令 一般取ether 作为本地主网卡 显示信息中包含有MAC地址信息
			process = Runtime.getRuntime().exec("/bin/sh -c ifconfig -a");

			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				index = line.toLowerCase().indexOf("ether");
				if (index >= 0) {
					mac = line.substring(index + "ether".length() + 1).trim();

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	/**
	 * 获取unix网卡的mac地址.
	 * 
	 * @return mac地址
	 */
	public static String getUnixMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			// unix下的命令一般取eth0 作为本地主网卡 显示信息中包含有MAC地址信息
			process = Runtime.getRuntime().exec("ifconfig eth0");

			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				// 寻找标示字符串【hwaddr】物理网卡地址
				index = line.toLowerCase().indexOf("hwaddr");
				// 找到了地址
				if (index >= 0) {
					// 取出mac地址并去除两边空格
					mac = line.substring(index + "hwaddr".length() + 1).trim();

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	/**
	 * 获取Linux网卡的mac地址.
	 * 
	 * @author
	 * 
	 * @create date 2012.6.27
	 * @return mac地址
	 */
	public static String getLinuxMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			// linux下的命令一般取eth0 作为本地主网卡 显示信息中包含有MAC地址信息 用process流
			process = Runtime.getRuntime().exec("ifconfig ");

			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				// 寻找标示字符串 物理网卡地址
				index = line.toLowerCase().indexOf("hwaddr");
				// 找到了地址
				if (index != -1) {
					// 取出mac地址并去除两边空格
					mac = line.substring(index + 4).trim();

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}
	
	/**   
     * 获取widnows网卡的mac地址.   
     * @return mac地址   
     */     
    public static String getWindowsMACAddress() {     
        String mac = null;     
        BufferedReader bufferedReader = null;     
        Process process = null;     
        try {     
              /**  
               * windows下的命令，显示信息中包含有mac地址信息    
               */  
            process = Runtime.getRuntime().exec("ipconfig /all");   
            bufferedReader = new BufferedReader(new InputStreamReader(process     
                    .getInputStream()));     
            String line = null;     
            int index = -1;     
            while ((line = bufferedReader.readLine()) != null) {     
                   /**  
                    *  寻找标示字符串[physical address]   
                    */  
                index = line.toLowerCase().indexOf("physical address");    
                if (index != -1) {   
                    index = line.indexOf(":");   
                    if (index != -1) {   
                           /**  
                            *   取出mac地址并去除2边空格  
                            */  
                       mac = line.substring(index + 1).trim();    
                   }   
                    break;     
                }   
            }   
        } catch (IOException e) {     
            e.printStackTrace();     
        }finally {     
            try {     
                if (bufferedReader != null) {     
                    bufferedReader.close();     
                  }     
            }catch (IOException e1) {     
                e1.printStackTrace();     
              }     
            bufferedReader = null;     
            process = null;     
        }     
     
        return mac;     
    }     
	
	//以上是通过语句获取mac地址，但有时候不准确，以下是通过ip地址获取。
	
	/**
	 * 获取widnows网卡的mac地址.
	 * 
	 * @return mac地址
	 */
	/**
	 * 获取ip地址. 这种方式只能在Windows上使用可以 如果在linux平台可能有问题 会得到127.0.0.1
	 * 
	 * @return ip地址
	 */
	public static String getIpAddress() {
		String IP = "";
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IP = ia.getHostAddress().trim();

		return IP;
	}
	
	//获取windows的ip对象
	public static InetAddress getInetAddress() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ia;
	}

	/**
	 * 根据网卡获取本机配置的ip地址.在linux平台下
	 * 
	 * Author:
	 * 
	 * Create Date: 2012.6.26
	 * 
	 * @return linux ip
	 */
	public static String getIpAddressOnLinux() {
		// 枚举本机所有网卡对象
		Enumeration allNetInterfaces = null;
		try {
			// 显示本机所有网卡硬件地址
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InetAddress ip = null;
		String IPAdd = "";
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
					.nextElement();
			// System.out.println(netInterface.getName());
			ip = (InetAddress) netInterface.getInetAddresses().nextElement();
			if (ip.isSiteLocalAddress() || ip.isLoopbackAddress()) {
				// System.out.println("本机的ip="+ ip.getHostAddress());
				break;
			} else {
				ip = null;
			}
		}
		IPAdd = ip.getHostAddress();
		return IPAdd;
	}
	
	public static InetAddress getInetAddressOnLinux() {
		// 枚举本机所有网卡对象
		Enumeration allNetInterfaces = null;
		try {
			// 显示本机所有网卡硬件地址
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
					.nextElement();
			// System.out.println(netInterface.getName());
			ip = (InetAddress) netInterface.getInetAddresses().nextElement();
			if (ip.isSiteLocalAddress() || ip.isLoopbackAddress()) {
				// System.out.println("本机的ip="+ ip.getHostAddress());
				break;
			} else {
				ip = null;
			}
		}
		return ip;
	}

	/**
	 * mac os x Hardware UUID
	 * 
	 * @return UUID
	 */
	public static String getMacosxUUID() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(
					"system_profiler SPHardwareDataType");
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				index = line.toLowerCase().indexOf("hardware uuid");
				if (index >= 0) {
					mac = line.substring(index + "hardware uuid".length() + 1)
							.trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	/**
	 * Author:
	 * 
	 * Create Date: 2012.6.25
	 * 
	 * @return 通过命令获取网卡地址存在一定的问题， 直接通过IP来获取MAC ADDRESS
	 */
	public static String getMACAddress(InetAddress ia) throws Exception {

		// TODO Auto-generated method stub
		// 获得网络接口即网卡 并得到MAC地址 mac地址存在于一个byte数组中
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		// 把mac地址拼装成string
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i]&0xff是为了把BYTE化为整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		// 把字符串所有小写字母改为大写为正规的mac地址并返回
		return sb.toString().toUpperCase();
	}
	
	
	/**   
     * 测试用的main方法.   
     *    
     * @param argc   
    *            运行参数.   
	 * @throws Exception 
     */     
    public static void main(String[] argc) throws Exception {
    	System.out.println("macos uuid :" + getMacosxUUID());
    	System.out.println("macos macadd:" + getMacosMACAddress());

    	InetAddress ip=InetAddress.getLocalHost();
    	
    	System.out.println(getMACAddress(ip));
//        String os = getOSName();     
//        System.out.println(os);     
//        if(os.startsWith("windows")){     
//            String mac = getWindowsMACAddress();     
//            System.out.println("本地是windows:"+mac);     
//        }else if(os.startsWith("linux")){     
//              String mac = getLinuxMACAddress();     
//            System.out.println("本地是Linux系统,MAC地址是:"+mac);   
//        }else{     
//            String mac = getUnixMACAddress();                         
//            System.out.println("本地是Unix系统 MAC地址是:"+mac);   
//        }     
    }    

}