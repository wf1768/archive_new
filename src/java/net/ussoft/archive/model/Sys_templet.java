package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_templet")
public class Sys_templet {

	private String id;
	private String templetname;
	private String templettype;
	private String orgid;
	private Integer sort;
	private String parentid;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTempletname() {
		return templetname;
	}
	public void setTempletname(String templetname) {
		this.templetname = templetname;
	}
	public String getTemplettype() {
		return templettype;
	}
	public void setTemplettype(String templettype) {
		this.templettype = templettype;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
}
