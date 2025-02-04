package kr.or.ddit.question.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.question.vo.CategoryVO;
import kr.or.ddit.question.vo.QuestionVO;

@Mapper
public interface QuestionMapper {
	
	public List<CategoryVO> selectCategoryList();
	
	/**
	 * 문의 게시글 상세 조회
	 * @param quNo
	 * @return QuestionVO
	 */
	public QuestionVO selectQuestion(String quNo);
	
	/**
	 * 문의 게시글 리스트 조회 - 페이징 처리 X
	 * @return List - QuestionVO
	 */
	public List<QuestionVO> selectQuestionListNonPaging();
	
	/**
	 * 문의 게시글 리스트 조회 - 페이징 처리 O
	 * @param paging
	 * @return
	 */
	public List<QuestionVO> selectQuestionList(PaginationInfo paging);
	
	/**
	 * 페이징 처리를 위한 검색 결과 레코드 수 조회
	 * @param paging
	 * @return
	 */
	public int selectTotalRecord(PaginationInfo<QuestionVO> paging);
	
	/**
	 * 문의 게시글 등록
	 * @param questionVO
	 * @return int
	 */
	public int insertQuestion(QuestionVO questionVO);
	
	/**
	 * 문의 게시글 수정
	 * @param questionVO
	 * @return int
	 */
	public int updateQuestion(QuestionVO questionVO);
	
	/**
	 * 문의 게시글 삭제
	 * @param quNo
	 * @return int
	 */
	public int deleteQuestion(String quNo);

	/**
	 * 답변 수정
	 * @param question
	 * @return
	 */
	public int updateAnswer(QuestionVO question);
	
	/**
	 * 답변 삭제
	 * @param question
	 * @return
	 */
	public int deleteAnswer(QuestionVO question);
	

}
