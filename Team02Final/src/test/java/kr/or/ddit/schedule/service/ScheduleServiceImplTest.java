package kr.or.ddit.schedule.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.annotation.RootContextWebConfig;

@Transactional
@RootContextWebConfig
class ScheduleServiceImplTest {

	@Inject
	private ScheduleService service;
	
	
	@Test
	void testReadScheduleList() {
		
		/*
		 * EmployeeVO emp = new EmployeeVO(); ScheduleVO schedule = new ScheduleVO();
		 * String schetypeId = schedule.getSchetypeId(); String empId = emp.getEmpId();
		 * String departCode = emp.getDepartCode();
		 */
		
		assertDoesNotThrow(()->service.readScheduleList("3","DEP005", "EMP013"));
	}

	@Test
	void testReadScheduleOne() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateSchedule() {
		
	}

	@Test
	void testModifySchedule() {
		fail("Not yet implemented");
	}

	@Test
	void testRemoveSchedule() {
		fail("Not yet implemented");
	}

}
