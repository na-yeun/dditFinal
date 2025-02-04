package kr.or.ddit.attendance.vo;

import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.Data;

@Data
public class AttendanceHistoryVO {
	private int rnum;
	
	private String atthisId;
	private String empId;
	private String hahisTime; // 출근시간
	private String hleaveTime; // 퇴근시간
	private String atthisCause; // 지각결근사유
	private Long atthisOver; // 초과근무시간
	private String atthisOverYn; // 초과근무여부
	private String attstaIdIn; // 출근관련상태코드
	private String attstaIdOut; // 퇴근관련상태코드
	private String attendId; // 출퇴근시간 관련 코드
	
	private String inStatusName; // in 상태이름
	private String outStatusName; // out 상태이름
	private String earlyLeaveCause; // 조퇴사유 
	private EmployeeVO emp;
	private AttendTbVO attendTb;
	
	
	
	// 근태 상태 리스트에서 부서명 직급명 출력하기 위해 추가 (민재)
	private String posiName;
	private String departName;
	private String attendTime;
	private String leaverTime;
}
