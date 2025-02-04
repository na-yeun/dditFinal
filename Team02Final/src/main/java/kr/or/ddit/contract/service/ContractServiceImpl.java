package kr.or.ddit.contract.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.contract.dao.ContractMapper;
import kr.or.ddit.contract.vo.EmpCountVO;
import kr.or.ddit.contract.vo.FirstSettingVO;
import kr.or.ddit.contract.vo.PaymentVO;
import kr.or.ddit.contract.vo.ScaleVO;
import kr.or.ddit.contract.vo.StorageVO;
import kr.or.ddit.organitree.vo.ContractVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractServiceImpl implements ContractService {
	
	@Inject
	private ContractMapper mapper;

	@Override
	public List<ScaleVO> readAllScaleList() {
		return mapper.selectAllScaleList();
	}

	@Override
	public List<EmpCountVO> readAllEmpCountList() {
		return mapper.selectAllEmpCountList();
	}

	@Override
	public List<StorageVO> readAllStorageList() {
		return mapper.selectAllStorageList();
	}

	@Override
	public List<ContractVO> readContINGCompanyList(PaginationInfo<ContractVO> paging) {
		
		// 계약 시작일과 종료일이 varchar2 타입이기때문에 변환을 해줘서 남은날 계산 위한 로직 
		if (paging != null) {
	        int totalRecord = mapper.selectTotalRecord(paging);
	        paging.setTotalRecord(totalRecord);
	    }
		
		
		List<ContractVO> contractList = mapper.selectContINGCompanyList(paging); 
		
		
		
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd");
		
		for(ContractVO contract : contractList) {
			String contractEnd = contract.getContractEnd();			
			try {
				
				// 문자열을 LocalDate로 변환 
				LocalDate endDate = LocalDate.parse(contractEnd,formatDate);
				
				// 계약 종료일에서 현재일을 빼서 남은일수 계산
				long remainDays = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
				contract.setRemainDays((int) remainDays);
//	            log.info("계약명: {}, 남은일수: {}", contract.getContractName(), remainDays);
				
			
			}catch(DateTimeParseException e){
				e.printStackTrace();
				 // 날짜 파싱 오류 시 기본값 처리
                contract.setRemainDays(0);
			}
		}
		
		
		return contractList;
	}

	@Override
	public List<ScaleVO> readScaleList() {
		return mapper.selectAllScaleList();
	}

	@Override
	public List<ContractVO> readDownloadExcelContractingCompanyList(Map<String, Object> condition) {
		
		List<ContractVO> contractList = mapper.selectDownloadExcelContractingCompanyList(condition);
		
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd");
		for(ContractVO contract : contractList) {
			try {
				String contractEnd = contract.getContractEnd();
				
				LocalDate endDate = LocalDate.parse(contractEnd,formatDate);
				long remainDays = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
				contract.setRemainDays((int) remainDays);
				
			}catch (Exception e) {
				e.printStackTrace();
				contract.setRemainDays(0);
			}
		}
		return contractList;
	}

	@Override
	public ContractVO readOneCompanyDetail(String contractId) {
		 ContractVO contract = mapper.selectOneCompanyDetail(contractId);
		 DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd");
		 try {
			 String contractEnd = contract.getContractEnd();
			 LocalDate endDate = LocalDate.parse(contractEnd,formatDate);
			 long remainDays = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
			 contract.setRemainDays((int) remainDays);
		 }catch (Exception e) {
			e.printStackTrace();
			contract.setRemainDays(0);
		}
		 
		return contract; 
	}

	@Override
	public ServiceResult modifyContractingCompanyInfo(ContractVO contract) {
		return mapper.updateContractingCompanyInfo(contract) > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}

	@Override
	public List<ContractVO> readWaitCompanyList(PaginationInfo<ContractVO> paging) {
		if(paging != null) {
			int totalRecord = mapper.selectWaitTotalCount(paging);
			paging.setTotalRecord(totalRecord);
		}
		List<ContractVO> waitCompanyList = mapper.selectWaitCompanyList(paging);
		return waitCompanyList;
	}
	
	@Override
	public ContractVO readOneWaitCompanyDetail(String contractId) {
		return mapper.selectOneWaitCompanyDetail(contractId);
	}
		
	
	@Transactional
	@Override
	public ServiceResult modifyWaitCompanyStatusReject(ContractVO contract) {
		String contractId = contract.getContractId();
		String contractEmail = contract.getContractEmail();
		String contractCompany = contract.getContractCompany();
		String contractReject = contract.getContractReject();
		
		// 반려시 결제테이블의 결제상태 'N' 으로 변경 로직 
		int payUdtCnt = mapper.updatePaymentStatusReject(contractId);
		if(payUdtCnt <= 0) {
			return ServiceResult.FAIL;
		}
		
		int contractUdtCnt = mapper.updateWaitCompanyStatusReject(contract);
		if(contractUdtCnt > 0) {
			
		String subject = "계약 반려 안내";
		String content = String.format(
				"안녕하세요, %s 대표님. <br>귀사의 계약 신청이 아래 사유로 반려되었습니다.<br><br>"
				+ "반려 사유 : %s <br><br>"
				+ "추가 문의사항은 담당자에게 연락 부탁드립니다.<br>"
				+ "감사합니다."
				, contractCompany , contractReject
			);
			sendMail(contractEmail, subject, content);	
			return ServiceResult.OK;
		}else {
			return ServiceResult.FAIL;
		}
	}
			
		
	

	@Override
	public ServiceResult modifyWaitCompanyStatusOK(ContractVO contract) {
		String contractId = contract.getContractId();
		String contractEmail = contract.getContractEmail();
		String contractCompany = contract.getContractCompany();
		String setupPageUrl = String.format(
				"https://localhost/work2gether/setupPage/firstSet"
				, contractId , contractCompany
				
		);
		int contractUdtCnt = mapper.updateWaitCompanyStatusOK(contract);
		if(contractUdtCnt > 0) {
			String subject = "계약 1차 승인 안내";
			String content = String.format(
				    "안녕하세요, %s 대표님.<br>귀사의 계약 신청이 1차 승인되었습니다.<br>"
				    + "초기세팅페이지로 이동하여 양식에 맞게 작성해주세요.<br><br>"
				    + "<form action='%s' method='POST'>"
				    + "  <input type='hidden' name='contractId' value='%s'>"
				    + "  <input type='hidden' name='contractCompany' value='%s'>"
				    + "  <button type='submit' style='color: blue; text-decoration: underline; background: none; border: none; cursor: pointer;'>초기 세팅 페이지 바로가기</button>"
				    + "</form><br>"
				    + "초기세팅 신청결과는 신청일로부터 3일 내에 메일에서 확인 가능하며,<br>"
				    + "추가 문의사항은 담당자에게 연락 부탁드립니다.<br>감사합니다.",
				    contractCompany,
				    setupPageUrl,
				    contractId,
				    contractCompany
				);
			sendMail(contractEmail, subject, content);		
			return ServiceResult.OK;
		}else {
			return ServiceResult.FAIL;
		}
		
	}

// =====================================================================================================================================================
	/**
	 * 메일 전송 메소드 
	 * @param contractEmail
	 * @param subject
	 * @param content
	 */
	private void sendMail(String contractEmail, String subject, String content) {
		final String username = "kimnayeun0111@naver.com";
		// 발신자 email(관리자이메일) 계정의 비밀번호
		final String password = ""; // 발신자 이메일 비밀번호
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
		Session session = Session.getInstance(props, new Authenticator() { // 설정된 속성을 사용하여 이메일 세션 생성 | 인증정보
																						// 제공함
			protected PasswordAuthentication getPasswordAuthentication() { // 이메일 서버 인증을 위한 'PasswordAuthentication'메소드
																			// Override
				return new PasswordAuthentication(username, password); // 발신자 이메일 계정 정보로 인증 | 사용자
																		// 이름과 비밀번호로 인증
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

		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(contractEmail));
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
	
// =====================================================================================================================================================
	
	
	@Override
	public List<ContractVO> readWaitFirstSettingList(PaginationInfo<ContractVO> paging) {
		if(paging !=null) {
			int totalRecord = mapper.selectFirstSettingTotalCount(paging);
			paging.setTotalRecord(totalRecord);
		}
		List<ContractVO> fSettingList = mapper.selectWaitFirstSettingList(paging);
		return fSettingList;
	}

	@Override
	public ServiceResult addFirstSetting(FirstSettingVO fSetting) {
		
		return mapper.insertFirstSetting(fSetting) > 0 ? ServiceResult.OK : ServiceResult.FAIL;
	}
	
	
	// 사용자가 계약신청 양식 최종 제출하면 결제 테이블과 계약업체 테이블에 같이 insert 
	@Transactional
	@Override
	public ServiceResult addContractRequestForm(ContractVO contract, String payAmount) {
		
		// 계약시작일과 결제값 파라미터 null 검증 
		if(StringUtils.isBlank(contract.getContractStart())) {
			throw new IllegalArgumentException("계약시작일 누락");
		}
		if(StringUtils.isBlank(payAmount)) {
			throw new IllegalArgumentException("최종 결제값 누락 ");
		}
		if (StringUtils.isBlank(payAmount)) {
		    throw new IllegalArgumentException("최종 결제값 누락");
		}
		
		// 계약시작일의 -을 제외한 8자리 
		String yyyymmdd = contract.getContractStart().replace("-", "").substring(0,8);
		log.info("yyyymmdd >>> {}" , yyyymmdd);
		String contractEnd = contract.getContractEnd().replace("-", "").substring(0,8);
		PaymentVO payment = new PaymentVO();
		
		
		
		synchronized (this) { // ContractId 생성 
			// 계약시작일 날짜 기준 maxId 조회 
			String maxContractId = mapper.selectMaxContractIdByJoin(yyyymmdd);
			int nextNumber = 1; 
			log.info("maxContractId >>> {}",maxContractId);
			
			if(StringUtils.isNotBlank(maxContractId)) {
				String numPart = maxContractId.substring(8);
				nextNumber = Integer.parseInt(numPart) + 1;
			}
			// yyyymmdd + 0001 과 같은 형태로 포맷팅 해서 contractId 만들어주기 
			String contractId = yyyymmdd + String.format("%04d", nextNumber);
			contract.setContractId(contractId);
			contract.setContractStart(yyyymmdd);
			contract.setContractEnd(contractEnd);
			
			String payPK = LocalDate.now().toString();
			String aaaabbcc = payPK.replace("-", "").substring(0,8);
			String maxPayId = mapper.selectMaxPayIdByJoin(aaaabbcc);
			if(StringUtils.isNotBlank(maxPayId)) {
				String numPart = maxPayId.substring(8);
				nextNumber = Integer.parseInt(numPart) + 1;
			}
				String payId = aaaabbcc + String.format("%04d", nextNumber);
				payment.setPayId(payId);
				payment.setContractId(contractId);
				payment.setPayAmount(Long.parseLong(payAmount));
				
		}
		
			int rowcnt = mapper.insertContractRequestForm(contract);
			if(rowcnt == 1) {
				int paymentRowcnt = mapper.insertPayment(payment);
				if(paymentRowcnt == 1) {
					return ServiceResult.OK;
				}
				else {
					return ServiceResult.FAIL;
				}
			}
			else {
				return ServiceResult.FAIL;
			}
		
		
	}
	
	
	
	@Override
	public ContractVO readOneFSettingDetail(String contractId) {
		if(StringUtils.isBlank(contractId)) {
			throw new IllegalArgumentException("계약업체 아이디 값 누락");
		}
		ContractVO contract =  mapper.selectOneFSettingDetail(contractId);
		
		return contract;
	}
	
	
	@Override
	public ServiceResult modifyBucketAndStatus(ContractVO contract) {
		// 여기서 버킷명 생성. 생성방법 >> contractId +"-"+contractCompany 
		
		if(StringUtils.isBlank(contract.getContractId())) {
			throw new IllegalArgumentException("계약업체 아이디값 누락 ");
		}
		if(StringUtils.isBlank(contract.getContractCompany())) {
			throw new IllegalArgumentException("계약업체명 누락 ");
		}
		String contractId = contract.getContractId().trim().toString();
		String contractCompany = contract.getContractCompany().trim();
		String contractEmail = contract.getContractEmail().trim().toString(); 
		String finalURL = String.format(
				"https://localhost/work2gether/a001/login"
				
		);
		
		// @ 이전까지 잘라버리는 역할. 
		int atIndex = contractEmail.indexOf("@");
		String empId = atIndex > 0 ? contractEmail.substring(0,atIndex) : contractEmail;
		
		String contractBucket = contractId+"-"+contractCompany;
		
		
		ContractVO contractVO = mapper.selectContractStart(contractId);
		    if (contractVO != null) {
		    	String contractStart = contractVO.getContractStart();
		        contract.setContractStart(contractStart);
		    } else {
		        throw new IllegalArgumentException("계약 시작일 정보를 찾을 수 없습니다.");
		    }
		
		
		 System.out.println("contractCompany >>>>" +contractCompany);
		contract.setContractBucket(contractBucket);
		int rowcnt = mapper.updateBucketAndStatus(contract);
		if(rowcnt > 0) {
			String subject = "계약 최종 승인 안내";
			String content = String.format(
				    "안녕하세요, %s 대표님. <br>귀사의 초기세팅 신청이 최종 승인되었습니다.<br>"
				    + "아래 URL로 접속하여 이용 가능합니다.<br>"
				    + "접속 아이디는 %s 이며 초기비밀번호는 123 입니다.<br><br>"
				    + "<form action='%s' method='get'>"
				    + " <input type='hidden' name='contractId' value='%s'>"
				    + " <input type='hidden' name='contractCompany' value='%s'>"
				    + " <input type='hidden' name='contractStart' value='%s'>"
				    + " <button type='submit' style='color: blue; text-decoration: underline; background: none; border: none; cursor: pointer;'>그룹웨어 접속하기</button>"
				    + "</form><br>"
				    + "추가 문의사항은 담당자에게 연락 부탁드립니다.<br>감사합니다.",
				    contractCompany != null ? contractCompany : "N/A",
				    empId != null ? empId : "N/A",
				    finalURL != null ? finalURL : "N/A",
				    contractId != null ? contractId : "N/A",
				    contractCompany != null ? contractCompany : "N/A",
				    contract.getContractStart() != null ? contract.getContractStart() : "N/A"
				);

				log.info("Generated Email Content: {}", content);

			sendMail(contractEmail, subject, content);
			return ServiceResult.OK;
		}else {
			return ServiceResult.FAIL;
		}
				
	}
		
}
