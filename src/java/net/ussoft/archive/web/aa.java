package net.ussoft.archive.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class aa {

	public static void main(String[] args) {
		
		List list = new ArrayList();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", "1");
		map.put("name", "aaa");
		list.add(map);
		
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("id", "2");
		map1.put("name", "222");
		list.add(map1);
		
		JSONArray bb = JSON.parseArray(JSON.toJSONString(list));
		System.out.println(((JSONObject) bb.get(0)).get("name"));
		((JSONObject) bb.get(0)).put("isParent", true);
		String cc = JSON.toJSONString(bb);
		System.out.println(cc);
		

	}

}
