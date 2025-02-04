package kr.or.ddit.gmail.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.vo.MailDeleteVO;

@Mapper
public interface MailDeleteMapper {
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
	public String selectLastApiCallTime(String empMail);
	
	
	/**
	 * 한 명의 삭제 메일함 가지고 오기
	 * @return
	 */
	public List<MailDeleteVO> selectDeleteMailList(PaginationInfo paging);
	
	/**
	 * 메일 한 개의 상세 정보 가지고 오기
	 * @param messageId
	 * @return
	 */
	public MailDeleteVO selectDeleteMailDetail(String messageId);
	
	
	/**
	 * 삭제메일을 추가하기 전, data base에 존재하는 메일이 있는지 확인
	 * @param receivedMail
	 * @return
	 */
	public int selectDeleteMailExist(MailDeleteVO deleteMail);
	
	
	/** 
	 * 삭제 메일함 추가(data base 및 api처리)
	 * @return
	 */
	public int insertDeleteMail(MailDeleteVO deleteMail);
	
	/**
	 * 삭제 메일 영구삭제(data base 및 api 처리)
	 * @param rmailMessageid
	 * @return
	 */
	public int deleteDeleteMail(@Param("mailMessageid")String mailMessageid);

}
