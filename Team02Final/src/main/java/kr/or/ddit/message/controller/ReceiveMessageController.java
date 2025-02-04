package kr.or.ddit.message.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import kr.or.ddit.message.service.ReceiveMessageService;
import kr.or.ddit.message.vo.ReceiveMessageVO;
import kr.or.ddit.security.AccountVOWrapper;

@Controller
@RequestMapping("/{companyId}/message/receive")
public class ReceiveMessageController {

	@Inject
	private ReceiveMessageService service;

	public static final String MODELNAME = "receiveMessage";

	@ModelAttribute(MODELNAME)
	public ReceiveMessageVO receiveMessage() {
		return new ReceiveMessageVO();
	}

	@GetMapping
	public String getReceiveList(Authentication authentication, @PathVariable("companyId") String companyId,
			@RequestParam(required = false, defaultValue = "1") int page,
			@ModelAttribute("condition") SimpleCondition simpleCondition, Model model) {
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		EmployeeVO myEmp = (EmployeeVO) account;

		if (myEmp == null) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}

		String empId = myEmp.getEmpId();

		PaginationInfo<ReceiveMessageVO> paging = new PaginationInfo<>();
		paging.setCurrentPage(page);
		paging.setSimpleCondition(simpleCondition);

		model.addAttribute("receiveList", service.readReceiveMessageList(paging, empId));
		PaginationRenderer renderer = new DefaultPaginationRenderer();
		model.addAttribute("pagingHTML", renderer.renderPagination(paging, "fnPaging"));

		return "message/receiveList";
	}

	@PostMapping("up")
	@ResponseBody
	public ResponseEntity<?> updateReadStatus(@RequestBody Map<String, String> requestData) {
		String rmesId = requestData.get("rmesId");
		HashMap<String, Object> response = new HashMap<>();
		// DB 업데이트 실행
		ServiceResult result = service.modifyReceiveMessage(rmesId);
		
		if (result == ServiceResult.OK) { 
	        response.put("success", true);
	        return ResponseEntity.ok(response);
	    } else { // 실패한 경우
	        response.put("success", false);
	        response.put("message", "업데이트 실패");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
		
	}

	@PostMapping("{rmesId}/del")
	@ResponseBody
	public Map<String, Object> deleteReceivedMessages(@RequestBody Map<String, Object> request) {

		Map<String, Object> response = new HashMap<>();
		// 요청에서 rmesIds 추출
		List<String> rmesIds = (List<String>) request.get("rmesIds");

		// 유효성 검사: rmesIds가 비어 있거나 null인지 확인
		if (rmesIds == null || rmesIds.isEmpty()) {
			response.put("success", false);
			response.put("message", "삭제할 메시지가 없습니다.");
			return response;
		}

		try {
			// rmesIds를 반복하며 삭제 로직 실행
			for (String rmesId : rmesIds) {
				// 서비스 레이어에서 rmesId로 메시지 삭제
				service.removeReceiveMessage(rmesId);
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
