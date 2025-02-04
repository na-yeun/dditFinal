package kr.or.ddit.contract.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.commons.exception.PKNotFoundException;
import kr.or.ddit.employee.service.EmployeeService;

@Controller
@RequestMapping("/contractAuthCheck")
public class ContractAuthCheckController {
	
	@Inject
	private EmployeeService employeeService;
	
	
	// 인증번호 전송 버튼을 클릭하면 실행됨
		@PostMapping
		@ResponseBody
		public ResponseEntity<String> sendSMS(
				
				BindingResult errors,
				// 인증번호를 세션에 저장하기 위해
				HttpSession session
		){
			
			if(!errors.hasErrors()) {
				// 검증 통과
				
				try {
					// 데이터베이스에 해당 정보로 등록된 정보가 있는지 확인
					// 없으면 exception, 있으면 try 안에 실행
					
					String verificationCode = employeeService.generateVerificationCode();
					String smsText = String.format("[Work2gether] 인증번호 [%s]를 입력해주세요. 사칭/전화사기에 주의하세요.", verificationCode);
					session.setAttribute("verificationCode", verificationCode);
					// log.info("==> ☎️☎️☎️☎️☎️☎️☎️☎️전송될 메세지 : {}", smsText);

					// 성공적으로 응답 반환
					return ResponseEntity.ok("문자 전송 완료");
				} catch(PKNotFoundException e) {
					// 등록된 정보가 없을 때 exception 발생
					return ResponseEntity.badRequest().body("일치하는 정보가 없습니다.");
				}
				
			} else {
				// 검증 실패
				return ResponseEntity.badRequest().body("빈 칸 없이 전송해주세요.");
			}
		}
		
			@PostMapping("/checkTelAuth")
			@ResponseBody
			public ResponseEntity<String> authCodeCheck(
					// 클라이언트가 입력한 인증번호
					@RequestParam(value = "authCode", required = true) String authCode,
					// 세션에 저장된 인증번호를 꺼내오기 위해..
					HttpSession session
	//				Model model
			) {
				// 세션에 저장된 인증번호 꺼내오기
				String verificationCode = (String) session.getAttribute("verificationCode");
	
				// 세션에 저장된 인증번호와 입력한 인증번호 비교
				if (StringUtils.isNotBlank(verificationCode) && verificationCode.equals(authCode)) {
					// 상태 ok로 리턴
					return ResponseEntity.ok().build();
				} else {
					return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다. 다시 입력해주세요.");
				}
			}
		
		
		
		
	
}
