package kr.or.ddit.survey.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.survey.vo.SurveyResultVO;

public interface SurveyResultService {
	
	/**
	 * 한 개의 설문조사 게시글의 답변 리스트
	 * @param sboardNo
	 * @return
	 */
	public  Map<String, Map<String, Object>> readSurveyResultList(String sboardNo);
	
	
	public SurveyResultVO readSurveyResult(SurveyResultVO result);
	
	/**
	 * 회원ID와 게시판 번호를 사용해서 이미 참여했는지 확인
	 * @param empId
	 * @param sboardNo
	 * @return
	 */
	public boolean readSurveyExist(String empId, String sboardNo);
	
	/**
	 * 한 개의 설문조사 답변 등록
	 * @param result
	 * @return
	 */
	public ServiceResult createSurveyResult(List<SurveyResultVO> result);
	
}
