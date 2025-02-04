package kr.or.ddit.survey.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.survey.vo.SurveyResultVO;

@RootContextWebConfig
class SurveyResultMapperTest {
	@Inject
	SurveyResultMapper mapper;

	@Test
	void testSelectSurveyResultList() {
		mapper.selectSurveyResultList("20250101001");
	}

	@Test
	void testSelectSurveyResult() {
		fail("Not yet implemented");
	}
	
	@Test
	void testSelectSurveyExist() {
		assertEquals(7, mapper.selectSurveyExist("EMP009", "20241223003"));
	}

	@Test
	void testInsertSurveyResultList() {
		SurveyResultVO result = new SurveyResultVO();
		result.setEmpId("EMP015");
		result.setSboardNo("20241224001");
		result.setSuritemNo("1");
		result.setSurquesNo("1");
		result.setResContent("콘텐트으으으");
		mapper.insertSurveyResult(result);
	}

	@Test
	void testUpdateSurveyResult() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteSurveyResult() {
		fail("Not yet implemented");
	}

}
