package kr.or.ddit.provider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/helloTest")
public class PathTestController {

	@GetMapping
	public String get() {
		
		return "provider/provTest/test.jsp";
	}
	
}
