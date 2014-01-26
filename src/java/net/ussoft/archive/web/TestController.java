package net.ussoft.archive.web;

import net.ussoft.archive.util.resule.ResultInfo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;



@Controller
@RequestMapping(value="/user")
public class TestController {

//	private Logger log = new Logger(TestController.class);
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody
	String add(String username) {
		ResultInfo info = new ResultInfo();
		info.setSuccess(true);
		info.setMsg(username);
		String result = JSONObject.toJSON(info).toString();
		return result;
	}
	
	@RequestMapping(value = "/edit")
	public @ResponseBody
	String edit() {
		return "jalk";
	}
}
