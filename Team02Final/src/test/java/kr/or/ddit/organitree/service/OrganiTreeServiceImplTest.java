package kr.or.ddit.organitree.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.organitree.dao.OrganiTreeMapper;
import kr.or.ddit.organitree.vo.DepartmentVO;

@RootContextWebConfig
class OrganiTreeServiceImplTest {
	
	@Autowired
	private OrganiTreeMapper mapper;
	
	@Disabled
	@Test
	void testReadOrganiList() {
		assertDoesNotThrow(()-> {
			mapper.selectOrganiList();
		});
	}
	@Disabled
	@Test
	void testReadOneOrganiList() {
		String departCode = "DEP001"; 
		mapper.selectOneOrganiList(departCode);
	}
	@Disabled
	@Test
	void testReadOneEmployeeDetail() {
		String empId = "EMP011";
		mapper.selectOneEmployeeDetail(empId);
	}
	@Disabled
	@Test
	void testSearchOneEmployee() {
//		String empName = "유민재";   ==> 두명 조회 O
//		mapper.searchOneEmployeeDetail(empName);
		
		String empName = "김나연"; // ==> 한명 조회 O 
		mapper.searchOneEmployeeDetail(empName);
	}
	@Disabled
	@Test
	void testReadDirectory() {
		fail("Not yet implemented");
	}
	@Disabled
	@Test
	void testReadOneDepartment() {
		String departCode = "DEP001";  // ==> 인사부 출력 
		mapper.selectOneDepart(departCode);
	}
	@Disabled
	@Test
	void testCreateOneDepartment() {
		DepartmentVO dept = new DepartmentVO();
		dept.setDepartParentcode("DEP000");
		dept.setContractId("a001");
		dept.setDepartName("민재부");
		
		mapper.insertOneDepart(dept);
		
	}
	@Disabled
	@Test
	void testModifyOneDepartment() {
		DepartmentVO dept = new DepartmentVO();
		dept.setDepartCode("DEP029");
		dept.setDepartName("유민재");
		mapper.updateOneDepart(dept);
	}
	@Disabled
	@Test
	void testRemoveOneDepartment() {
		String departCode = "DEP029";
		mapper.deleteOneDepart(departCode);
	}
	@Disabled
	@Test
	void testOrganiTreeServiceImpl() {
		fail("Not yet implemented");
	}

}
