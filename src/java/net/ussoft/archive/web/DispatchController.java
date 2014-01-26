package net.ussoft.archive.web;

import net.ussoft.archive.util.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DispatchController {
	
	private Logger log = new Logger(DispatchController.class);
	
	@RequestMapping(value="dispatch",method=RequestMethod.GET)
	public String dispatch(String page) {
		return page;
	}

}
