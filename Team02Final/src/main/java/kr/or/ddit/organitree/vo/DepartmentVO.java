package kr.or.ddit.organitree.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import lombok.Data;

@Data
public class DepartmentVO {
	
	
	private String departCode;
	@NotBlank(groups = InsertGroup.class)
	private String departParentcode;
	private String contractId;
	@NotBlank(groups = {InsertGroup.class,UpdateGroup.class} )
	private String departName;
	@NotBlank
	private String departStatus;
	private String departHead;
	
	private List<EmployeeVO> employeeList;
	private List<CommonCodeVO> commonList;
	private String posiName;
}
