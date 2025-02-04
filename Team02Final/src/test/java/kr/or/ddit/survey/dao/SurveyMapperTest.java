package kr.or.ddit.survey.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import lombok.extern.slf4j.Slf4j;

@RootContextWebConfig
@Slf4j
class SurveyMapperTest {
	@Inject
	SurveyMapper mapper;

	@Test
	void testSelectTotalRecord() {
		PaginationInfo paging = new PaginationInfo();
		assertEquals(1,mapper.selectTotalRecord(paging));
	}

	@Test
	void testSelectSurveyBoardList() {
		PaginationInfo paging = new PaginationInfo();
		int totalRecord = mapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		paging.setCurrentPage(1);
		log.info("{}",totalRecord);
		log.info("{}",paging);
		mapper.selectSurveyBoardList(paging);
	}

	@Test
	void testSelectSurveyBoardDetail() {
		mapper.selectSurveyBoardDetail("20241223001");
	}

	@Test
	void testInsertSurveyBoard() {
		SurveyBoardVO board = new SurveyBoardVO();
		board.setEmpId("EMP015");
		board.setSurboardContent("테스트용 본문");
		board.setSurboardNm("테스트용 제목3");
		board.setSurboardYn("Y");
		board.setSurboardStdate(LocalDate.of(2024, 12, 26));
		board.setSurboardEnddate(LocalDate.of(2024, 12, 28));
		mapper.insertSurveyBoard(board);
	}

	@Test
	void testUpdateSurveyBoard() {
		SurveyBoardVO board = new SurveyBoardVO();
		board.setSboardNo("20241223004");
		mapper.updateSurveyBoard(board);
	}

	

}
