package kr.or.ddit.vacation.vo;

import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.Data;

@Data
public class VacationStatusVO {
	private String vstaCode;
	private String empId;
	private double vstaAllcount;
	private double vstaAppend;
	private double vstaUse;
	private double vstaNowcount;
	private double vstaSickcount;
	
	// 사원관리 한 사원의 휴가현황에 사용 (민재) 
	private String posiName;
	private String departName;
	private Long addVacationDays;
	private EmployeeVO employee;
}
