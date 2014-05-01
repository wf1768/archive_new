package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.ussoft.archive.dao.CodeDao;
import net.ussoft.archive.dao.TableDao;
import net.ussoft.archive.dao.TempletDao;
import net.ussoft.archive.dao.TempletfieldDao;
import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_code;
import net.ussoft.archive.model.Sys_table;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ITempletfieldService;
import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TempletfieldService implements ITempletfieldService {
	
	@Resource
	private TreeDao treeDao;
	@Resource
	private TableDao tableDao;
	@Resource
	private TempletDao templetDao;
	@Resource
	private TempletfieldDao templetfieldDao;
	@Resource
	private CodeDao codeDao;

	/**
	 * 暂时没有用。做接口的调用如果没有用到就删除
	 */
	@Override
	public ResultInfo getField(String treeid) {
		ResultInfo info = new ResultInfo();
		
		if (treeid.equals("") || null == treeid) {
			info.setSuccess(false);
			info.setMsg("没有获得treeid参数。");
		}
		
		//获取tree对象
		Sys_tree tree = treeDao.get(treeid);
		if (tree == null) {
			info.setSuccess(false);
			info.setMsg("根据参数treeid，没有获得tree对象。");
			return info;
		}
		
		if (tree.getTreetype().equals("F")) {
			info.setSuccess(false);
			info.setMsg("根据treeid参数，得到的树节点类型为“F”（文件夹）。为“W”的才是数据节点。请重新选择.");
			return info;
		}
		
		List<Object> values=new ArrayList<Object>();
		values.add(tree.getTempletid());
		values.add("02");
		//根据templetid获取tableid
		List<Sys_table> tableList = tableDao.search("select * from sys_table where templetid=? and tabletype=?", values);
		
		//获取字段
		values.clear();
		values.add(tableList.get(0).getId());
		List<Sys_templetfield> fieldList = templetfieldDao.search("select * from sys_templetfield where tableid=? and sort <> -1", values);
		
		info.setSuccess(true);
		info.setMsg("获取字段成功。");
		info.put("list", fieldList);
		
		return info;
		
	}
	
	private Boolean saveField(Sys_templetfield templetfield ) {
		
		//获取真实表名
		Sys_table table = tableDao.get(templetfield.getTableid());
		if (null == table) {
			return false;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("alter table ").append(table.getTablename()).append(" add ").append(templetfield.getEnglishname());
		
		if (templetfield.getFieldtype().equals("VARCHAR")) {
			sb.append(" varchar");
		}
		else if (templetfield.getFieldtype().equals("DATE")) {
			sb.append(" varchar");
		}
		else if (templetfield.getFieldtype().equals("INT")) {
			sb.append(" int");
		}
		else {
			sb.append(" varchar");
		}
		
		if (templetfield.getFieldtype().equals("VARCHAR") ) {
			if (templetfield.getFieldsize() >= 30 && templetfield.getFieldsize() < 2000) {
				sb.append("(").append(templetfield.getFieldsize()).append(")");
			}
			else {
				sb.append("(").append("200").append(")");
			}
		}
		else if (templetfield.getFieldtype().equals("INT")) {
			sb.append("(").append("11").append(")");
		}
		else if (templetfield.getFieldtype().equals("DATE")) {
			if (templetfield.getFieldsize() >= 30 && templetfield.getFieldsize() < 2000) {
				sb.append("(").append(templetfield.getFieldsize()).append(")");
			}
			else {
				sb.append("(").append("60").append(")");
			}
		}
		
		if (null != templetfield.getDefaultvalue() && !templetfield.getDefaultvalue().equals("")) {
			if (templetfield.getFieldtype().equals("VARCHAR")) {
				sb.append(" default '").append(templetfield.getDefaultvalue()).append("'");
			}
			else if (templetfield.getFieldtype().equals("DATE")) {
				sb.append(" default '").append(templetfield.getDefaultvalue()).append("'");
			}
			else if (templetfield.getFieldtype().equals("INT")) {
				sb.append(" default ").append(templetfield.getDefaultvalue());
			}
		}
		
		sb.append(";");
//		tableDao.add(sb.toString(), null);
		tableDao.execute(sb.toString());
		
//		select max(sort) from sys_templetfield where tableid='1' 
//		alter table sys_templet add sort int(11) default 0;
//		alter table sys_org_tree add docauth varchar(40) default 'asdf';
		
		//查sort最大值＋1赋予新字段sort
		List<Object> values = new ArrayList<Object>();
		String sql = "select max(sort) from sys_templetfield where tableid=?";
		values.clear();
		values.add(templetfield.getTableid());
		Long lo = templetfieldDao.getLong(sql, values);
		
		if (lo > 0) {
			templetfield.setSort(lo.intValue()+1);
		}
		templetfield = templetfieldDao.save(templetfield);
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public String insert(Sys_templetfield templetfield) {
		
		String result = "success";
		
		//判断字段英文名是否存在
		String sql = "select * from  sys_templetfield where englishname=? and tableid=? and accountid=?";
		List<Object> values = new ArrayList<Object>();
		values.add(templetfield.getEnglishname());
		values.add(templetfield.getTableid());
		values.add("SYSTEM");
		int num = templetfieldDao.getCount(sql, values);
		
		if (num >0) {
			return "repeat";
		}
		
		//首先插入真实表
		if (null == templetfield.getTableid() || templetfield.getTableid().equals("")) {
			return "failure";
		}
		//插入的是系统字段
		templetfield.setAccountid("SYSTEM");
		
		Boolean b = saveField(templetfield);
		
		if (!b) {
			return "failure";
		}
		
		//成功插入系统字段后，处理帐户字段
		Sys_table table = tableDao.get(templetfield.getTableid());
		//获取多少个帐户对当前表字段有帐户字段
		sql = "select accountid,count(*) as count from sys_templetfield where tableid=? and accountid <> 'SYSTEM' group by accountid having count>0";
		values.clear();
		values.add(table.getId());
//		List<Sys_templetfield> templetfields = templetfieldDao.search(sql, values);
		List<Map<String, Object>> templetfields = templetfieldDao.searchForMap(sql, values);
		//为每个帐户插入新字段
		for (Map<String, Object> map : templetfields) {
			templetfield.setId(UUID.randomUUID().toString());
			templetfield.setAccountid(map.get("accountid").toString());
			templetfieldDao.save(templetfield);
		}
		
		return result;
	}

	@Override
	public Sys_templetfield getById(String id) {
		return templetfieldDao.get(id);
	}

	@Transactional("txManager")
	@Override
	public int update(Sys_templetfield templetfield) {
		//首先判断字段大小是否变化
		Sys_templetfield tmp = templetfieldDao.get(templetfield.getId());
		
		//获取table
		Sys_table table = tableDao.get(tmp.getTableid());
		
		if (null == tmp || null == table) {
			return 0;
		}
		//如果是修改的系统字段，则更新实体表，帐户字段忽略
		if (tmp.getAccountid().equals("SYSTEM")) {
			//如果字段大小有了变化，更新实体表
			if (tmp.getFieldsize() != templetfield.getFieldsize()) {
//				alter table sys_account modify ACCOUNTCODE varchar(50) ;
				StringBuffer sb = new StringBuffer();
				sb.append("alter table ");
				sb.append(table.getTablename());
				sb.append(" modify ");
				sb.append(tmp.getEnglishname());
				sb.append(" ").append(tmp.getFieldtype());
				sb.append("(").append(templetfield.getFieldsize()).append(");");
				tableDao.add(sb.toString(), null);
			}
			
			//如果默认值有了变化，更新实体表
			Boolean defaultvalueBoolean = false;
			if (!tmp.getDefaultvalue().equals(templetfield.getDefaultvalue())) {
				defaultvalueBoolean = true;
			}

			if (defaultvalueBoolean) {
//				alter table tablename alter column drop default; (若本身存在默认值，则先删除)
//				alter table tablename alter column set default 't5';(若本身不存在则可以直接设定)
				StringBuffer sb = new StringBuffer();
				sb.append("alter table ");
				sb.append(table.getTablename());
				sb.append(" alter ");
				sb.append(tmp.getEnglishname());
				sb.append(" set default ");
				if (tmp.getFieldtype().equals("VARCHAR")) {
					sb.append("'").append(templetfield.getDefaultvalue()).append("'");
				}
				else if (tmp.getFieldtype().equals("DATE")) {
					sb.append("'").append(templetfield.getDefaultvalue()).append("'");
				}
				else if (tmp.getFieldtype().equals("INT")) {
					sb.append(templetfield.getDefaultvalue());
				}
				sb.append(";");
				tableDao.add(sb.toString(), null);
			}
			
			//要更新帐户字段,帐户字段仅更新帐户中文名、长度、默认值。 代码项在代码功能里修改。
			//其他字段：检索、列表、数据排序、字段排序已交由帐户自己管理
			
			//获取除accountid＝“SYSTEM”（系统字段）外的当前表对应的accountid
			String sql = "select accountid,count(*) as count from sys_templetfield where tableid=? and accountid <> 'SYSTEM' group by accountid having count>0";
			List<Object> values = new ArrayList<Object>();
			values.add(table.getId());
			List<Map<String, Object>> accountids = templetfieldDao.searchForMap(sql, values);
			//为每个帐户修改新字段
			for (Map<String, Object> map : accountids) {
				//获取帐户对应修改字段的对象
				Sys_templetfield accountfield = new Sys_templetfield();
				accountfield.setAccountid(map.get("accountid").toString());
				accountfield.setTableid(table.getId());
				accountfield.setEnglishname(tmp.getEnglishname());
				accountfield = templetfieldDao.searchOne(accountfield);
				
				Sys_templetfield field = new Sys_templetfield();
				field.setId(accountfield.getId());
				field.setChinesename(templetfield.getChinesename());
				field.setFieldsize(templetfield.getFieldsize());
				field.setDefaultvalue(templetfield.getDefaultvalue());
				templetfieldDao.update(field);
			}
		}
		
		Sys_templetfield result = templetfieldDao.update(templetfield);
		
		if (null != result) {
			return 1;
		}
		return 0;
	}

	@Transactional("txManager")
	@Override
	public int delete(String id) {
		//获取字段对象
		Sys_templetfield templetfield = templetfieldDao.get(id);
		if (null == templetfield) {
			return 0;
		}
		//获取table对象
		Sys_table table = tableDao.get(templetfield.getTableid());
		
		if (null == table) {
			return 0;
		}
		//删除实体表字段
//		alter table sys_doc drop column PARENTID;
		StringBuffer sb = new StringBuffer();
		sb.append("alter table ");
		sb.append(table.getTablename());
//		sb.append("?");
		sb.append(" drop column ");
		sb.append(templetfield.getEnglishname());
//		sb.append("?");
		sb.append(";");
		
		List<Object> values = new ArrayList<Object>();
		tableDao.del(sb.toString(), values);
		
		//删除帐户字段
		String sql = "select accountid,count(*) as count from sys_templetfield where tableid=? and accountid <> 'SYSTEM' group by accountid having count>0";
		values.clear();
		values.add(table.getId());
		List<Map<String, Object>> accountids = templetfieldDao.searchForMap(sql, values);
		//为每个帐户删除字段
		for (Map<String, Object> map : accountids) {
			//获取要删除的帐户对应字段的对象
			Sys_templetfield accountfield = new Sys_templetfield();
			accountfield.setAccountid(map.get("accountid").toString());
			accountfield.setTableid(table.getId());
			accountfield.setEnglishname(templetfield.getEnglishname());
			accountfield = templetfieldDao.searchOne(accountfield);
			
			//判断要删除的字段是否有代码
			if (accountfield.getIscode() == 1) {
				sql = "delete from sys_code where templetfieldid=?";
				values.clear();
				values.add(accountfield.getId());
				codeDao.del(sql, values);
			}
			
			templetfieldDao.del(accountfield.getId());
		}
		
		//判断要删除的字段是否有代码
		if (templetfield.getIscode() == 1) {
			sql = "delete from sys_code where templetfieldid=?";
			values.clear();
			values.add(templetfield.getId());
			codeDao.del(sql, values);
		}
		
		int num = templetfieldDao.del(id);
		
		return num;
	}

	@Transactional("txManager")
	@Override
	public Boolean sort(String id, String type) {
		if (null == id || id.equals("")) {
			return false;
		}
		if (null == type || type.equals("")) {
			return false;
		}
		
		//获取字段本身对象
		Sys_templetfield templetfield = templetfieldDao.get(id);
		
		if (null == templetfield) {
			return false;
		}
		
		//获取该字段临近的字段,要区分向上还是向下
		//向下sql
//		select * from sys_templetfield where tableid='1' and sort > 2 order by sort asc limit 1
		//向上sql
//		select * from sys_templetfield where tableid='1' and sort < 2 order by sort desc limit 1
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sys_templetfield where ");
		sb.append("tableid=? and accountid=?");
		if (type.equals("up")) {
			sb.append(" and sort < ? and sort > 0 ").append("order by sort desc limit 1");
		}
		else if (type.equals("down")) {
			sb.append(" and sort > ? and sort > 0 ").append("order by sort asc limit 1");
		}
		
		List<Object> values = new ArrayList<Object>();
		values.add(templetfield.getTableid());
		values.add(templetfield.getAccountid());
		values.add(templetfield.getSort());
		
		List<Sys_templetfield> tmpList = templetfieldDao.search(sb.toString(), values);
		
		if (null == tmpList || tmpList.size()<=0 ) {
			return false;
		}
		Sys_templetfield tmp = tmpList.get(0);
		
		//执行update  sort  交换sort值
		Sys_templetfield result = new Sys_templetfield();
		//执行2个sort更新
		result.setId(id);
		result.setSort(tmp.getSort());
		templetfieldDao.update(result);
		
		result.setId(tmp.getId());
		result.setSort(templetfield.getSort());
		templetfieldDao.update(result);
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public Boolean updateOtherInfo(String id, String type, Integer value) {
		//获取对象
		if (null == id || id.equals("") || null == type || type.equals("")) {
			return false;
		}
		
		Sys_templetfield templetfield = templetfieldDao.get(id);
		
		if (null == templetfield) {
			return false;
		}
		
		if (null == value || value < 0 ) {
			value = 0;
		}
		
		Sys_templetfield tmp = new Sys_templetfield();
		tmp.setId(templetfield.getId());
		if (type.equals("issearch")) {
			tmp.setIssearch(value);
		}
		else if (type.equals("isgridshow")) {
			tmp.setIsgridshow(value);
		}
		else if (type.equals("isedit")) {
			tmp.setIsedit(value);
		}
		else if (type.equals("iscopy")) {
			tmp.setIscopy(value);
		}
		
		tmp = templetfieldDao.update(tmp);
		
		if (null == tmp) {
			return false;
		}
		
		return true;
		
	}
	@Transactional("txManager")
	@Override
	public Boolean fieldpaste(String fieldid, String tableid) {
		
		String[] idsStrings = fieldid.split(",");
		
		for (String id : idsStrings) {
			//获取copy字段实体
			Sys_templetfield field = templetfieldDao.get(id);
			if (null == field) {
				continue;
			}
			
			//判断字段英文名是否存在
			String sql = "select * from  sys_templetfield where englishname=? and tableid=? and accountid=?";
			List<Object> values = new ArrayList<Object>();
			values.add(field.getEnglishname());
			values.add(tableid);
			values.add("SYSTEM");
			int num = templetfieldDao.getCount(sql, values);
			
			if (num >0) {
				continue;
			}
			
			//更换id和tableid，插入数据库新的字段
			Sys_templetfield targetField = field;
			targetField.setId(UUID.randomUUID().toString());
			targetField.setTableid(tableid);
			//保存新字段
			Boolean b = saveField(targetField);
			List<Sys_code> codes = new ArrayList<Sys_code>();
			//插入新字段后，处理字段代码
			if (field.getIscode() == 1) {
				//获取原字段的代码
				values.clear();
				values.add(id);
				sql = "select * from sys_code where templetfieldid=? order by codeorder asc";
				codes = codeDao.search(sql, values);
				if (null != codes && codes.size() > 0) {
					for (Sys_code code : codes) {
						code.setId(UUID.randomUUID().toString());
						code.setTempletfieldid(targetField.getId());
						codeDao.save(code);
					}
				}
			}
					
			
			//成功插入系统字段后，处理帐户字段
			Sys_table table = tableDao.get(tableid);
			//获取多少个帐户对当前表字段有帐户字段
			sql = "select accountid,count(*) as count from sys_templetfield where tableid=? and accountid <> 'SYSTEM' group by accountid having count>0";
			values.clear();
			values.add(table.getId());
			List<Map<String, Object>> templetfields = templetfieldDao.searchForMap(sql, values);
			//为每个帐户插入新字段
			for (Map<String, Object> map : templetfields) {
				targetField.setId(UUID.randomUUID().toString());
				targetField.setAccountid(map.get("accountid").toString());
				
				if (null != codes && codes.size() > 0) {
					for (Sys_code code : codes) {
						code.setId(UUID.randomUUID().toString());
						code.setTempletfieldid(targetField.getId());
						codeDao.save(code);
					}
				}
				
				templetfieldDao.save(targetField);
			}
		}
		
		return true;
	}
	

	@Transactional("txManager")
	@Override
	public Boolean insertFieldCode(String id, String columndata) {
		
		Sys_code code = new Sys_code();
		code.setId(UUID.randomUUID().toString());
		code.setColumndata(columndata);
		code.setColumnname(columndata);
		code.setTempletfieldid(id);
		
		//获取排序最大值
		//查sort最大值＋1赋予新字段sort
		String sql = "select max(codeorder) from sys_code where templetfieldid=?";
		List<Object> values = new ArrayList<Object>();
		values.add(id);
		Long lo = codeDao.getLong(sql, values);
		
		if (lo > 0) {
			code.setCodeorder(lo.intValue()+1);
		}
		else {
			code.setCodeorder(1);
		}
		
		code = codeDao.save(code);
		
		if (null == code) {
			return false;
		}
		
		//判断field的iscode是否为1，不为1，修改为1
		Sys_templetfield templetfield = templetfieldDao.get(id);
		
		if (null == templetfield) {
			return false;
		}
		
		if (templetfield.getIscode() == 0) {
			Sys_templetfield tmp = new Sys_templetfield();
			tmp.setId(templetfield.getId());
			tmp.setIscode(1);
			templetfieldDao.update(tmp);
		}
		
		return true;
		
	}

	@Transactional("txManager")
	@Override
	public Boolean delCode(String id) {
		//获取code对象
		Sys_code code = codeDao.get(id);
		
		//删除code
		codeDao.del(id);
		
		//获取sys_code
		String sql = "select * from sys_code where templetfieldid=?";
		List<Object> values = new ArrayList<Object>();
		values.add(code.getTempletfieldid());
		List<Sys_code> codes = codeDao.search(sql, values);
		
		if (null == codes || codes.size() == 0) {
			//获取字段对象
			Sys_templetfield templetfield = templetfieldDao.get(code.getTempletfieldid());
			
			if (null == templetfield) {
				return false;
			}
			//如果该字段下没有代码了，更新字段的iscode为0
			Sys_templetfield tmp = new Sys_templetfield();
			tmp.setId(templetfield.getId());
			tmp.setIscode(0);
			templetfieldDao.update(tmp);
		}
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public Boolean sortcode(String id, String type) {
		if (null == id || id.equals("")) {
			return false;
		}
		if (null == type || type.equals("")) {
			return false;
		}
		
		//获取字段本身对象
		Sys_code code = codeDao.get(id);
		
		if (null == code) {
			return false;
		}
		
		//获取该字段临近的字段,要区分向上还是向下
		//向下sql
//		select * from sys_templetfield where tableid='1' and sort > 2 order by sort asc limit 1
		//向上sql
//		select * from sys_templetfield where tableid='1' and sort < 2 order by sort desc limit 1
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sys_code where ");
		sb.append("templetfieldid=?");
		if (type.equals("up")) {
			sb.append(" and codeorder < ? ").append("order by codeorder desc limit 1");
		}
		else if (type.equals("down")) {
			sb.append(" and codeorder > ? ").append("order by codeorder asc limit 1");
		}
		
		List<Object> values = new ArrayList<Object>();
		values.add(code.getTempletfieldid());
		values.add(code.getCodeorder());
		
		List<Sys_code> tmpList = codeDao.search(sb.toString(), values);
		
		if (null == tmpList || tmpList.size()<=0 ) {
			return false;
		}
		Sys_code tmp = tmpList.get(0);
		
		
		//执行update  sort  交换sort值
		Sys_code result = new Sys_code();
		//执行2个sort更新
		result.setId(id);
		result.setCodeorder(tmp.getCodeorder());
		codeDao.update(result);
		
		result.setId(tmp.getId());
		result.setCodeorder(code.getCodeorder());
		codeDao.update(result);
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public Boolean pastecode(String fieldid, String targetid) {
		//获取sys_code
		List<Object> values = new ArrayList<Object>();
		values.add(fieldid);
		String sql = "select * from sys_code where templetfieldid=? order by codeorder asc";
		
		List<Sys_code> codes = codeDao.search(sql, values);
		
		if (null == codes || codes.size() == 0) {
			return false;
		}
		
		for (Sys_code code : codes) {
			code.setId(UUID.randomUUID().toString());
			code.setTempletfieldid(targetid);
			codeDao.save(code);
		}
		
		//获取字段对象
		Sys_templetfield templetfield = templetfieldDao.get(targetid);
		
		if (null == templetfield) {
			return false;
		}
		//如果该字段下没有代码了，更新字段的iscode为0
		Sys_templetfield tmp = new Sys_templetfield();
		tmp.setId(templetfield.getId());
		tmp.setIscode(1);
		templetfieldDao.update(tmp);
		
		return true;
	}

	@Transactional("txManager")
	@Override
	public List<Sys_templetfield> getAccountTempletfields(String templetid,String tabletype,String accountid) {
		
		//根据templetid 和 tabletype 来或者tableid
		Sys_table table = new Sys_table();
		table.setTempletid(templetid);
		table.setTabletype(tabletype);
		table = tableDao.searchOne(table);
		
		if (null == table) {
			return null;
		}
		
		//根据tableid，获取字段 templetfield的list
		String sql = "select * from sys_templetfield where tableid=? and accountid=? and sort <> -1 order by sort";
		List<Object> values = new ArrayList<Object>();
		values.add(table.getId());
		values.add(accountid);
		List<Sys_templetfield> templetfields = templetfieldDao.search(sql, values);
		
		//如果没有设置，就参照系统的，添加到帐户本身
		if (null == templetfields || templetfields.size() == 0) {
			sql= "select * from sys_templetfield where tableid=? and accountid=? order by sort";
			values.clear();
			values.add(table.getId());
			values.add("SYSTEM");
			templetfields = templetfieldDao.search(sql, values);
			
			//添加帐户自己的字段设置
			if (null != templetfields && templetfields.size() > 0) {
				for (Sys_templetfield templetfield : templetfields) {
					String uuid = UUID.randomUUID().toString();
					String sysFieldid = templetfield.getId();
					
					templetfield.setAccountid(accountid);
					templetfield.setId(uuid);
					
					//判断字段是否有代码
					if (templetfield.getIscode() == 1) {
						//获取代码list，并赋予帐户字段相同的代码
						sql = "select * from sys_code where templetfieldid=?";
						values.clear();
						values.add(sysFieldid);
						List<Sys_code> codes = codeDao.search(sql, values);
						
						//将系统字段的代码，copy一份给帐户字段
						for (Sys_code code : codes) {
							code.setId(UUID.randomUUID().toString());
							code.setTempletfieldid(uuid);
							codeDao.save(code);
						}
					}
					
					templetfieldDao.save(templetfield);
				}
			}
			
			templetfields.clear();
			//插入完毕后，再次读取
			sql = "select * from sys_templetfield where tableid=? and accountid=? and sort <> -1 order by sort";
			values.clear();
			values.add(table.getId());
			values.add(accountid);
			templetfields = templetfieldDao.search(sql, values);
		}
		return templetfields;
	}

}
