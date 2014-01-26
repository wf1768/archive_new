package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_code")
public class Sys_code {

	private String id;
	private String templetfieldid;
	private String columnname;
	private String columndata;
	private Integer codeorder;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTempletfieldid() {
		return templetfieldid;
	}
	public void setTempletfieldid(String templetfieldid) {
		this.templetfieldid = templetfieldid;
	}
	public String getColumnname() {
		return columnname;
	}
	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}
	public String getColumndata() {
		return columndata;
	}
	public void setColumndata(String columndata) {
		this.columndata = columndata;
	}
	public Integer getCodeorder() {
		return codeorder;
	}
	public void setCodeorder(Integer codeorder) {
		this.codeorder = codeorder;
	}
	
	
	
}
