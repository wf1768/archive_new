package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_templetfield")
public class Sys_templetfield {

	private String id;
	private String englishname;
	private String chinesename;
	private Integer fieldsize;
	private String fieldtype;
	private Integer ispk;
	private String defaultvalue;
	private Integer isrequire;
	private Integer isunique;
	private Integer issearch;
	private Integer isgridshow;
	private Integer sort;
	private Integer isedit;
	private Integer iscode;
	private Integer issystem;
	private String fieldcss;
	private String tableid;
	private String orderby;
	private Integer iscopy;
	private String accountid;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getEnglishname() {
		return englishname;
	}
	public void setEnglishname(String englishname) {
		this.englishname = englishname;
	}
	public String getChinesename() {
		return chinesename;
	}
	public void setChinesename(String chinesename) {
		this.chinesename = chinesename;
	}
	public Integer getFieldsize() {
		return fieldsize;
	}
	public void setFieldsize(Integer fieldsize) {
		this.fieldsize = fieldsize;
	}
	public String getFieldtype() {
		return fieldtype;
	}
	public void setFieldtype(String fieldtype) {
		this.fieldtype = fieldtype;
	}
	public Integer getIspk() {
		return ispk;
	}
	public void setIspk(Integer ispk) {
		this.ispk = ispk;
	}
	public String getDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	public Integer getIsrequire() {
		return isrequire;
	}
	public void setIsrequire(Integer isrequire) {
		this.isrequire = isrequire;
	}
	public Integer getIsunique() {
		return isunique;
	}
	public void setIsunique(Integer isunique) {
		this.isunique = isunique;
	}
	public Integer getIssearch() {
		return issearch;
	}
	public void setIssearch(Integer issearch) {
		this.issearch = issearch;
	}
	public Integer getIsgridshow() {
		return isgridshow;
	}
	public void setIsgridshow(Integer isgridshow) {
		this.isgridshow = isgridshow;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getIsedit() {
		return isedit;
	}
	public void setIsedit(Integer isedit) {
		this.isedit = isedit;
	}
	public Integer getIscode() {
		return iscode;
	}
	public void setIscode(Integer iscode) {
		this.iscode = iscode;
	}
	public Integer getIssystem() {
		return issystem;
	}
	public void setIssystem(Integer issystem) {
		this.issystem = issystem;
	}
	public String getFieldcss() {
		return fieldcss;
	}
	public void setFieldcss(String fieldcss) {
		this.fieldcss = fieldcss;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public Integer getIscopy() {
		return iscopy;
	}
	public void setIscopy(Integer iscopy) {
		this.iscopy = iscopy;
	}
	public String getAccountid() {
		return accountid;
	}
	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}
	
	
}
