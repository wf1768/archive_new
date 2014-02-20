package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_org_tree")
public class Sys_org_tree {

	
	private String id;
	private String orgid;
	private String treeid;
	private Integer filescan;
	private Integer filedown;
	private Integer fileprint;
	private String filter;
	private String docauth;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getTreeid() {
		return treeid;
	}
	public void setTreeid(String treeid) {
		this.treeid = treeid;
	}
	public Integer getFilescan() {
		return filescan;
	}
	public void setFilescan(Integer filescan) {
		this.filescan = filescan;
	}
	public Integer getFiledown() {
		return filedown;
	}
	public void setFiledown(Integer filedown) {
		this.filedown = filedown;
	}
	public Integer getFileprint() {
		return fileprint;
	}
	public void setFileprint(Integer fileprint) {
		this.fileprint = fileprint;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getDocauth() {
		return docauth;
	}
	public void setDocauth(String docauth) {
		this.docauth = docauth;
	}
	
	
}
