package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_function")
public class Sys_function {

	private String id;
	private String funchinesename;
	private String funenglishname;
	private String funpath;
	private Integer funorder;
	private Integer funsystem;
	private String funparent;
	private String funicon;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFunchinesename() {
		return funchinesename;
	}
	public void setFunchinesename(String funchinesename) {
		this.funchinesename = funchinesename;
	}
	public String getFunenglishname() {
		return funenglishname;
	}
	public void setFunenglishname(String funenglishname) {
		this.funenglishname = funenglishname;
	}
	public String getFunpath() {
		return funpath;
	}
	public void setFunpath(String funpath) {
		this.funpath = funpath;
	}
	public Integer getFunorder() {
		return funorder;
	}
	public void setFunorder(Integer funorder) {
		this.funorder = funorder;
	}
	public Integer getFunsystem() {
		return funsystem;
	}
	public void setFunsystem(Integer funsystem) {
		this.funsystem = funsystem;
	}
	public String getFunparent() {
		return funparent;
	}
	public void setFunparent(String funparent) {
		this.funparent = funparent;
	}
	public String getFunicon() {
		return funicon;
	}
	public void setFunicon(String funicon) {
		this.funicon = funicon;
	}
	
}
