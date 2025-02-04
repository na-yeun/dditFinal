package kr.or.ddit.gmail.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.tiles.autotag.core.runtime.annotation.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.http.HttpResponse;
import com.google.api.services.gmail.model.Message;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.validate.MailSendGroup;
import kr.or.ddit.employee.service.GoogleOAuthService;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.gmail.MailSendType;
import kr.or.ddit.gmail.service.GmailAPICommonService;
import kr.or.ddit.gmail.service.GmailListCommonService;
import kr.or.ddit.gmail.service.MailSentService;
import kr.or.ddit.gmail.vo.MailSendDTO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/mail/send")
public class MailSendController {
	@Inject
	private MailSentService sentService;
	@Inject
	private GmailAPICommonService apiService;
	@Inject
	private GoogleOAuthService oAuthService;
	@Inject
	private GmailListCommonService commonService;
	
	public static final String MODELNAME = "sendMail";

	@ModelAttribute(MODELNAME)
	public MailSendDTO member(Model model) {
		MailSendDTO sendMailModel = (MailSendDTO) model.getAttribute(MODELNAME);
		if (sendMailModel != null) {
			return sendMailModel;
		} else {
			return new MailSendDTO();
		}
	}

	@GetMapping
	public String getMailSendForm(
			@PathVariable("companyId") String companyId
			, @Parameter(required = false, name = "empMail") String empMail
			, Model model
	) {
		if(StringUtils.isNotBlank(empMail)) {
			List<String> toList = new ArrayList<>();
			toList.add(empMail);
			MailSendDTO sendMailModel = (MailSendDTO) model.getAttribute(MODELNAME);
			sendMailModel.setMailTo(toList);
		}
		return "mail/mailSendForm";
	}

	@PostMapping
	@ResponseBody
	public Map<String, Object> postMail(
			Authentication authentication
			, @PathVariable("companyId") String companyId
			, @Validated(MailSendGroup.class) @ModelAttribute(MODELNAME) MailSendDTO mailDetails
			, BindingResult errors
			, Model model
	) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO myEmp = (EmployeeVO) account;
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());

		Map<String, Object> response = new HashMap<>();
		if (!errors.hasErrors()) {
			// 검증 성공
			String empMail = principal.getUsername();
			mailDetails.setMailFrom(empMail);

			String forwardMessageId = mailDetails.getMessageId();
			try {
				Message message = null;
				HttpResponse result = null;
				// forwardMessageId 가 null이면 일반메일 null이 아니면 전달메일
				if(forwardMessageId==null) {
					message = apiService.mailMessage(mailDetails, null, myNewOAuth);
					result = apiService.sendMail(MailSendType.SEND, myNewOAuth, message);
				} else {
					message = apiService.mailMessage(mailDetails, forwardMessageId, myNewOAuth);
					result = apiService.sendMail(MailSendType.SEND, myNewOAuth, message);
				}
				
				if (result.getStatusCode() == 200 || result.getStatusCode() == 204) {
					Message responseMessage = result.parseAs(Message.class);
					String messageId = responseMessage.getId();

					HttpResponse detailResponse = commonService.getMailDetail("messages", messageId, myNewOAuth);
					JsonObject detailJson = JsonParser.parseString(detailResponse.parseAsString()).getAsJsonObject();
					commonService.setSentMail(detailJson, messageId, myNewOAuth);
					response.put("status", "success");

					return response;
				} else {
					response.put("status", "fail");
					return response;
				}

			} catch (MessagingException | IOException e) {
				e.printStackTrace();
				response.put("status", "error");
				return response;
			}

		} else {
			// 검증 실패
			response.put("status", "fail");
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : errors.getFieldErrors()) {
			    errorMap.put(error.getField(), error.getDefaultMessage());
			}
			response.put("errors", errorMap);
			return response;

		}
	}

	@PostMapping("saveDraft")
	public ResponseEntity<String> saveDraftMail(
			Authentication authentication
			, @PathVariable("companyId") String companyId
			, MailSendDTO mailDetails
	){
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();

		EmployeeVO myEmp = (EmployeeVO) account;
		OAuthVO myNewOAuth = oAuthService.getUsableAccessToken(myEmp.getOauth(),myEmp.getEmpMail());
		// 검증 성공
		String empMail = principal.getUsername();
		mailDetails.setMailFrom(empMail);

		try {
			Message message = apiService.mailMessage(mailDetails, null ,myNewOAuth);
			HttpResponse sendResponse = apiService.sendMail(MailSendType.DRAFT, myNewOAuth, message);

			if (sendResponse.getStatusCode() == 200 || sendResponse.getStatusCode() == 204) {
				return ResponseEntity.ok().body("성공");
			} else {
				return ResponseEntity.internalServerError().body("임시저장에 실패했습니다.");
			}

		} catch (MessagingException | IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("서버 오류입니다.");
		}

	}
	
	@GetMapping("search")
	@ResponseBody
	public Map<String, String> searchMail(
			@RequestParam("empMail")String empMail
	) {
		Map<String, String> resultMap = new HashMap<>();
		EmployeeVO mailEmp = sentService.readSearchMail(empMail);
		if(mailEmp!=null) {
			resultMap.put("searchEmpName", mailEmp.getEmpName());
			resultMap.put("searchEmpMail", mailEmp.getEmpMail());
			resultMap.put("searchEmpDepartmentName", mailEmp.getPosiName());
		}
		return resultMap;
	}
}
