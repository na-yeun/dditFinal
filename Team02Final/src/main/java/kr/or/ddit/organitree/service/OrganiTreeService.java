package kr.or.ddit.organitree.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;

public interface OrganiTreeService {
		
	public List<DepartmentVO> readOrganiList();
	
	public List<EmployeeVO> readOneOrganiList(String departCode);
	
	public EmployeeVO readOneEmployeeDetail(String empId);
	
	public List<EmployeeVO> searchOneEmployee(String empName);
	
	public List<DepartmentVO> readDirectory();
	
	public DepartmentVO readOneDepartment(String departCode);
	
	public int createOneDepartment(DepartmentVO department);
	
	public int modifyOneDepartment(DepartmentVO department);
	
	public ServiceResult removeOneDepartment(String departCode);
	
	
	
}
