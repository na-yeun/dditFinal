package kr.or.ddit.message.controller;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.SimpleCondition;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.message.service.MessageBoxService;
import kr.or.ddit.message.vo.MessageBoxVO;
import kr.or.ddit.message.vo.ReceiveMessageVO;
import kr.or.ddit.security.AccountVOWrapper;

@Controller
@RequestMapping("/{companyId}/message/box")
public class MessageBoxController {
	
	@Inject
	private MessageBoxService service;
	
	public static final String MODELNAME = "box";

	@ModelAttribute(MODELNAME)
	public MessageBoxVO boxMessage() {
		return new MessageBoxVO();
	}
	
	
	
	@GetMapping
	public String boxList(
			Authentication authentication
			,@PathVariable("companyId") String companyId
			,@RequestParam(required = false, defaultValue = "1") int page
			,@ModelAttribute("condition") SimpleCondition simpleCondition
			,Model model) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}

		String empId = myEmp.getEmpId(); 
		
		PaginationInfo<MessageBoxVO> paging = new PaginationInfo<>();
		paging.setCurrentPage(page);
		paging.setSimpleCondition(simpleCondition);
		
		
		model.addAttribute("boxList",service.readBoxMessageList(paging,empId));
		PaginationRenderer renderer = new DefaultPaginationRenderer();
		model.addAttribute("pagingHTML", renderer.renderPagination(paging, "fnPaging"));
		
		
		return "message/boxList";
	}
	
	
	public void setMessageDate(MessageBoxVO box, String dateString) {
	    // 문자열 날짜 형식 설정
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

	    try {
	        // 문자열을 Date로 변환
	        Date parsedDate = formatter.parse(dateString);

	        // Date를 LocalDateTime으로 변환
	        LocalDateTime localDateTime = parsedDate.toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDateTime();

	        // box에 LocalDateTime 설정
	        box.setMbsendDate(localDateTime);
	    } catch (ParseException e) {
	        // 날짜 변환 실패 시 예외 처리
	        System.err.println("날짜 변환 오류: " + e.getMessage());
	    }
	}
	
	
	
	@ResponseBody
	@PostMapping("new")
	public  Map<String, Object> boxNew(
			Authentication authentication
			,@PathVariable("companyId") String companyId
			,Model model
			, @RequestBody Map<String, List<Map<String, String>>> requestBody
			,@ModelAttribute(MODELNAME) MessageBoxVO box
			) {
		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}

		List<Map<String, String>> messages = requestBody.get("messages");

	    if (messages == null || messages.isEmpty()) {
	        Map<String, Object> response = new HashMap<>();
	        response.put("success", false);
	        response.put("message", "선택된 메시지가 없습니다.");
	        return response;
	    }


	    // 첫 번째 메시지 데이터를 예로 box 객체 설정
	    for (Map<String, String> message : messages) {
	        box.setMboxTitle(message.get("title"));  // 제목 설정
	        box.setMboxContent(message.get("content")); // 내용 설정
	        box.setMbsendId(message.get("sendId"));  // 발신자 설정
	        box.setMboxEmergencyyn(message.get("emergency")); // 긴급 여부 설정
	     // 날짜 문자열 가져오기
	        String dateString = message.get("date");
	        if (dateString != null) {
	            setMessageDate(box, dateString); // 날짜 변환 후 설정
	        }
	        box.setMbreceiveId(myEmp.getEmpId());

	        // 서비스 호출
	        ServiceResult result = service.createBoxMessage(box);

	        if (result != ServiceResult.OK) {
	            Map<String, Object> response = new HashMap<>();
	            response.put("success", false);
	            response.put("message", "메시지 이동 중 오류가 발생했습니다.");
	            return response;
	        }
	    }

	 // 모든 메시지 처리 성공
	    Map<String, Object> response = new HashMap<>();
	    response.put("success", true);
	    response.put("message", "모든 메시지가 보관함으로 이동되었습니다.");
	    return response;
	
	}
	
	
	@PostMapping("{mboxId}/del")
	@ResponseBody
	public Map<String, Object> deleteReceivedMessages(@RequestBody Map<String, Object> request) {
		Map<String, Object> response = new HashMap<>();
	    
	    // 요청에서 mboxIds 추출
	    List<String> mboxIds = (List<String>) request.get("mboxIds");

	    // 유효성 검사: rmesIds가 비어 있거나 null인지 확인
	    if (mboxIds == null || mboxIds.isEmpty()) {
	        response.put("success", false);
	        response.put("message", "삭제할 메시지가 없습니다.");
	        return response;
	    }

	    try {
	        // rmesIds를 반복하며 삭제 로직 실행
	        for (String mboxId : mboxIds) {
	            // 서비스 레이어에서 rmesId로 메시지 삭제
	            service.removeBoxMessage(mboxId);
	        }

	        response.put("success", true);
	        response.put("message", "선택한 메시지가 성공적으로 삭제되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "삭제 중 오류가 발생했습니다.");
	    }

	    return response;
	}
	
	
}
