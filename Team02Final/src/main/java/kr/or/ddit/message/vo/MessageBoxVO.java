package kr.or.ddit.message.vo;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import lombok.Data;

//쪽지 보관함(수신만)
@Data
public class MessageBoxVO {
	private String mboxId;
	private String mbsendId;
	private String mbreceiveId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT+9")
	private LocalDateTime mbsendDate;
	private String mboxTitle;
	private String mboxContent;
	private String mboxEmergencyyn;
	
	private EmployeeVO employee;
	private DepartmentVO depart;
}
