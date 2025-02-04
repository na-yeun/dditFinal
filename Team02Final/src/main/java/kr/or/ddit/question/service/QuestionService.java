package kr.or.ddit.question.service;

import java.util.List;

import kr.or.ddit.atch.vo.AtchFileDetailVO;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.question.vo.CategoryVO;
import kr.or.ddit.question.vo.QuestionVO;

public interface QuestionService {
	
	/**
	 * 페이징 처리를 위해 record 수를 카운트
	 * @param paging
	 * @return 없으면 0 반환
	 */
	public int readTotalRecord(PaginationInfo paging);
	
	/**
	 * 문의 게시글 등록때 사용할 카테고리 읽어오기
	 * @return
	 */
	public List<CategoryVO> readCategoryList();
	
	/**
	 * 문의 게시글 상세 조회
	 * @param quNo
	 * @return QuestionVO
	 */
	public QuestionVO readQuestion(String quNo);
	
	
	/**
	 * 문의 게시글 리스트 조회 - 페이징 처리 O
	 * @param paginationInfo
	 * @return
	 */
	public List<QuestionVO> readQuestionList(PaginationInfo paginationInfo);
	
	/**
	 * 문의 게시글 등록
	 * @param questionVO
	 * @return 
	 */
	public ServiceResult createQuestion(QuestionVO questionVO);
	
	/**
	 * 문의 게시글 수정
	 * @param questionVO
	 * @return OK, FAIL
	 */
	public ServiceResult modifyQuestion(QuestionVO questionVO);
	
	/**
	 * 문의 게시글 삭제
	 * @param quNo
	 * @return OK, FAIL
	 */
	public ServiceResult removeQuestion(String quNo);
	
	/**
	 * 파일 다운로드
	 * @param atchFileId
	 * @param fileSn
	 * @return
	 */
	public AtchFileDetailVO download(int atchFileId, int fileSn);
	
	/**
	 * 파일 한건 삭제
	 * @param atchFileId
	 * @param fileSn
	 */
	public void removeFile(int atchFileId, int fileSn);
	
	/**
	 * 답변 수정
	 * @param question
	 * @return
	 */
	public ServiceResult updateAnswer(QuestionVO question);
	
	/**
	 * 답변 삭제
	 * @param question
	 * @return
	 */
	public ServiceResult deleteAnswer(QuestionVO question);
	
	
}
