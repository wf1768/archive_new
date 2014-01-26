package net.ussoft.archive.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="ar_release")
public class Ar_release {

	private String id;
	private String fileid;
	private String tableid;
	private String releaseman;
	private String releasdate;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getReleaseman() {
		return releaseman;
	}
	public void setReleaseman(String releaseman) {
		this.releaseman = releaseman;
	}
	public String getReleasdate() {
		return releasdate;
	}
	public void setReleasdate(String releasdate) {
		this.releasdate = releasdate;
	}
	
	
}
