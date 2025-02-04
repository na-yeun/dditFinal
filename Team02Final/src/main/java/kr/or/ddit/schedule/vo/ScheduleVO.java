package kr.or.ddit.schedule.vo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import lombok.Data;

@Data
public class ScheduleVO {
	private String scheId;
	
	private String scheTitle;
	
	
	private String scheContent;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT+9")
	private LocalDateTime scheSdate;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT+9")
	private LocalDateTime scheEdate;
	private String scheStatus;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT+9")
	private LocalDateTime scheRegidate;
	private String scheLastup;
	private String schetypeId;
	private String empId;
	private String scheBcolor;
	private String scheFcolor;

	private CommonCodeVO comCode;
	private DepartmentVO depart;
	private EmployeeVO employee;
	private ScheduleColorVO scheduleColor;
}
