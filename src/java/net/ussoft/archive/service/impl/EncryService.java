package net.ussoft.archive.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.ussoft.archive.dao.InitDao;
import net.ussoft.archive.model.Sys_init;
import net.ussoft.archive.service.IEncryService;
import net.ussoft.archive.util.EncryptionDecryption;

@Service
public class EncryService implements IEncryService {
	
//	private String keyString = "zljt";
	
	@Resource
	private InitDao initDao;

	@Override
	public String encryption(String str) throws Exception {
		if (str == null || str.equals("") ) {
			return "";
		}
		EncryptionDecryption des = new EncryptionDecryption();
		return des.encrypt(str);
	}

	@Override
	public String Decryption(String str) throws Exception {
		if (str == null || str.equals("") ) {
			return "";
		}
		EncryptionDecryption des = new EncryptionDecryption();
		return des.decrypt(str);
	}

//	systemtype:
//	111711171117
//	每个4位代表含义
//	1、第一个四位如果是1117，代表系统包含前端数字档案页面
//	2、第二个四位如果是1117 ，代表系统是集团版
//	3、第三个四位如果是1117 代表系统是网络版
//	4、第四个四位如果是1117 代表系统是单机版
//	5、第五个四位如果是1117 代表系统包含多媒体管理
//	6、第六个四位如果是1117 代表系统包含全文检索
//	7、第七个四位如果是1117 代表系统包含全文浏览器

	@Override
	public Boolean getInit(int num) throws Exception {
		Sys_init init = new Sys_init();
		init.setInitkey("systemtype");
		
		List<Sys_init> initList = initDao.search(init);
		
		if (null == initList || initList.size() == 0) {
			return false;
		}
		
		init = (Sys_init) initList.get(0);
		
		String decStr = Decryption(init.getInitvalue());
		try {
			String iString = decStr.substring(num, num + 4);
			if (iString.equals("1117")) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		
		return false;
	}

}
