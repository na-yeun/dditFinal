package kr.or.ddit.employee.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.socket.WebSocketSession;

import kr.or.ddit.account.service.AccountService;
import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.exception.PKNotFoundException;
import kr.or.ddit.commons.validate.LoginGroup;
import kr.or.ddit.commons.websocket.EchoHandler;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.event.loginGmailEvent.LoginSuccessEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}")
public class EmployeeLoginController {


	/**
	 * login 페이지로 이동하는 handler method
	 */
	@GetMapping("login")
	public String getLoginForm() {
		return "/employee/employeeLoginForm";
	}

	/**
	 * 사용자가 login 정보를 입력하여 전송하면 해당 handler method
	 */
	/*
	@PostMapping("loginprocess")
	public String loginResult(
			@PathVariable("companyId") String companyId,
			@Validated(LoginGroup.class) AccountVO account,
			BindingResult errors,
			RedirectAttributes redirectAttributes,
			HttpSession session) {
		session.setAttribute("companyId", companyId);
		String urlRegex = "redirect:/%s/%s";
		// 입력해주는 데이터는 account임
		// account로 select 해서 정보가 있으면 로그인 성공
		// 없으면 로그인 실패
		// --> 로그인 실패했을 때 입력한 email을 카운트 해서 3회 이상일 경우
		// --> 해당 email을 가진 계정의 상태를 update
		if (!errors.hasErrors()) {
			// 검증 성공
			try {
				// account에 조회된 결과가 있을 때
				String myMail = String.format("%s@gmail.com", account.getAccountMail());
				account.setAccountMail(myMail);
				AccountVO result = accountService.readAccountExist(account);
				// 첫 로그인일 경우 회원가입 유도
				if (result.getAccountYn().equals("Y")) {
					redirectAttributes.addFlashAttribute("message", "사원인증을 먼저 진행해주세요.");
					redirectAttributes.addFlashAttribute("messageKind", "error");
					return String.format(urlRegex, companyId, "login");
				}
				EmployeeVO myEmp = employeeService.readEmployee(result.getAccountMail());
				if (myEmp.getEmpStatus().equals("U") || myEmp.getEmpStatus().equals("V") || myEmp.getEmpStatus().equals("R")) {
					// 상태(U : 재직중 / V : 휴가) 로그인 가능
					session.setAttribute("myEmp", myEmp);
					session.setAttribute("myOAuth", myEmp.getOAuth());
					
					if(myEmp.getEmpStatus().equals("R")) {
						redirectAttributes.addFlashAttribute("tempPass", "임시비밀번호를 변경하시겠습니까?");
						employeeService.modifyStatusEmployee(myEmp.getEmpMail(), "U");
					}
					
					eventPublisher.publishEvent(new LoginSuccessEvent(this, myEmp.getOAuth()));
					return String.format(urlRegex, companyId, "main");
				} else if (myEmp.getEmpStatus().equals("S")) {
					// 상태(S : 정지) 문자 인증을 통한 정지 해제 필요
					// 문자 인증을 위한 폼을 띄우도록 유도..
					redirectAttributes.addFlashAttribute("message", "비밀번호 3회 잘 못 입력하여 정지된 계정입니다. 문자 인증을 진행해주세요.");
					redirectAttributes.addFlashAttribute("messageKind", "error");
					return String.format(urlRegex, companyId, "empauth");
				} else if (myEmp.getEmpStatus().equals("W")) {
					redirectAttributes.addFlashAttribute("message", "회원가입 후 이용해주세요.");
					redirectAttributes.addFlashAttribute("messageKind", "error");
					return String.format(urlRegex, companyId, "login");
				} else {
					// 상태(Q : 퇴사) 로그인 불가능
					redirectAttributes.addFlashAttribute("message", "퇴사한 회원입니다. 로그인이 불가능합니다.");
					redirectAttributes.addFlashAttribute("messageKind", "error");
					return String.format(urlRegex, companyId, "login");
				}

			} catch (PKNotFoundException e) {
				// account에 조회된 결과가 없을 때
				Map<String, Integer> count = (Map<String, Integer>) session.getAttribute("countPassword");
				if (count == null) {
					// null이면
					count = new HashMap<>();
					count.put(account.getAccountMail(), 1);
				} else {
					// null이 아니면
					Integer failCount = count.get(account.getAccountMail());
					if (failCount >= 3) {
						// 인증 필요
						// 쿼리문을 날려서 해당 계정 상태 업데이트..
						employeeService.modifyStatusEmployee(account.getAccountMail(), "S");
						redirectAttributes.addFlashAttribute("message", "정지된 계정입니다.");
						redirectAttributes.addFlashAttribute("messageKind", "error");
						return String.format(urlRegex, companyId, "login");
					} else {
						// 3회 미만이면...
						failCount += 1;
						count.put(account.getAccountMail(), failCount);
					}
				}

				session.setAttribute("countPassword", count);
				redirectAttributes.addFlashAttribute("message", "일치하는 정보가 없습니다.");
				redirectAttributes.addFlashAttribute("messageKind", "error");
				return String.format(urlRegex, companyId, "login");
			}
		} else {
			// 검증실패
			redirectAttributes.addFlashAttribute("message", "다시 시도해주세요.");
			redirectAttributes.addFlashAttribute("messageKind", "error");
			return String.format(urlRegex, companyId, "login");
		}
	}
	*/
	
}
