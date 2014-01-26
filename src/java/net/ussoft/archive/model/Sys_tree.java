package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_tree")
public class Sys_tree {
	
	private String id;
	private String parentid;
	private String treename;
	private String treetype;
	private String treenode;
	private String templetid;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTempletid() {
		return templetid;
	}
	public void setTempletid(String templetid) {
		this.templetid = templetid;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getTreename() {
		return treename;
	}
	public void setTreename(String treename) {
		this.treename = treename;
	}
	public String getTreetype() {
		return treetype;
	}
	public void setTreetype(String treetype) {
		this.treetype = treetype;
	}
	public String getTreenode() {
		return treenode;
	}
	public void setTreenode(String treenode) {
		this.treenode = treenode;
	}

	
}
