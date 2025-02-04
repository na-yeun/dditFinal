package kr.or.ddit.message.vo;

import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.Data;
import lombok.ToString;

//수신자 맵핑 테이블(단일/다중해도 다 들어가는 것)

@Data
public class MessageMappingVO {
	private Long mapId;
	private String receiveId;
	private String smesId;
	private String mesReceread;
	
	@ToString.Exclude
	private SendMessageVO sendMessage;
	private EmployeeVO employee;
}
