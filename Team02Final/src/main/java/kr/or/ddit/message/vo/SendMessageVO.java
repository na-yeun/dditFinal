package kr.or.ddit.message.vo;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode.Include;


//발신 쪽지함
@Data
public class SendMessageVO {
	
	private String smesId;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(min = 1, max = 100, groups = {InsertGroup.class, UpdateGroup.class})
	private String smesTitle;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(min = 1, max = 666, groups = {InsertGroup.class, UpdateGroup.class})
	private String smesContent;
	@DateTimeFormat(pattern = "yy-MM-dd HH:mm")
	@JsonFormat(pattern = "yy-MM-dd'T'HH:mm", timezone = "GMT+9")
	private LocalDateTime smesDate;
	private String semergencyYn;
	
	private String messendId; //데이터베이스에 넣기(발신자 아이디)
	
	private String receiverNames; // 추가 필드

	
	@ToString.Exclude
	private EmployeeVO employee;

	private List<MessageMappingVO> mappingList;
	
	@ToString.Exclude
	private DepartmentVO depart;
	
}
