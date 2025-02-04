package kr.or.ddit.survey.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import kr.or.ddit.survey.vo.SurveyItemVO;
import kr.or.ddit.survey.vo.SurveyQuestionVO;
@RootContextWebConfig
class SurveyServiceImplTest {
	@Inject
	SurveyService service;
	
	@Test
	void testReadTotalRecord() {
		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(1);
		service.readTotalRecord(paging);
	}
	
	@Test
	void testReadSurveyBoardList() {
		PaginationInfo paging = new PaginationInfo();
		paging.setCurrentPage(1);
		service.readSurveyBoardList(paging);
	}

	@Test
	void testReadSurveyBoardDetail() {
		service.readSurveyBoardDetail("20250101001");
	}

	@Test
	void testCreateSurvey() {
		SurveyBoardVO board = new SurveyBoardVO();
		board.setSurboardNm("제목테스트");
		board.setSurboardContent("내용테스트");
		board.setSurboardStdate(LocalDate.now());
		board.setSurboardEnddate(LocalDate.now());
		board.setEmpId("EMP015");
		
		List<SurveyQuestionVO> surveyQuestionList = new ArrayList<>();
		SurveyQuestionVO question1 = new SurveyQuestionVO();
		question1.setSurquesContent("객관형질문1");
		question1.setSurquesDupleyn("Y");
		question1.setSurquesOrder(0L);
		question1.setSurquesType("S_MULTI");
		
		List<SurveyItemVO>surveyItemList = new ArrayList<>();
		SurveyItemVO item1 = new SurveyItemVO();
		item1.setSuritemContent("중복1");
		item1.setSuritemIndex(0);
		SurveyItemVO item2 = new SurveyItemVO();
		item2.setSuritemContent("중복2");
		item2.setSuritemIndex(1);
		SurveyItemVO item3 = new SurveyItemVO();
		item3.setSuritemContent("중복3");
		item3.setSuritemIndex(2);
		surveyItemList.add(item1);
		surveyItemList.add(item2);
		surveyItemList.add(item3);
		
		question1.setSurveyItemList(surveyItemList);
		
		service.createSurvey(board);
	}

	@Test
	void testModifySurveyBoard() {
		fail("Not yet implemented");
	}

}
