package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_doc")
public class Sys_doc {

	private String id;
	private String docserverid;
	private String docoldname;
	private String docnewname;
	private String doctype;
	private String docext;
	private String doclength;
	private String docpath;
	private String creater;
	private String createtime;
	private String fileid;
	private String tableid;
	private String treeid;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDocserverid() {
		return docserverid;
	}
	public void setDocserverid(String docserverid) {
		this.docserverid = docserverid;
	}
	public String getDocoldname() {
		return docoldname;
	}
	public void setDocoldname(String docoldname) {
		this.docoldname = docoldname;
	}
	public String getDocnewname() {
		return docnewname;
	}
	public void setDocnewname(String docnewname) {
		this.docnewname = docnewname;
	}
	public String getDoctype() {
		return doctype;
	}
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}
	public String getDocext() {
		return docext;
	}
	public void setDocext(String docext) {
		this.docext = docext;
	}
	public String getDoclength() {
		return doclength;
	}
	public void setDoclength(String doclength) {
		this.doclength = doclength;
	}
	public String getDocpath() {
		return docpath;
	}
	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getTreeid() {
		return treeid;
	}
	public void setTreeid(String treeid) {
		this.treeid = treeid;
	}
	
	
}
