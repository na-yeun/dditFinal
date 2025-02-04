package kr.or.ddit.survey.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.survey.dao.SurveyResultMapper;
import kr.or.ddit.survey.vo.SurveyResultVO;
import lombok.extern.slf4j.Slf4j;

@RootContextWebConfig
@Slf4j
class SurveyResultServiceImplTest {
	@Inject
	SurveyResultServiceImpl service;
	@Inject
	SurveyResultMapper resultMapper;

	@Test
	void test1() {
		List<SurveyResultVO> resultList = resultMapper.selectSurveyResultList("20250101001");
		for(SurveyResultVO result : resultList) {
			String type = result.getSurquesType();
			if(type.equals("S_SUBJ")){
				log.info(result.getTotalResult());
			}
		}
	}
	
	@Test
	void testReadSurveyResultList() {
		service.readSurveyResultList("20250101001");
	}

	@Test
	void testReadSurveyResult() {
		fail("Not yet implemented");
	}

	@Test
	void testReadSurveyExist() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateSurveyResult() {
		fail("Not yet implemented");
	}

}
