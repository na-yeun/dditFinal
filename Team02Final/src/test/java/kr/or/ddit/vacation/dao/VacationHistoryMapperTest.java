package kr.or.ddit.vacation.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.commons.paging.PaginationInfo;

@RootContextWebConfig
class VacationHistoryMapperTest {
	@Inject
	private VacationHistoryMapper mapper;
	
	@Disabled
	@Test
	void testSelectTotalRecord() {
		
	}

	@Test
	void testSelectEmployeeVacationHistoryList() {
		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(1);
		Map<String, Object> variousCondition = new HashMap<>();
		
		variousCondition.put("startDate", "20220101");
		variousCondition.put("endDate", "20241210");
		
		
		paging.setVariousCondition(variousCondition);
		
		
		mapper.selectVacationHistoryList(paging);
	}

}
