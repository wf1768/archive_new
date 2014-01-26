package net.ussoft.archive.util.resule;

import java.util.HashMap;
import java.util.Map;

public class ResultInfo {

	private Boolean success = Boolean.FALSE;

	private String msg;

	private Map<String, Object> data = new HashMap<String, Object>();
	
	public ResultInfo() {
		super();
	}

	public ResultInfo(Boolean success) {
		super();
		this.success = success;
	}

	public ResultInfo(Boolean success, String msg) {
		super();
		this.success = success;
		this.msg = msg;
	}
	
	public ResultInfo(Boolean success, String msg, Map<String, Object> data) {
		super();
		this.success = success;
		this.msg = msg;
		this.data = data;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public void put(String key,Object obj){
		this.data.put(key, obj);
	}

}
