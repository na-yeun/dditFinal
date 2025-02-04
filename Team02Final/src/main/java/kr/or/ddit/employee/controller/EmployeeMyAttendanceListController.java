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
import kr.or.ddit.attendance.service.AttendanceHistoryService;
import kr.or.ddit.attendance.vo.AttendanceHistoryVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.commons.paging.renderer.DefaultPaginationRenderer;
import kr.or.ddit.commons.paging.renderer.PaginationRenderer;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/{companyId}/mypage")
public class EmployeeMyAttendanceListController {
	@Inject
	private AttendanceHistoryService service;
	

	// 나의 근태 조회 페이지로 이동
	@GetMapping("myAttendance")
	public String getAttendance(
			Authentication authentication
			, Optional<Integer> page
			, @RequestParam(value="startDate", required = false) String startDate
			, @RequestParam(value="endDate", required = false)String endDate
			, Model model) {
		
		AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
		AccountVO account = principal.getAccount();
		
		EmployeeVO myEmp = (EmployeeVO)account;	
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
		
		
		List<AttendanceHistoryVO> attendanceHistory = service.readAttendanceHistoryList(paging);

		PaginationRenderer renderer = new DefaultPaginationRenderer();
		String pagingHtml = renderer.renderPagination(paging, "fnPaging");
		log.info(pagingHtml);
		model.addAttribute("list", attendanceHistory);
		model.addAttribute("pagingHtml", pagingHtml);
		model.addAttribute("variousCondition", variousCondition);
		
		return "employee/employeeAttendancePage";
		
	
	}

	

}
