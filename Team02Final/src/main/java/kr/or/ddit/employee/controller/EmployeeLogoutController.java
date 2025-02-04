package kr.or.ddit.employee.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.ddit.employee.vo.EmployeeVO;

@Controller
@RequestMapping("/{companyId}/logout")
public class EmployeeLogoutController {
	
	@GetMapping
	public String logout(
			HttpSession session
			, @PathVariable("companyId") String companyId
	) {
		// 나의 퇴근 상태 조회해서 if문 돌리기
		
		session.invalidate();
		
		String url = String.format("redirect:/%s/login", companyId);
		return url;
	}

}
