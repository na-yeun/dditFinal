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

import com.google.api.client.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.employee.service.GoogleOAuthService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.gmail.service.GmailAPICommonService;
import kr.or.ddit.gmail.service.GmailListCommonService;
import kr.or.ddit.gmail.service.GmailRefreshCommonService;
import kr.or.ddit.gmail.service.MailAttachmentService;
import kr.or.ddit.gmail.service.MailDeleteService;
import kr.or.ddit.gmail.service.MailImportantService;
import kr.or.ddit.gmail.service.MailSendService;
import kr.or.ddit.gmail.vo.MailAttachmentVO;
import kr.or.ddit.gmail.vo.MailDeleteVO;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailSendDTO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/mail/important")
public class MailImportantController {
	@Inject
	private MailImportantService mailImportantservice;
	@Inject
	private MailAttachmentService attachmentService;
	@Inject
	private MailDeleteService deleteService;
	@Inject
	private GmailAPICommonService commonCRUDService;
	@Inject
	private GmailListCommonService commonService;
	@Inject
	private GmailRefreshCommonService refreshService;
	@Inject
	private MailSendService sendService;
//	@Inject
//	private MailReceivedService receivedService;
//	@Inject
//	private MailSentService mailSentservice;

	@Inject
	private GoogleOAuthService oAuthService;
	
	@GetMapping
	public String getDeleteMailListPage(
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

		List<MailImportantVO> sentMailList = mailImportantservice.readImportantMailList(paging);

		PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");

		log.info(pagingHtml);

		model.addAttribute("list", sentMailList);
		model.addAttribute("pagingHtml", pagingHtml);
		model.addAttribute("variousCondition", variousCondition);

		return "mail/mailImportantList";
	}
	
	@GetMapping("{mailMessageId}")
	@ResponseBody
	public ResponseEntity<MailImportantVO> getSentMailDetail(
			@PathVariable("mailMessageId") String mailMessageId
			, Model model) {
		MailImportantVO myMail = mailImportantservice.readImportantMailDetail(mailMessageId);

		if (myMail == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MailImportantVO());
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
			, HttpSession session) {
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
		
		byte[] result = commonCRUDService.getAttachment(messageId, mailattachmentId, myNewOAuth);
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
	public ResponseEntity trash(
			Authentication authentication
			, @RequestBody List<String> checkedIds
			, HttpSession session) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;	
		// 혹시 refresh token이 만기되었을 수 있으니 체크 한 번..
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());
		try {
			for (String messageId : checkedIds) {
				// 휴지통에 insert
				MailImportantVO myMail = mailImportantservice.readImportantMailDetail(messageId);
				
				MailDeleteVO updateMail = new MailDeleteVO(
						myMail.getEmpId(), myMail.getEmpMail(), myMail.getMailMessageId(),
						myMail.getImailTo(), myMail.getImailFrom(), myMail.getImailSubject(),
						myMail.getImailDate(), myMail.getImailContentType(), myMail.getImailContent(),
						myMail.getImailCc(), myMail.getImailBcc(), myMail.getImailReplyTo(),
						myMail.getImailCalltime()
				);
				
				int delResult = deleteService.createDeleteMail(updateMail);
				if(delResult!=1) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
				
				// 데이터베이스 삭제
				int dbImportantResult = mailImportantservice.deleteImportantMail(messageId);
				
				if (dbImportantResult != 1) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
				}
				MailAttachmentVO attach = new MailAttachmentVO();
				attach.setMailmessageId(messageId);
				attach.setEmpMail(myEmp.getEmpMail());
				// 데이터베이스에서 해당 메일의 첨부파일 여부를 확인
				int attachResult = attachmentService.readAttachmentExist(attach);
				if(attachResult>0) {
					// 삭제
					int dbAttachResult = attachmentService.deleteMailAttachment(messageId);
					
					if(dbAttachResult<1) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생");
					}
				}

				// API 호출해서 starred 라벨을 없애고 trash 처리(자동으로 없어진다고는 하는데 불안함)
				ServiceResult apiResult1 
					= commonCRUDService.disMailLables(messageId, myNewOAuth, new String[]{"STARRED"});
						
				ServiceResult apiResult
					= commonCRUDService.trashMail(messageId, myNewOAuth, "trash");
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
	
	

	@PostMapping("disimportant")
	@ResponseBody
	public ResponseEntity<String> disimportant(
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
				// important 테이블에서 삭제 & api 호출 & label 비교해서 sent나 received에 넣기
				// 메일의 중요 라벨 삭제하기
				commonCRUDService.disMailLables(messageId, myNewOAuth, new String[] {"STARRED"});
				// 메일의 현재 라벨 찾아와서, SENT 혹은 RECIVED에 넣기
				HttpResponse thisMail = commonService.getMailDetail("messages", messageId, myNewOAuth);
				
				JsonObject detailJson = JsonParser.parseString(thisMail.parseAsString())
												  .getAsJsonObject();
				JsonArray labelIds = detailJson.getAsJsonArray("labelIds");
				log.info("labelIds : {}", labelIds);
				for(JsonElement labelId : labelIds) {
					String label = labelId.getAsString();
					if (label.equalsIgnoreCase("SENT")) {
						commonService.setSentMail(detailJson, messageId, myNewOAuth);
						break;
			        } else if (label.equalsIgnoreCase("INBOX")) {
			        	commonService.setReceivedMail(detailJson, messageId, myNewOAuth);
						break;
			        }
				}
				// 데이터 베이스 삭제하기
				mailImportantservice.deleteImportantMail(messageId);
				
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
		MailImportantVO myMail = mailImportantservice.readImportantMailDetail(messageId);
		MailSendDTO forward = sendService.importantToSendDTO(myMail, messageId);
		model.addAttribute("sendMail",forward);
		// controller에서는 해당 id를 가지고 메일의 상세 내용을 가지고 와서 MailSendDTO에 담음
		// 담은 후 mail send form으로 이동
		return "mail/mailSendForm";
	}
	
	@PostMapping("reply")
	public String reply(
			@RequestParam("messageId")String messageId
			, Model model
	) {
		MailImportantVO myMail = mailImportantservice.readImportantMailDetail(messageId);
		MailSendDTO reply = sendService.replyToImportant(myMail, messageId);
		
		model.addAttribute("sendMail",reply);
		return "mail/mailSendForm";
	}
}
