package kr.or.ddit.statistics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("{companyId}/statistics")
@Controller
public class StatisticsController {
	
	@GetMapping("emp")
	public String getEmpStatistics() {
		return "statistics/empStatistics";
	}

	@GetMapping("attendance")
	public String getAttendanceStatistics() {
		return "statistics/attendanceStatistics";
	}
	
	@GetMapping("vacation")
	public String getVacationStatistics() {
		return "statistics/vacationStatistics";
	}
}
