package net.ussoft.archive.service.impl;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.ussoft.archive.dao.DocDao;
import net.ussoft.archive.dao.DocserverDao;
import net.ussoft.archive.dao.DynamicDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.model.Sys_docserver;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.IDocService;
import net.ussoft.archive.util.FileOperate;
import net.ussoft.archive.util.FtpUtil;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DocService implements IDocService {
	
	@Resource
	private DocDao docDao;
	@Resource
	private DocserverDao docserverDao;
	@Resource
	private TableDao tableDao;
	@Resource
	private DynamicDao dynamicDao;
	@Resource
	private TreeDao treeDao;
	
	@Override
	public Sys_doc selectById(String id) {
		return docDao.get(id);
	}

	@Override
	public List<Sys_doc> list() {
		return docDao.getAll();
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_doc doc) {
		Sys_doc tmp = docDao.update(doc);
		if (null != tmp) {
			return 1;
		}
		return 0;
	}

	@Transactional("txManager")
	@Override
	public Sys_doc insert(Sys_doc doc) {
		return docDao.save(doc);
	}

	@Override
	public Sys_doc selectByWhere(Sys_doc doc) {
		if (null == doc) {
			return null;
		}
		List<Sys_doc> serverList = docDao.search(doc);
		
		if (null != serverList && serverList.size() == 1) {
			return serverList.get(0);
		}
		return null;
	}

	@Transactional("txManager")
	@Override
	public ResultInfo delete(String idsString) throws SocketException, IOException {
		
		ResultInfo info = new ResultInfo();
		info.setSuccess(false);
		info.setMsg("操作失败，请重新操作，或与管理员联系。");
		int num = 0;
		
		//适应多个电子全文一并删除.假设传入的id为全文id的字符串格式。例如“1，2，3”
		String[] ids = idsString.split(",");
		
		for (String id : ids) {
			Sys_doc doc = docDao.get(id);

			//获取doc的server
			Sys_docserver docserver = docserverDao.get(doc.getDocserverid());
			//删除物理文件
			if ("LOCAL".equals(docserver.getServertype())) {
				//得到服务器路径
				String serverPath = docserver.getServerpath();
				if (!serverPath.substring(serverPath.length()-1,serverPath.length()).equals("/")) {
					serverPath += "/";
	            }
				serverPath += doc.getDocpath();
				String filename = doc.getDocnewname();
				FileOperate fo = new FileOperate();
				boolean b = fo.delFile(serverPath + filename);
	            //删除文件记录
				num = docDao.del(doc.getId());
			}
			else {
				//处理ftp删除
	            FtpUtil util = new FtpUtil();
	            util.connect(docserver.getServerip(),
	                    docserver.getServerport(),
	                    docserver.getFtpuser(),
	                    docserver.getFtppassword(),
	                    docserver.getServerpath());
//	                FileInputStream s = new FileInputStream(newFile);
//	                util.uploadFile(s, newName);
	            util.changeDirectory(doc.getDocpath());
	            boolean isDel = util.deleteFile(doc.getDocnewname());
	            util.closeServer();
	            //删除文件记录
	            num = docDao.del(doc.getId());
			}
			
			if (num > 0) {
				//判断删除电子文件后，档案如果没有挂接文件了，将档案的isdoc设置为0
				String archiveid = doc.getFileid();
				if (null != archiveid && !"".equals(archiveid)) {
					String tableid = doc.getTableid();
					//获取档案是否还有其他电子文件
					Sys_doc tmpDoc = new Sys_doc();
					tmpDoc.setFileid(archiveid);
					tmpDoc.setTableid(tableid);
					List<Sys_doc> docs = docDao.search(tmpDoc);
					if (null == docs || docs.size() == 0) {
						Sys_table table = tableDao.get(tableid);
						String sql = "update " + table.getTablename() + " set isdoc=0 where id=?";
						List<Object> values = new ArrayList<Object>();
						values.add(archiveid);
						dynamicDao.update(sql, values);
					}
				}
				
				info.setSuccess(true);
				info.setMsg("操作完毕。");
			}
		}
		
		return info;
	}

	@Override
	public List<Sys_doc> exeSql(String sql, List<Object> values) {
		return docDao.search(sql, values);
	}

	@Transactional("txManager")
	@Override
	public ResultInfo multiple(List<Map<String, String>> docs, Map<String, String> sysMap) {
		
		ResultInfo info = new ResultInfo();
		info.setSuccess(false);
		info.setMsg("操作失败，请重新操作，或与管理员联系。");
		
		Sys_tree tree = treeDao.get(sysMap.get("treeid"));
		
		if (null == tree) {
			return info;
		}
		
		//获取table
		Sys_table table = new Sys_table();
		table.setTempletid(tree.getTempletid());
		table.setTabletype(sysMap.get("tabletype"));
		table = tableDao.searchOne(table);
		
		for (Map<String, String> map : docs) {
			String fileid = map.get("fileid");
			String docid = map.get("id");
			
			Sys_doc doc = new Sys_doc();
			doc.setId(docid);
			doc.setTableid(table.getId());
			doc.setTreeid(tree.getId());
			doc.setFileid(fileid);
			docDao.update(doc);
			
			String sql = "update " + table.getTablename() + " set isdoc = 1 where id='" + fileid + "'";
			dynamicDao.execute(sql);
		}
		
		info.setSuccess(true);
		info.setMsg("操作完毕。");
		return info;
	}

}
