package kr.or.ddit.employee.controller;

import java.security.SecureRandom;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.exception.PKNotFoundException;
import kr.or.ddit.commons.validate.PhoneAuthGroup;
import kr.or.ddit.employee.service.EmployeeService;
import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 페이지에서 비밀번호 찾기 요청시 처리하는 controller
 *
 */
@Slf4j
@Controller
@RequestMapping("/{companyId}/findPW")
public class EmployeeFindPasswordController {
	@Inject
	private EmployeeService employeeService;

	@ResponseBody
	@PostMapping
	public ResponseEntity<String> checkEmp(
			@PathVariable("companyId") String companyId,
			@Validated(PhoneAuthGroup.class) EmployeeVO emp,
			BindingResult errors
	) {
		// 프론트에서 입력한 값을 가지고 controller로 이동
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body("누락된 데이터가 있습니다.");
		} else {
			try {
				// 해당 정보를 가진 사원의 정보가 있는지 확인
				// 해당 정보를 가진 사원이 있다면,
				EmployeeVO myEmp = employeeService.readEmployeeForAuth(emp);
				// UUID를 통해 무작위 문자를 만들기
				String newPassword = generateRandomPassword();
	
				// data base 저장하기
				myEmp.setEmpPass(newPassword);
				ServiceResult resp = employeeService.modifyEmployeePassword(myEmp);
	
				if (resp == ServiceResult.OK) {
					// 데이터베이스 업데이트 완료
					// 바뀐 비밀번호를 메일로 발송
					String subject = "Work2gether: 임시비밀번호 발급";
					
					String content = "<div style='text-align: center; border: 5px dotted black; width: 500px; height: auto;'>\r\n"
							+ "        <h2>" + emp.getEmpName() + "님 안녕하세요!</h2>\r\n" + "        <br>\r\n" + "        <div>"
							+ emp.getEmpName() + "(" + emp.getEmpMail()
							+ ")님의 임시 비밀번호는 <span style='font-weight: bold; font-size:20px;'>" + newPassword
							+ "</span> 입니다.</div>\r\n" + "        <br>\r\n" + "</div>";
					
					SendPwMail(emp.getEmpMail(), subject, content);
					
					employeeService.modifyStatusEmployee(emp.getEmpMail(), "R");
					return ResponseEntity.ok().build();
				} else {
					// 데이터베이스 업데이트 실패
					return ResponseEntity.internalServerError().body("서버에 오류가 있습니다. 다시 시도해주세요.");
				}
	
				// redirect 로 login 페이지로 이동
			} catch (PKNotFoundException e) {
				// 해당 정보를 가진 사원이 없다면 다시 입력해주세요??
				return ResponseEntity.badRequest().body("일치하는 정보가 없습니다. 다시 시도해주세요.");
			}
		}
	}

	
	// 임시비밀번호 만드는 메소드
	public static String generateRandomPassword() {
		SecureRandom random = new SecureRandom();
		// 임시비밀번호에 사용된 문자열
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		// 비밀번호 길이
		int passwordLength = 8;
		// 8자리 임시비밀번호 만들기
		StringBuilder password = new StringBuilder(passwordLength);
		for (int i = 0; i < passwordLength; i++) {
			int index = random.nextInt(characters.length());
			password.append(characters.charAt(index));
		}
		return password.toString();
	}

	// 메일 보내는 메소드
	public void SendPwMail(String empMail, String subject, String content) {
		// 발신자 email(관리자이메일)
		String username = "kimnayeun0111@naver.com";
		// 발신자 email(관리자이메일) 계정의 비밀번호
		String password = "javaTest!2468"; // 발신자 이메일 비밀번호
		// 발신자 이름(받는 사람의 이메일에 보내는 사람으로 표시될 이름)
		String senderName = "Work2gether";
		

		// SMTP 서버 설정
		Properties props = new Properties(); // SMTP 서버 설정을 저장할 'Properties' 객체를 생성합니다.
		props.put("mail.smtp.auth", "true"); // SMTP 서버 인증 사용
		props.put("mail.smtp.starttls.enable", "true"); // STARTTLS 명령 활성화 | 암호화된 연결을 설정하기 위해 필요
		props.put("mail.smtp.host", "smtp.naver.com"); // SMTP 서버 호스트 설정 | 'naver'의 SMTP 서버 사용
		props.put("mail.smtp.port", "587"); // SMTP 서버 포트 설정 (TLS용) | SMTP서버와 통신하는 포트 설정 : naver나 gmail은 공통으로 "587" :
											// TLS / "465" : SSL

		// 세션 생성
		Session session = Session.getInstance(props, new Authenticator() { // 설정된 속성을 사용하여 이메일 세션 생성 | 인증정보 제공함
			protected PasswordAuthentication getPasswordAuthentication() { // 이메일 서버 인증을 위한 'PasswordAuthentication'메소드 Override
				return new PasswordAuthentication(username, password); // 발신자 이메일 계정 정보로 인증 | 사용자 이름과 비밀번호로 인증
			}
		});

		try { // 이메일 전송 시도
				// 메일 메시지 구성
			Message message = new MimeMessage(session); // 새로운 이메일 메시지 객체 생성

			// 발신자 이름 지정하기
			try {
				message.setFrom(new InternetAddress(username, senderName));
			} catch (Exception e) {
				e.printStackTrace();
				// 이름설정 실패하면 보내는 사람 이름 지정 안 하고 보내겠다~~~
				message.setFrom(new InternetAddress(username));
			}

			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empMail));
			message.setSubject(subject); // 이메일 제목 설정

			
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart(); // 이메일 본문을 설정하기 위한 MimeBodyPart 객체 생성
			messageBodyPart.setText(content, "UTF-8"); // 이메일 본문을 설정
			messageBodyPart.setHeader("content-Type", "text/html"); // 본문내용 HTML 설정
			multipart.addBodyPart(messageBodyPart); // 이메일 본문을 Multipart 객체에 추가

			// 메일에 멀티파트 설정
			message.setContent(multipart); // 이메일 메시지에 Multipart 객체를 설정하여 본문과 첨부파일을 포함시킴

			// 메일 전송
			Transport.send(message); // 메시지 전송

			log.info("이메일 전송 성공!!!");

		} catch (MessagingException e) { // 이메일 전송 중 예외가 발생할 경우 처리
			e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
		}

	}

}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
