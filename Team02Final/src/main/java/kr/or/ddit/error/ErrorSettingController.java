package kr.or.ddit.error;

import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ErrorSettingController {

	private static Logger LOGGER = LoggerFactory.getLogger(ErrorSettingController.class);
	
	
	@GetMapping("/error404")
	public String error404(Model model) {
		model.addAttribute("code","ERROR_404");
		return "/error/404page";
	}
	
	@GetMapping("/error500")
	public String error500(Model model) {
		model.addAttribute("code","ERROR_500");
		return "/error/500page";
	}
	
	@GetMapping("/error400")
	public String error400(Model model) {
		model.addAttribute("code","ERROR_400");
		return "/error/400page";
	}
	
	@GetMapping("/error403")
	public String error403(Model model) {
		model.addAttribute("code","ERROR_403");
		return "/error/403page";
	}
	
	@GetMapping("/error405")
	public String error405(Model model) {
		model.addAttribute("code","ERROR_405");
		return "/error/405page";
	}
	
	@GetMapping("/error415")
	public String error415(Model model) {
		model.addAttribute("code","ERROR_415");
		return "/error/415page";
	}
	
}
