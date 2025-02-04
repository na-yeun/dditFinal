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
import kr.or.ddit.gmail.service.MailReceivedService;
import kr.or.ddit.gmail.service.MailSentService;
import kr.or.ddit.gmail.vo.MailAttachmentVO;
import kr.or.ddit.gmail.vo.MailDeleteVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/mail/delete")
public class MailDeleteController {
	@Inject
	private MailDeleteService mailDeleteservice;
	@Inject
	private MailAttachmentService attachmentService;
	@Inject
	private GmailAPICommonService commonCRUDService;
	@Inject
	private GmailListCommonService commonService;
	@Inject
	private GmailRefreshCommonService refreshService;
	@Inject
	private MailSentService sentService;
	@Inject
	private MailReceivedService receivedService;

	@Inject
	private GoogleOAuthService oAuthService;
	
	/**
	 * 삭제 메일 리스트 반환
	 * @param companyId
	 * @param page
	 * @param type
	 * @param value
	 * @param session
	 * @param model
	 * @return
	 */
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

		List<MailDeleteVO> sentMailList = mailDeleteservice.readDeleteMailList(paging);

		PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");

		log.info(pagingHtml);

		model.addAttribute("list", sentMailList);
		model.addAttribute("pagingHtml", pagingHtml);
		model.addAttribute("variousCondition", variousCondition);

		return "mail/mailDeleteList";
	}
	
	@GetMapping("{mailMessageId}")
	@ResponseBody
	public ResponseEntity<MailDeleteVO> getSentMailDetail(
			@PathVariable("mailMessageId") String mailMessageId
			, Model model) {
		MailDeleteVO myMail = mailDeleteservice.readDeleteMailDetail(mailMessageId);

		if (myMail == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MailDeleteVO());
		}
		return ResponseEntity.ok(myMail);
	}


	/**
	 *  메일 새로고침 버튼 클릭시
	 * @param session
	 * @return
	 */
	@GetMapping("refresh")
	@ResponseBody
	public ResponseEntity<String> refreshMailList(
			Authentication authentication
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;
		// 혹시 refresh token이 만기되었을 수 있으니 체크 한 번..
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());
		
		ServiceResult result = refreshService.allRefresh(myNewOAuth, myEmp.getEmpMail());
		if(result==ServiceResult.OK) {
			return ResponseEntity.ok("성공");
		} else {
			return ResponseEntity.internalServerError().body("실패");
		}
	}
	
	/**
	 * 첨부파일 다운로드
	 * @param messageId
	 * @param mailattachmentId
	 * @param session
	 * @return
	 */
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
	
	/**
	 * 메일 영구 삭제
	 * @param checkedIds
	 * @param session
	 * @return
	 */
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
				// 데이터베이스 삭제
				int dbDeleteResult = mailDeleteservice.deleteDeleteMail(messageId);
				if (dbDeleteResult != 1) {
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

				// API 호출해서 trash 처리
				ServiceResult apiResult = commonCRUDService.deleteMail(messageId, myNewOAuth);
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
	
	/**
	 * 메일 복원
	 * @param checkedIds
	 * @param session
	 * @return
	 */
	@PostMapping("restore")
	@ResponseBody
	public ResponseEntity restore(
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
				// messageId를 통해 api를 호출하여 복원
				// important 메일과 동일한 과정을 통해 원래 자리로 return
				// db처리
				commonCRUDService.trashMail(messageId, myNewOAuth, "untrash");
				
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
			        } else {
			        	commonService.setReceivedMail(detailJson, messageId, myNewOAuth);
			        	break;
			        }
				}
				
				mailDeleteservice.deleteDeleteMail(messageId);
				
				
			}
			return ResponseEntity.ok("성공");
		} catch (Exception e) {
			// 예외 발생 시 상세 메시지 로깅
			log.error("에러 발생: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류 발생: " + e.getMessage());
		}
	}
}
