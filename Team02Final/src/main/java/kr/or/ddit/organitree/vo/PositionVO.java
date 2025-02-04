package kr.or.ddit.organitree.vo;

import java.util.List;

import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.Data;

@Data
public class PositionVO {
	
	private String posiId;
	private String posiName;
	
	
	private List<EmployeeVO> employeeList;
}
