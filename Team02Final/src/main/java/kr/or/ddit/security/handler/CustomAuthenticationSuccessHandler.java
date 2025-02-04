package kr.or.ddit.security.handler;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.event.loginGmailEvent.LoginSuccessEvent;
import kr.or.ddit.provider.vo.ProviderVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Inject
	private EmployeeService employeeService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request
			, HttpServletResponse response
			, Authentication authentication) throws IOException, ServletException {

		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		String urlFormat = "%s/%s/%s";
		String url = null;
				
		if (account instanceof EmployeeVO) {
			// EMPLOYEE계정이면
			EmployeeVO myEmp = (EmployeeVO) account;
			String empStatus = myEmp.getEmpStatus();
			String companyId = myEmp.getCompanyId();
			
			if (empStatus.equals("W")) {
				// 인증 필요
				request.getSession().setAttribute("message", "인증을 진행해주세요.");
				request.getSession().setAttribute("messageKind", "error");
				url = String.format(urlFormat, request.getContextPath(), companyId, "login");
				response.sendRedirect(url);
				
			} else {

				if (myEmp.getEmpStatus().equals("U") || myEmp.getEmpStatus().equals("V")) {
					// 상태(U : 재직중 / V : 휴가) 로그인 가능
					// 여기 주석 풀기
					eventPublisher.publishEvent(new LoginSuccessEvent(this, myEmp.getOauth()));
					// 이전 페이지로
					url = String.format(urlFormat, request.getContextPath(), companyId, "main");
					response.sendRedirect(url);
				} else if (myEmp.getEmpStatus().equals("R")) {
					// 상태 (R: 비밀번호 재발급 계정, 비밀번호 수정 요청)
					employeeService.modifyStatusEmployee(myEmp.getEmpMail(), "U");
					request.getSession().setAttribute("message", "비밀번호 재발급 계정입니다. 비밀번호를 변경해주세요.");
					request.getSession().setAttribute("messageKind", "error");
					url = String.format(urlFormat, request.getContextPath(), companyId, "mypage/edit");
					response.sendRedirect(url);
				} else if (myEmp.getEmpStatus().equals("S")) {
					// 상태(S : 정지) 문자 인증을 통한 정지 해제 필요
					// 문자 인증을 위한 폼을 띄우도록 유도..
					request.getSession().setAttribute("message", "비밀번호 3회 잘 못 입력하여 정지된 계정입니다. 문자 인증을 진행해주세요.");
					request.getSession().setAttribute("messageKind", "error");
					
					url = String.format(urlFormat, request.getContextPath(), companyId, "empauth");
					response.sendRedirect(url);
					
				} else {
					// 상태(Q : 퇴사) 로그인 불가능
					request.getSession().setAttribute("message", "퇴사한 회원입니다. 로그인이 불가능합니다.");
					request.getSession().setAttribute("messageKind", "error");
					
					url = String.format(urlFormat, request.getContextPath(), companyId, "login");
					response.sendRedirect(url);
					
					response.sendRedirect(url);
				}
			}

		} else if (account instanceof ProviderVO) {
			// 인증 기완료
			ProviderVO myProv = (ProviderVO) account;
			response.sendRedirect(request.getContextPath()+"/prov/main");

		}
	}
}
