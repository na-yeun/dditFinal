package kr.or.ddit.employee.controller;

import java.util.Random;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.commons.exception.PKNotFoundException;
import kr.or.ddit.commons.validate.PhoneAuthGroup;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

/**
 * 로그인 3회 이상 실패하여 계정이 정지되었을 때 인증을 처리하는 controller 
 * 
 *
 */
//@Slf4j
@Controller
@RequestMapping("/{companyId}/empauth")
public class EmployeeAuthCheckController {

	@Inject
	private EmployeeService employeeService;


	@GetMapping
	public String authMessage() {
		return "/employee/employeePhoneAuthForm";
	}
	
	
	// 인증번호 전송 버튼을 클릭하면 실행됨
	@PostMapping
	@ResponseBody
	public ResponseEntity<String> sendSMS(
			// 클라이언트로부터 받은 정보
			@Validated(PhoneAuthGroup.class) @ModelAttribute("emp") EmployeeVO emp,
			BindingResult errors,
			// 인증번호를 세션에 저장하기 위해
			HttpSession session
	){
		
		if(!errors.hasErrors()) {
			// 검증 통과
			
			try {
				// 데이터베이스에 해당 정보로 등록된 정보가 있는지 확인
				// 없으면 exception, 있으면 try 안에 실행
				EmployeeVO myEmp = employeeService.readEmployeeForAuth(emp);
				if(myEmp!=null) {
					String status = myEmp.getEmpStatus();
					if(status.equals("S")) {
						String verificationCode = employeeService.generateVerificationCode();
						String smsText = String.format("[Work2gether] 인증번호 [%s]를 입력해주세요. 사칭/전화사기에 주의하세요.", verificationCode);
						session.setAttribute("verificationCode", verificationCode);
						// log.info("==> ☎️☎️☎️☎️☎️☎️☎️☎️전송될 메세지 : {}", smsText);
						
						employeeService.sendOauthSMS(emp.getEmpPhone(),smsText);
						
						// 성공적으로 응답 반환
						return ResponseEntity.ok("문자 전송 완료");
					} else {
						return ResponseEntity.badRequest().body("정지된 계정이 아닙니다.");
					}
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 정보가 없습니다.");
				}
				
			} catch(PKNotFoundException e) {
				// 등록된 정보가 없을 때 exception 발생(404에러 내보내기)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 정보가 없습니다.");
			}
			
		} else {
			// 검증 실패
			return ResponseEntity.badRequest().body("빈 칸 없이 전송해주세요.");
		}
	}
	
	@PostMapping("checkAuthCode")
	@ResponseBody
	public ResponseEntity<String> authCodeCheck(
			@PathVariable("companyId") String companyId,
			// 클라이언트가 입력한 인증번호
			@RequestParam(value = "authCode", required = true) String authCode,
			@RequestParam("empMail")String empMail,
			// 세션에 저장된 인증번호를 꺼내오기 위해..
			HttpSession session
//			Model model
	) {
		// 세션에 저장된 인증번호 꺼내오기
		String verificationCode = (String) session.getAttribute("verificationCode");

		// 세션에 저장된 인증번호와 입력한 인증번호 비교
		if (StringUtils.isNotBlank(verificationCode) && verificationCode.equals(authCode)) {
			// 데이터베이스의 해당 계정의 상태를 U로 업데이트
			employeeService.modifyStatusEmployee(empMail, "U");
			// 상태 ok로 리턴
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다. 다시 입력해주세요.");
		}
	}

	
	
	
	

}
