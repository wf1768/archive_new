package net.ussoft.archive.service.impl;

import java.util.ArrayList;
import java.util.List;
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
		List<Sys_templetfield> fieldList = templetfieldDao.search("select * from sys_templetfield where tableid=? and sort != -1", values);
		
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
		tableDao.add(sb.toString(), null);
		
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
		String sql = "select * from  sys_templetfield where englishname=? and tableid=?";
		List<Object> values = new ArrayList<Object>();
		values.add(templetfield.getEnglishname());
		values.add(templetfield.getTableid());
		int num = templetfieldDao.getCount(sql, values);
		
		if (num >0) {
			return "repeat";
		}
		
		//首先插入真实表
		if (null == templetfield.getTableid() || templetfield.getTableid().equals("")) {
			return "failure";
		}
		
		Boolean b = saveField(templetfield);
		
		if (!b) {
			return "failure";
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
		//如果字段大小有了变化，更新实体表
		if (tmp.getFieldsize() != templetfield.getFieldsize()) {
//			alter table sys_account modify ACCOUNTCODE varchar(50) ;
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
		if (tmp.getFieldtype().equals("VARCHAR")) {
			if (!tmp.getDefaultvalue().equals(templetfield.getDefaultvalue())) {
				defaultvalueBoolean = true;
			}
		}
		else if (tmp.getFieldtype().equals("INT")) {
			if (tmp.getDefaultvalue() != templetfield.getDefaultvalue()) {
				defaultvalueBoolean = true;
			}
		}
		else if (tmp.getFieldtype().equals("DATE")) {
			if (!tmp.getDefaultvalue().equals(templetfield.getDefaultvalue())) {
				defaultvalueBoolean = true;
			}
		}
		if (defaultvalueBoolean) {
//			alter table tablename alter column drop default; (若本身存在默认值，则先删除)
//			alter table tablename alter column set default 't5';(若本身不存在则可以直接设定)
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
				sb.append(templetfield.getDefaultvalue());
			}
			else if (tmp.getFieldtype().equals("INT")) {
				sb.append(templetfield.getDefaultvalue());
			}
			sb.append(";");
			tableDao.add(sb.toString(), null);
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
		
		//判断要删除的字段是否有代码
		if (templetfield.getIscode() == 1) {
			String sql = "delete from sys_code where templetfieldid=?";
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
		sb.append("tableid=?");
		if (type.equals("up")) {
			sb.append(" and sort < ? ").append("order by sort desc limit 1");
		}
		else if (type.equals("down")) {
			sb.append(" and sort > ? ").append("order by sort asc limit 1");
		}
		
		List<Object> values = new ArrayList<Object>();
		values.add(templetfield.getTableid());
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
		
		//获取copy字段实体
		Sys_templetfield field = templetfieldDao.get(fieldid);
		if (null == field) {
			return false;
		}
		
		//判断字段英文名是否存在
		String sql = "select * from  sys_templetfield where englishname=? and tableid=?";
		List<Object> values = new ArrayList<Object>();
		values.add(field.getEnglishname());
		values.add(tableid);
		int num = templetfieldDao.getCount(sql, values);
		
		if (num >0) {
			return false;
		}
		
		//更换id和tableid，插入数据库新的字段
		Sys_templetfield targetField = field;
		targetField.setId(UUID.randomUUID().toString());
		targetField.setTableid(tableid);
		//保存新字段
		Boolean b = saveField(targetField);
		
		if (!b) {
			return false;
		}
		
		//插入新字段后，处理字段代码
		if (field.getIscode() == 1) {
			//获取原字段的代码
			values.clear();
			values.add(fieldid);
			sql = "select * from sys_code where templetfieldid=? order by codeorder asc";
			List<Sys_code> codes = codeDao.search(sql, values);
			if (null != codes && codes.size() > 0) {
				for (Sys_code code : codes) {
					code.setId(UUID.randomUUID().toString());
					code.setTempletfieldid(targetField.getId());
					codeDao.save(code);
				}
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

}
