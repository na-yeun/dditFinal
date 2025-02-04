package kr.or.ddit.employee.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import kr.or.ddit.vacation.service.VacationHistoryService;
import kr.or.ddit.vacation.service.VacationStatusService;
import kr.or.ddit.vacation.vo.VacationHistoryVO;
import kr.or.ddit.vacation.vo.VacationStatusVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/mypage")
public class EmployeeMyVacationListController {
	@Inject
	private VacationStatusService statusService;
	@Inject
	private VacationHistoryService historyService;
	

	// 나의 휴가 조회 페이지로 이동
	@GetMapping("myVacation")
	public String getMyVacation(
			Authentication authentication
			, Optional<Integer> page
			, @RequestParam(value="startDate", required = false) String startDate
			, @RequestParam(value="endDate", required = false)String endDate
			, Model model) {
		
		// 1) session에 담긴 나의 정보를 가지고 현재 휴가 근황 조회
		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;	
		
		
		VacationStatusVO vacationStatus = statusService.readVacationStatusThisYear(myEmp.getEmpId());

		if (vacationStatus != null) {
			model.addAttribute("vacationStatus", vacationStatus);
		}
		
		
		// 2) 나의 휴가 리스트 조회
		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(page.orElse(1));
		Map<String, Object> variousCondition = new HashMap<>();
		variousCondition.put("empId", myEmp.getEmpId());
		
		if(!(StringUtils.isBlank(startDate)&&StringUtils.isBlank(endDate))) {
			variousCondition.put("startDate", startDate);
			variousCondition.put("endDate", endDate);
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
		}
		
		paging.setVariousCondition(variousCondition);
		
		
		List<VacationHistoryVO> vacationHistoryList = historyService.readVacationHistoryList(paging);

		PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");

		model.addAttribute("list", vacationHistoryList);
		model.addAttribute("pagingHtml", pagingHtml);
		model.addAttribute("variousCondition", variousCondition);
		
		return "employee/employeeVacationPage";
	}

	

}
