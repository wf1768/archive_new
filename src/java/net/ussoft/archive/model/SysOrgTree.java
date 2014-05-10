package net.ussoft.archive.model;

public class SysOrgTree {
    
    private String orgTreeId;
    private String orgid;
    private String treeid;
    private Integer filescan;
    private Integer filedown;
    private Integer fileprint;
   
    private String filter;
    
    
    
    
    
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getOrgTreeId() {
        return orgTreeId;
    }
    public void setOrgTreeId(String orgTreeId) {
        this.orgTreeId = orgTreeId;
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
}