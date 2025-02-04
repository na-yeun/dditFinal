package kr.or.ddit.survey.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.survey.vo.SurveyBoardVO;
import kr.or.ddit.survey.vo.SurveyItemVO;
import kr.or.ddit.survey.vo.SurveyQuestionVO;

@Mapper
public interface SurveyMapper {
	/**
	 * 페이징 처리를 위해 record 수를 카운트
	 * @param paging
	 * @return
	 */
	public int selectTotalRecord(PaginationInfo paging);
	
	/**
	 * 설문조사 리스트를 가지고 오는 메소드
	 * @param paging
	 * @return
	 */
	public List<SurveyBoardVO> selectSurveyBoardList(PaginationInfo paging);
	
	/**
	 * 설문조사 한 건의 디테일을 가지고 오는 메소드
	 * @param sboardNo
	 * @return
	 */
	public SurveyBoardVO selectSurveyBoardDetail(@Param("sboardNo")String sboardNo);
	
	/**
	 * 설문조사 게시글 한 건 등록
	 * @param board
	 * @return
	 */
	public int insertSurveyBoard(SurveyBoardVO board);
	
	/**
	 * 설문조사 게시글 한 건 내의 문항 등록
	 * @param question
	 * @return
	 */
	public int insertSurveyQuestion(SurveyQuestionVO question);
	
	/**
	 * 설문조사 게시글 한 건 내의 문항에 해당하는 항목 등록
	 * @param item
	 * @return
	 */
	public int insertSurveyItem(SurveyItemVO item);
	
	/**
	 * 설문조사 한 건 수정
	 * @param board
	 * @return
	 */
	public int updateSurveyBoard(SurveyBoardVO board);
	
	/**
	 * 설문조사 게시글 한 건 내의 문항이 있는지 없는지..
	 * @param board
	 * @return
	 */
	public int selectSurveyQuestionCount(SurveyQuestionVO question);
	
	/**
	 * 설문조사 게시글 한 건 내의 문항 수정
	 * @param board
	 * @return
	 */
	public int updateSurveyQuestion(SurveyQuestionVO question);
	
	/**
	 * 설문조사 게시글 한 건 내의 문항에 해당하는 항목이 있는지 없는지..
	 * @param board
	 * @return
	 */
	public int selectSurveyItemCount(SurveyItemVO item);
	
	/**
	 * 설문조사 게시글 한 건 내의 문항에 해당하는 항목 수정
	 * @param board
	 * @return
	 */
	public int updateSurveyItem(SurveyItemVO item);
	
	/**
	 * 설문조사 게시글 한 건 삭제
	 * @param sboardNo
	 * @return
	 */
	public int deleteSurveyBoard(@Param("sboardNo")String sboardNo);
	
	/**
	 * 게시글 한 개의 모든 질문 삭제
	 * @param sboardNo
	 * @return
	 */
	public int deleteSurveyQuestionAll(@Param("sboardNo")String sboardNo);
	
	/**
	 * 게시글 한 개의 모든 질문의 항목 삭제
	 * @param sboardNo
	 * @return
	 */
	public int deleteSurveyItemAll(@Param("sboardNo")String sboardNo);
	
	
	/**
	 * 업데이트시 기존의 질문을 삭제했을 경우
	 * @param surquesNo
	 * @return
	 */
	public int deleteSurveyQuestion(@Param("surquesNo")String surquesNo);
	
	/**
	 * 업데이트시 기존의 질문을 삭제했을 경우 해당 질문과 연관된 모든 item 삭제
	 * @param surquesNo
	 * @return
	 */
	public int deleteSurveyItemsWithQuestion(@Param("surquesNo")String surquesNo);
	
	
	/**
	 * 업데이트시 기존의 항목을 삭제했을 경우
	 * @param suritemNo
	 * @return
	 */
	public int deleteSurveyItem(@Param("suritemNo")String suritemNo);
	
}
