package kr.or.ddit.survey.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import kr.or.ddit.survey.vo.SurveyItemVO;
import kr.or.ddit.survey.vo.SurveyQuestionVO;

public interface SurveyService {
	/**
	 * 페이징 처리를 위해 record 수를 카운트
	 * @param paging
	 * @return 없으면 0 반환
	 */
	public int readTotalRecord(PaginationInfo paging);
	
	/**
	 * 설문조사 리스트를 가지고 오는 메소드
	 * @param paging
	 * @return 없으면 빈 배열 반환
	 */
	public List<SurveyBoardVO> readSurveyBoardList(PaginationInfo paging);
	
	/**
	 * 설문조사 한 건의 디테일을 가지고 오는 메소드
	 * @param sboardNo
	 * @return 없으면 null 반환
	 */
	public SurveyBoardVO readSurveyBoardDetail(@Param("sboardNo")String sboardNo);
	
	
	/**
	 * 설문조사 게시글 한 건 등록
	 * @param board(게시글 한 건에 해당하는 질문과 항목 포함)
	 * @return
	 */
	public ServiceResult createSurvey(SurveyBoardVO board);
	
	
	/**
	 * 설문조사 게시글 한 건 수정 가능 확인
	 * @param boardDetail
	 * @return
	 */
	public boolean checkUpdate(SurveyBoardVO boardDetail);

	/**
	 * 설문조사 한 건 수정
	 * @param board
	 * @return 
	 */
	public ServiceResult modifySurveyBoard(SurveyBoardVO board);

	/**
	 * 설문조사 한 건 삭제
	 * @param board
	 * @return
	 */
	public ServiceResult deleteSurveyBoard(String sboardNo);
}
