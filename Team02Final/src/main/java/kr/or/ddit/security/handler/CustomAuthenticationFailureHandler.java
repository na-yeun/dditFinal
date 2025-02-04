package kr.or.ddit.security.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import kr.or.ddit.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	@Inject
	private EmployeeService employeeService;

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request
			, HttpServletResponse response
			, AuthenticationException exception) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
        Map<String, Integer> count = (Map<String, Integer>) session.getAttribute("countPassword");
        String accountMail = request.getParameter("accountMail"); // 이메일 파라미터

        
    	// 현재 요청이 들어온 URI를 정규식을 통해 회사 id 추출
		String requestURI = request.getRequestURI();
		Pattern pattern = Pattern.compile("/([^/]+)/([^/]+)");
		Matcher matcher = pattern.matcher(requestURI);
		String companyId = null;
		if (matcher.find()) {
			companyId = matcher.group(2); 
		}
		
        if (count == null) {
            // 실패 기록 초기화
            count = new HashMap<>();
            count.put(accountMail, 1);
        } else {
            // 기존 실패 기록 업데이트
            Integer failCount = count.getOrDefault(accountMail, 0);
            if (failCount >= 3) {
                // 3회 이상 실패 -> 계정 상태 업데이트
                employeeService.modifyStatusEmployee(accountMail, "S"); // 계정 상태 '정지'로 업데이트

                // 상태 업데이트 후 세션 초기화
                count.remove(accountMail);
                session.setAttribute("countPassword", count);

                // 사용자에게 메시지 전달
                request.setAttribute("message", "정지된 계정입니다.");
                request.setAttribute("messageKind", "error");
                response.sendRedirect(request.getContextPath()+"/"+companyId+"/login");
                return;
            } else {
                // 실패 횟수 증가
                failCount += 1;
                count.put(accountMail, failCount);
            }
        }

        session.setAttribute("countPassword", count); // 세션에 실패 횟수 저장

        // 실패 메시지 설정
        request.getSession().setAttribute("message", "일치하는 정보가 없습니다.");
        request.getSession().setAttribute("messageKind", "error");
        response.sendRedirect(request.getContextPath()+"/"+companyId+"/login"); // 로그인 페이지로 리다이렉트

	}

}
