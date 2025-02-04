package kr.or.ddit.employee.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.api.client.auth.oauth2.Credential;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.employee.service.GoogleOAuthService;
import kr.or.ddit.employee.service.GoogleUserInfoAPIService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping
public class GoogleOAuthJoinController {
	@Inject
	private GoogleOAuthService oauthService;
	
	@Inject
	private GoogleUserInfoAPIService oAuthToEmailService;
	
	
	/*
	 1. UI에서 구글 계정 인증하기 	버튼을 클릭하면 googleLoginForm() 핸들러 호출됨
	 	(해당 핸들러 url : /google-oauth)
	 2. googleLoginForm() 핸들러
	 	 - 해당 핸들러에서는 서비스(비지니스 로직)의 코드를 통해 구글 인증 사이트로 이동함
	 	 - 해당 비지니스 로직에서는 GoogleAuthorizationCodeFlow 객체를 통해 리다이렉트가 될 url을 생성함
	 3. 구글의 계정 인증 사이트에서 인증 처리
	 4. 인증이 완료된 후에 /google-oauthcheck.do url로 매핑됨(여기에서 사용된 링크는 구글 클라우드의 oauth 설정에서 지정한 링크로 매핑해야함)
	     - 해당 url로 매핑될 때 구글에서 code라는 이름으로 Authorization code를 보내줌 해당 코드가 있어야 Credential 객체가 만들어짐
	     - Authorization code를 사용해서 Credential 객체를 만듬
	     - Credential 객체를 만드는 코드는 비지니스 로직에서 getCredentialFromCode 메소드를 통해 return 받음
	       해당 객체를 통해 access token, refresh token, access token 만료 시간 정보 가져옴
	     - 계정 연동을 진행한 이메일 계정을 가져오기 위해 Google User Info API를 호출하여 gmail 계정 가져오기
	     - oauth vo에 담아서 회원가입이 최종 완료 될 때 oauth 테이블에 insert 하기 위해 session에 담아 회원가입 페이지로 이동
	     - 회원가입 페이지에서 나머지 데이터를 입력하여 최종 회원가입 진행
	     
	 */

	// google 계정 연동하기 버튼을 누르면 해당 핸들러로 매핑됨
	// 해당 url로 들어오면 google oauth 인증 페이지로 보냄
	@GetMapping("google-oauth")
	public String googleLoginForm(
			@RequestHeader(value = "Referer", required = false) String referer,
			RedirectAttributes redirectAttributes,
			HttpSession session
	) {
		try {
			// google
			if (referer != null) {
				String url = referer.replace("https://localhost/work2gether/", "");
	            session.setAttribute("previousUrl", url);
	        }
			return String.format("redirect:%s",oauthService.getAuthorizationUrl());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "구글 로그인에 실패했습니다.");
            return "redirect:/";
		}
	}
	
	// 위 핸들러에서 구글 인증 페이지로 넘어가고 사용자가 승인을 하면 이 핸들러로 매핑됨
	@GetMapping("google-oauthcheck.do")
	public String oauth2Callback(
			Authentication authentication,
			HttpSession session,
			@RequestParam(value = "code", required = true) String code, 
			RedirectAttributes redirectAttributes
	) {
		
		OAuthVO oAuth = new OAuthVO();
		String url = (String)session.getAttribute("previousUrl");
		try {
			// code를 사용하여 토큰을 요청, 사용자의 정보가 담긴 Credential 객체를 받아옴
			Credential credential = oauthService.getCredentialFromCode(code);
			
			// credential 객체를 통해 access 토큰 가져오기
			String accessToken = credential.getAccessToken();
			// credential 객체를 통해 refresh 토큰 가져오기(이미 처음 인증을 거친 회원의 경우에는 refreshToken은 null)
			String refreshToken = credential.getRefreshToken();
			
			// Google User Info API 호출해서 계정의 gmail 계정 가져오기
			String email = oAuthToEmailService.getUserEmail(credential);
			
			// myEmp : session에 담겨있는 나의 employee 정보, 회원가입 가능 여부 확인할 때 담음 
			EmployeeVO myEmp = (EmployeeVO)session.getAttribute("myEmp");
			if(myEmp!=null) {
				// 회원가입 할 때
				oAuth.setEmpId(myEmp.getEmpId());
				oAuth.setOauthAccess(accessToken);
				oAuth.setOauthRefresh(refreshToken);
				oAuth.setOauthEmpmail(email);
								

				// 회원가입이 모두 끝난 후에 insert될 oauth 정보(아직 나의 oauth는 아님)
				session.setAttribute("oAuth", oAuth);
				myEmp.setEmpMail(email);
				redirectAttributes.addFlashAttribute("myEmp", myEmp);
				redirectAttributes.addFlashAttribute("message", "구글 계정 인증 완료!");
				redirectAttributes.addFlashAttribute("messageKind", "success");
				
				
				return "redirect:" + url;
			} else {
				// 개인정보 수정할 때
				AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
				AccountVO account = principal.getAccount();
				EmployeeVO myPrincipalEmp = (EmployeeVO)account;
				oAuth.setOauthEmpmail(email);
				// 회원가입이 모두 끝난 후에 insert될 oauth 정보(아직 나의 oauth는 아님)
				session.setAttribute("oAuth", oAuth);
				redirectAttributes.addFlashAttribute("message", "구글 계정 인증 완료!");
				redirectAttributes.addFlashAttribute("messageKind", "success");
				
				return "redirect:" + url;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "구글 계정 인증 실패..");
			redirectAttributes.addFlashAttribute("messageKind", "error");
			return "redirect:" + url;
		}
	}

	
	
}
