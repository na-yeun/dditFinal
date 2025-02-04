package kr.or.ddit.resource.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import kr.or.ddit.security.authorize.ReloadableAuthorizationManager;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SecuredResourceReloadController {
	
	private final ReloadableAuthorizationManager reloadableManager;

	@RequestMapping("/resource/reload")
	@ResponseBody
	public String reload() {
		reloadableManager.reload();
		return "reload";
	}
}