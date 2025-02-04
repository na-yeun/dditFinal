
package kr.or.ddit.gmail.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.employee.service.GoogleOAuthService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.gmail.service.GmailAPICommonService;
import kr.or.ddit.gmail.service.GmailRefreshCommonService;
import kr.or.ddit.gmail.service.MailAttachmentService;
import kr.or.ddit.gmail.service.MailDeleteService;
import kr.or.ddit.gmail.service.MailImportantService;
import kr.or.ddit.gmail.service.MailReceivedService;
import kr.or.ddit.gmail.service.MailSendService;
import kr.or.ddit.gmail.vo.MailAttachmentVO;
import kr.or.ddit.gmail.vo.MailDeleteVO;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;
import kr.or.ddit.gmail.vo.MailSendDTO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/mail/received")
public class MailReceivedController {
	@Inject
	private MailReceivedService receivedService;
	@Inject
	private MailDeleteService deleteService;
	@Inject
	private MailImportantService importantService;
	@Inject
	private MailAttachmentService attachmentService;
	@Inject
	private GmailAPICommonService commonService;
	@Inject
	private GmailRefreshCommonService refreshService;
	@Inject
	private MailSendService sendService;

	@Inject
	private GoogleOAuthService oAuthService;

	@GetMapping
	public String getReceivedMailListPage(
			Authentication authentication
			, @PathVariable("companyId") String companyId
			, Optional<Integer> page
			, @RequestParam(value = "type", required = false) String type
			, @RequestParam(value = "value", required = false) String value
			, HttpSession session
			, Model model
	) {

		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;	

		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(page.orElse(1));
		Map<String, Object> variousCondition = new HashMap<>();
		variousCondition.put("empMail", myEmp.getEmpMail());

		if (!(StringUtils.isBlank(type) && StringUtils.isBlank(value))) {
			variousCondition.put("type", type);
			variousCondition.put("value", value);
			model.addAttribute("type", type);
			model.addAttribute("value", value);
		}

		paging.setVariousCondition(variousCondition);

		List<MailReceivedVO> receivedMailList = receivedService.readReceivedMailList(paging);

		PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");

		model.addAttribute("list", receivedMailList);
		model.addAttribute("pagingHtml", pagingHtml);
		model.addAttribute("variousCondition", variousCondition);

		return "mail/mailReceivedList";
	}

	@GetMapping("{mailMessageId}")
	@ResponseBody
	public ResponseEntity<MailReceivedVO> getReceivedMailDetail(
			Authentication authentication
			, @PathVariable("mailMessageId") String mailMessageId
			, Model model
	) {
		MailReceivedVO myMail = receivedService.readReceivedMailDetail(mailMessageId);

		if (myMail == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MailReceivedVO());
		}
		return ResponseEntity.ok(myMail);
	}

	// 메일 새로고침 버튼 클릭시
	@GetMapping("refresh")
	@ResponseBody
	public ResponseEntity<String> refreshMailList(Authentication authentication) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		// 혹시 refresh token이 만기되었을 수 있으니 체크 한 번..
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());

		ServiceResult result = refreshService.allRefresh(myNewOAuth,myEmp.getEmpMail());
		if(result==ServiceResult.OK) {
			return ResponseEntity.ok("성공");
		} else {
			return ResponseEntity.internalServerError().body("실패");
		}
	}

	@GetMapping("download")
	@ResponseBody
	public ResponseEntity getAttachment(
			Authentication authentication
			, @RequestParam("messageId") String messageId
			, @RequestParam("mailattachmentId") String mailattachmentId
			, HttpSession session
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		// 혹시 refresh token이 만기되었을 수 있으니 체크 한 번..
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());
		MailAttachmentVO attachment = new MailAttachmentVO();
		attachment.setMailmessageId(messageId);
		attachment.setMailattachmentId(mailattachmentId);
		attachment.setMailattachmentMimetype(mailattachmentId);
		MailAttachmentVO myAttach = attachmentService.readAttachmentDetail(attachment);

		byte[] result = commonService.getAttachment(messageId, mailattachmentId, myNewOAuth);
		if (result != null || result.length != 0) {

			try {
				String encodedFilename = URLEncoder.encode(myAttach.getMailattachmentName(), "UTF-8").replaceAll("\\+", "%20");
				
				return ResponseEntity.ok()
		                .header(HttpHeaders.CONTENT_DISPOSITION,
		                        "attachment; filename*=UTF-8''" + encodedFilename)
		                .contentType(MediaType.parseMediaType(myAttach.getMailattachmentMimetype()))
		                .body(result);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("첨부파일 인코딩에 실패했습니다.");
			}
		} else {
			// Handle the case where the file data is not available
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("첨부파일을 불러올 수 없습니다.");
		}
	}

	@PostMapping("trash")
	@ResponseBody
	public ResponseEntity<String> trash(
			Authentication authentication
			, @RequestBody List<String> checkedIds
			, HttpSession session
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;	
		// 혹시 refresh token이 만기되었을 수 있으니 체크 한 번..
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());
		try {
			for (String messageId : checkedIds) {
				// 삭제하기 전 휴지통에 insert
				MailReceivedVO myMail = receivedService.readReceivedMailDetail(messageId);
				MailDeleteVO updateMail = new MailDeleteVO(
						myMail.getEmpId(), myMail.getEmpMail(), myMail.getMailMessageId(),
						myMail.getEmpMail(), myMail.getRmailFrom(), myMail.getRmailSubject(),
						myMail.getRmailDate(), myMail.getRmailContentType(), myMail.getRmailContent(),
						myMail.getRmailCc(), null, myMail.getRmailReplyTo(),
						myMail.getRmailCalltime()
				);
				
				
				int delResult = deleteService.createDeleteMail(updateMail);
				if(delResult!=1) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
				
				// 받은 메일함에서 데이터 삭제
				int dbReceivedResult = receivedService.deleteReceivedMail(messageId);
				if (dbReceivedResult != 1) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
				MailAttachmentVO attach = new MailAttachmentVO();
				attach.setMailmessageId(messageId);
				attach.setEmpMail(myEmp.getEmpMail());
				// 데이터베이스에서 해당 메일의 첨부파일 여부를 확인
				int attachResult = attachmentService.readAttachmentExist(attach);
				if(attachResult>0) {
					// 삭제
					attachmentService.deleteMailAttachment(messageId);					
				}
				// API 호출해서 trash 처리
				ServiceResult apiResult = commonService.trashMail(messageId, myNewOAuth, "trash");
				if (apiResult != ServiceResult.OK) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
			}
			return ResponseEntity.ok("성공");
		} catch (Exception e) {
			// 예외 발생 시 상세 메시지 로깅
			log.error("에러 발생: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생: " + e.getMessage());
		}
	}
	
	

	@PostMapping("important")
	@ResponseBody
	public ResponseEntity<String> important(
			Authentication authentication
			, @RequestBody List<String> checkedIds
			, HttpSession session
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		// 혹시 refresh token이 만기되었을 수 있으니 체크 한 번..
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());
		try {
			for (String messageId : checkedIds) {
				// 삭제하기 전 중요 메일에 insert한 후에 받은메일함에서 delete
				/*
				 * 
				 * String empId, String empMail, String mailMessageId, String imailTo, String imailFrom,
			String imailSubject, String imailDate, String imailContentType, String imailContent, String imailCc,
			String imailBcc, String imailReplyTo, String imailCalltime
				 * */
				MailReceivedVO myMail = receivedService.readReceivedMailDetail(messageId);
				MailImportantVO updateMail = new MailImportantVO(
						myMail.getEmpId(), myMail.getEmpMail(), myMail.getMailMessageId(),
						myMail.getEmpMail(), myMail.getRmailFrom(), myMail.getRmailSubject(),
						myMail.getRmailDate(), myMail.getRmailContentType(), myMail.getRmailContent(),
						myMail.getRmailCc(), null, myMail.getRmailReplyTo(),
						myMail.getRmailCalltime()
				);
				
				int impResult = importantService.createImportantMail(updateMail);
				if(impResult!=1) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
				
				int dbSentResult = receivedService.deleteReceivedMail(messageId);
				if (dbSentResult != 1) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
				ServiceResult apiResult = commonService.addMailLables(messageId, myNewOAuth, new String[] {"STARRED"});
				if(apiResult != ServiceResult.OK) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
			}		
			return ResponseEntity.ok("성공");
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생: " + e.getMessage());
		}
	}
	
	
	// 메일 전달 버튼 클릭시 이쪽으로 이동
	@PostMapping("forward")
	public String forward(
			@RequestParam("messageId")String messageId
			, Model model
	) {
		MailReceivedVO myMail = receivedService.readReceivedMailDetail(messageId);
		MailSendDTO forward = sendService.receivedToSendDTO(myMail, messageId);
		model.addAttribute("sendMail",forward);
		// controller에서는 해당 id를 가지고 메일의 상세 내용을 가지고 와서 MailSendDTO에 담음
		// 담은 후 mail send form으로 이동
		return "mail/mailSendForm";
	}
	
	// 메일 답장 버튼 클릭시
	@PostMapping("reply")
	public String reply(
			@RequestParam("messageId")String messageId
			, Model model
	) {
		MailReceivedVO myMail = receivedService.readReceivedMailDetail(messageId);;
		MailSendDTO reply = sendService.replyToReceived(myMail, messageId);
		
		model.addAttribute("sendMail",reply);
		return "mail/mailSendForm";
	}


}
