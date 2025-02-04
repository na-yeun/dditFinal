package kr.or.ddit.gmail.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.vo.MailDraftVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;

@Mapper
public interface MailDraftMapper {
	/**
	 * 페이징 처리를 위해 record 수를 카운트
	 * @param paging
	 * @return
	 */
	public int selectTotalRecord(PaginationInfo paging);
	
	
	/**
	 * 한 명의 계정으로 마지막 api 요청을 보낸 시간
	 * 
	 * @param rmailAccount
	 * @return
	 */
	public String selectLastApiCallTime(@Param("empMail")String empMail);
	
	
	public MailDraftVO selectDraftMailDetail(@Param("draftId")String draftId);
	/**
	 * 한 명의 임시저장함 가지고 오기
	 * @return
	 */
	public List<MailDraftVO> selectDraftMailList(PaginationInfo paging);
	
	/**
	 * 임시저장 메일을 추가하기 전, data base에 존재하는 메일이 있는지 확인
	 * @param receivedMail
	 * @return
	 */
	public int selectDraftMailExist(MailDraftVO draftMail);
	
	/** 
	 * 임시 저장 메일함 추가(data base 및 api처리)
	 * @return
	 */
	public int insertDraftMail(MailDraftVO draftMail);
	
	/**
	 * 임시 저장 메일 삭제(data base 및 api 처리)
	 * @param rmailMessageid
	 * @return
	 */
	public int deleteDraftMail(@Param("draftId")String draftId);


}
