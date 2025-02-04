package kr.or.ddit.attendance.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.attendance.vo.AttendanceHistoryVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RootContextWebConfig
class AttendanceHistoryMapperTest {
	@Inject
    private AttendanceHistoryMapper mapper;
	
	@Disabled
	@Test
	void testSelectTotalRecord() {
		
	}
	
	@Disabled
	@Test
	void testInsertAttendanceHistory() {
		fail("Not yet implemented");
	}
	
	@Disabled
	@Test
	void testUpdateAttendanceHistory() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void testDeleteAttendanceHistory() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectAttendanceHistoryList() {
		PaginationInfo paging = new PaginationInfo();
		
		Map<String, Object> variousCondition = new HashMap<>();
		variousCondition.put("empId", "EMP015");
		
		paging.setCurrentPage(1);
		paging.setVariousCondition(variousCondition);
		
		mapper.selectAttendanceHistoryList(paging);
		
		
	}

}
