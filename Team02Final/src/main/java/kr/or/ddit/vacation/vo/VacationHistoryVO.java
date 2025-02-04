package kr.or.ddit.vacation.vo;

import lombok.Data;

@Data
public class VacationHistoryVO {
	private int rnum;
	
	private String vacNo;
	private String empId;
	private String vacCode;
	private String vacStartdate;
	private String vacEnddate;
	private String vacDocId;
	private String vacStatus;
	
	private String vacName;
	
	// 휴가이력페이지에서 사용 위해 추가 (민재) 
	private String departName;
	private String posiName;
	private String empName;

}
