package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="sys_docserver")
public class Sys_docserver {

	private String id;
	private String serverip;
	private String serverpath;
	private String ftpuser;
	private String ftppassword;
	private String servername;
	private Integer serverstate;
	private String servermemo;
	private String servertype;
	private Integer serverport;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getServerpath() {
		return serverpath;
	}
	public void setServerpath(String serverpath) {
		this.serverpath = serverpath;
	}
	public String getServertype() {
		return servertype;
	}
	public void setServertype(String servertype) {
		this.servertype = servertype;
	}
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getFtpuser() {
		return ftpuser;
	}
	public void setFtpuser(String ftpuser) {
		this.ftpuser = ftpuser;
	}
	public String getFtppassword() {
		return ftppassword;
	}
	public void setFtppassword(String ftppassword) {
		this.ftppassword = ftppassword;
	}
	public String getServername() {
		return servername;
	}
	public void setServername(String servername) {
		this.servername = servername;
	}
	public String getServermemo() {
		return servermemo;
	}
	public void setServermemo(String servermemo) {
		this.servermemo = servermemo;
	}
	public Integer getServerstate() {
		return serverstate;
	}
	public void setServerstate(Integer serverstate) {
		this.serverstate = serverstate;
	}
	public Integer getServerport() {
		return serverport;
	}
	public void setServerport(Integer serverport) {
		this.serverport = serverport;
	}
	
}
