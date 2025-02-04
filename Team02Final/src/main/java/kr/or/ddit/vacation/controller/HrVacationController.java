package kr.or.ddit.vacation.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.vacation.service.VacationHistoryService;
import kr.or.ddit.vacation.service.VacationStatusService;
import kr.or.ddit.vacation.vo.VacationDTO;
import kr.or.ddit.vacation.vo.VacationHistoryVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/hr/vacation")
public class HrVacationController {
	
	@Inject
	private VacationHistoryService service;
	
	@Inject
	private VacationStatusService vacationStatusService;
	
	@GetMapping
	public String goHrVacaList(
		  @PathVariable("companyId") String companyId
		, Optional<Integer> page
		, @RequestParam(value="startDate", required = false) String startDate
		, @RequestParam(value="endDate", required = false)String endDate
		, @RequestParam(value="searchWord", required = false) String searchWord
		, Model model
	) {
	
		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(page.orElse(1));
		Map<String, Object> variousCondition = new HashMap<>();
		
		if(!(StringUtils.isBlank(startDate)&&StringUtils.isBlank(endDate))) {
			variousCondition.put("startDate", startDate);
			variousCondition.put("endDate", endDate);
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
		}
		if (StringUtils.isNotBlank(searchWord)) {
	        variousCondition.put("searchWord", searchWord);
	        model.addAttribute("searchWord", searchWord);
	    }
		
		
		paging.setVariousCondition(variousCondition);
		
		
		List<VacationHistoryVO> vacationHistoryList = service.readAllVacationHistoryList(paging);
		
		PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");
		log.info(pagingHtml);
		model.addAttribute("list", vacationHistoryList);
		model.addAttribute("pagingHtml", pagingHtml);
		model.addAttribute("variousCondition", variousCondition);
		
		return "vacation/hrVacationList";
	}
	
	// 휴가 새로 부여(post)
	@PostMapping("manage")
	@ResponseBody
	public Map<String, Object> postVacation(
			@RequestBody VacationDTO vacation
	) {
		Map<String, Object> returnResult = new HashMap<>();
		Set<String> failEmps = new HashSet<>();
		
		// 해당 조건으로 올해의 데이터가 있는지 없는지 확인
		
		ServiceResult result = vacationStatusService.createVacationStatus(vacation, failEmps);
		
		switch (result) {
			case OK:
				if(failEmps.size()>0) {
					// 실패한 애가 있음..
					returnResult.put("status", "warning");
					returnResult.put("message", "일부 실패");
					returnResult.put("failSet", failEmps);
				} else {
					// 완벽하게 다 성공
					returnResult.put("status", "success");
					returnResult.put("message", "전체 성공");
				}
				break;
			case PKDUPLICATED:
				returnResult.put("status", "error");
				returnResult.put("message", "PK 중복 오류");
				break;
			default: // SERVERERROR 포함
				returnResult.put("status", "error");
				returnResult.put("message", "서버 오류");
				break;
		}
		
		return returnResult;
	}
	
	// 휴가 추가 등록(put)
	@PutMapping("manage")
	@ResponseBody
	public Map<String, Object> putVacation(
			@RequestBody VacationDTO vacation
	) {
		Map<String, Object> returnResult = new HashMap<>();
		Set<String> failEmps = new HashSet<>();
		
		ServiceResult result = vacationStatusService.modifyVacationStatus(vacation, failEmps);
		
		switch (result) {
			case OK:
				if(failEmps.size()>0) {
					// 실패한 애가 있음..
					returnResult.put("status", "warning");
					returnResult.put("message", "일부 실패");
					returnResult.put("failSet", failEmps);
				} else {
					// 완벽하게 다 성공
					returnResult.put("status", "success");
					returnResult.put("message", "전체 성공");
				}
				break;
			case PKDUPLICATED:
				returnResult.put("status", "error");
				returnResult.put("message", "PK 중복 오류");
				break;
			default: // SERVERERROR 포함
				returnResult.put("status", "error");
				returnResult.put("message", "서버 오류");
				break;
		}
		return returnResult;
	}
	
	
		
		
	
}
