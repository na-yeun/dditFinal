package kr.or.ddit.employee.service;

import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.employee.vo.EmployeeVO;
@RootContextWebConfig
class EmployeeServiceImplTest {
	@Inject
	private EmployeeService service;

	@Disabled
	@Test // 성공
	void testReadEmployee() {
		AccountVO account = new AccountVO();
		account.setAccountMail("kimny950111@gmail.com");
		account.setAccountPass("123");
		service.readEmployee(account);
	}
	
	@Disabled
	@Test
	void testReadEmployeeList() {
		
	}
	
	
	@Disabled
	@Test // 성공
	void testReadEmployeeForAuth() {
		EmployeeVO emp = new EmployeeVO();
		emp.setEmpMail("kimny950111@gmail.com");
		emp.setEmpName("김나연");
		emp.setEmpPhone("01056959501");
		service.readEmployeeForAuth(emp);
	}

	
	@Disabled
	@Test // 성공
	void testCreateEmployee() {
		EmployeeVO emp = new EmployeeVO();
		emp.setEmpId("dawn");
		emp.setEmpName("새벽");
		emp.setEmpMail("mail");
		emp.setEmpJoin("20240101");
		emp.setPosiId("t");
		emp.setDepartCode("DEP005");
		emp.setEmpBirth("20240101");
		emp.setEmpGender("여");
		emp.setEmpPhone("01012345678");
	}

	@Disabled
	@Test // 성공
	void testModifyEmployee() {
		EmployeeVO emp = new EmployeeVO();
		emp.setEmpId("EMP015");
		emp.setEmpPass("test");
		service.modifyEmployee(emp);
	}

	@Disabled
	@Test // 성공
	void testModifyStatusEmployee() {
		service.modifyStatusEmployee("kimny950111@gmail.com", "S");
	}
	
	@Disabled
	@Test
	void testDeleteEmployee() {
		fail("Not yet implemented");
	}
	
}
