package kr.or.ddit.survey.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.survey.vo.SurveyResultVO;

@Mapper
public interface SurveyResultMapper {
	/**
	 * 한 개의 설문조사 게시글의 답변 리스트
	 * @param sboardNo
	 * @return
	 */
	public List<SurveyResultVO> selectSurveyResultList(@Param("sboardNo")String sboardNo);
	
	
	public SurveyResultVO selectSurveyResult(SurveyResultVO result);
	
	/**
	 * 회원ID와 게시판 번호를 사용해서 이미 참여했는지 확인
	 * @param empId
	 * @param sboardNo
	 * @return
	 */
	public int selectSurveyExist(@Param("empId")String empId, @Param("sboardNo")String sboardNo);
	
	/**
	 * 한 개의 설문조사 답변 등록
	 * @param result
	 * @return
	 */
	public int insertSurveyResult(SurveyResultVO result);
	
	/**
	 * 한 개의 설문조사에 달린 답변 전체 삭제
	 * @param sboardNo
	 * @return
	 */
	public int deleteSurveyResult(@Param("sboardNo")String sboardNo);	

}
