package kr.or.ddit.provider.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/provider/logout")
public class ProviderLogoutController {
	
	@GetMapping
	public String logout(
			HttpSession session
	) {
		// 나의 퇴근 상태 조회해서 if문 돌리기
		
		session.invalidate();
		String companyId = "a001";
		String url = String.format("redirect:/%s/login", companyId);
		return url;
	}
	
}
