package kr.or.ddit.message.vo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import lombok.Data;

//수신함

@Data
public class ReceiveMessageVO {
	private String rmesId;
	private String rsendId; //데이터 베이스에서 아이디 바꾸기
	@NotBlank(groups = InsertGroup.class)
	private String rmesTitle;
	@NotBlank(groups = InsertGroup.class)
	private String rmesContent;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT+9")
	private LocalDateTime rmesDate;
	private String rmesRead;
	private String remergencyYn;
	private String rmesreceiveId;
	
	private EmployeeVO employee;
	private DepartmentVO depart;
}
