package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_init")
public class Sys_init {

	private String id;
	private String initkey;
	private String initkeymemo;
	private String initvalue;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInitkey() {
		return initkey;
	}
	public void setInitkey(String initkey) {
		this.initkey = initkey;
	}
	public String getInitkeymemo() {
		return initkeymemo;
	}
	public void setInitkeymemo(String initkeymemo) {
		this.initkeymemo = initkeymemo;
	}
	public String getInitvalue() {
		return initvalue;
	}
	public void setInitvalue(String initvalue) {
		this.initvalue = initvalue;
	}
	
	
	
}
