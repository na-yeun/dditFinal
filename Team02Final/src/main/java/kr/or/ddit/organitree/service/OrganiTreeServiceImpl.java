package kr.or.ddit.organitree.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.dao.OrganiTreeMapper;
import kr.or.ddit.organitree.vo.DepartmentVO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrganiTreeServiceImpl implements OrganiTreeService {
	
	
	private final OrganiTreeMapper dao;
	
	@Override
	public List<DepartmentVO> readOrganiList() {		
		return dao.selectOrganiList();
	}

	@Override
	public List<EmployeeVO> readOneOrganiList(String departCode) {
		
		return dao.selectOneOrganiList(departCode);
	}

	@Override
	public EmployeeVO readOneEmployeeDetail(String empId) {
		
		return dao.selectOneEmployeeDetail(empId);
	}

	@Override
	public List<EmployeeVO> searchOneEmployee(String empName) {
		List<EmployeeVO> list = dao.searchOneEmployeeDetail(empName);
		return list ;
	}

	@Override
	public List<DepartmentVO> readDirectory() {	
		
		return dao.selectDirectory();
	}

	@Override
	public DepartmentVO readOneDepartment(String departCode) {
		
		return dao.selectOneDepart(departCode);
	}

	@Override
	public int createOneDepartment(DepartmentVO department) {
		String departName = department.getDepartName().trim();
		if(StringUtils.isBlank(departName)) {
			return 0;
		}else {
			int rowcnt = dao.selectDepartNameCheck(departName);
//			if(rowcnt == 0) {
//				return dao.insertOneDepart(department);
//			}else {
//				return 0;
//			}
			return rowcnt == 0 ? dao.insertOneDepart(department) : 0;
			
			
		}
		
		
			
	}

	@Override
	public int modifyOneDepartment(DepartmentVO department) {
		String departName = department.getDepartName().trim();
		if(StringUtils.isBlank(departName)) {
			return 0;
		}else {
			int rowcnt = dao.selectDepartNameCheck(departName);
			return rowcnt == 0 ? dao.updateOneDepart(department) : 0;
		}
		
	}

	@Override
	public ServiceResult removeOneDepartment(String departCode) {
		List<EmployeeVO> empList = dao.selectOneOrganiList(departCode);
		if(empList != null && !empList.isEmpty()) {
			return ServiceResult.FAIL;  // 소속직원이 있는지 검사 
		}
		List<DepartmentVO> departlist = dao.selectChildDepList(departCode);
		if(departlist != null && !departlist.isEmpty()) {
			return ServiceResult.FAIL; // 하위 부서가 있는지 검사 
		}
		int result = dao.deleteOneDepart(departCode);
		if(result > 0) { 
			return ServiceResult.OK;  // 통과 하면 ok 
		}else {
			return ServiceResult.FAIL; // 실패하면 fail 
		}
		
	}
		
		
		


	
	
	

}
