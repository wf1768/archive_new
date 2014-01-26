package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_errorlog")
public class Sys_errorlog {

	private String id;
	private String funname;
	private String errortime;
	private String errordoc;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFunname() {
		return funname;
	}
	public void setFunname(String funname) {
		this.funname = funname;
	}
	public String getErrortime() {
		return errortime;
	}
	public void setErrortime(String errortime) {
		this.errortime = errortime;
	}
	public String getErrordoc() {
		return errordoc;
	}
	public void setErrordoc(String errordoc) {
		this.errordoc = errordoc;
	}
	
}
