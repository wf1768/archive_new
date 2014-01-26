package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_org")
public class Sys_org {
	
	private String id;
	private String parentid;
	private Integer orgindex;
	private String treenode;
	private String orgname;
	private Integer orgorder;
	private String roleid;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrgname() {
		return orgname;
	}
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public Integer getOrgorder() {
		return orgorder;
	}
	public void setOrgorder(Integer orgorder) {
		this.orgorder = orgorder;
	}
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public Integer getOrgindex() {
		return orgindex;
	}
	public void setOrgindex(Integer orgindex) {
		this.orgindex = orgindex;
	}
	public String getTreenode() {
		return treenode;
	}
	public void setTreenode(String treenode) {
		this.treenode = treenode;
	}
	

}
