package kr.or.ddit.gmail.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.gmail.vo.MailSentVO;
@Mapper
public interface MailSentMapper {
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
	
	
	/**
	 * 메일 한 개의 상세 정보 가지고 오기
	 * @param messageId
	 * @return 없으면 null 반환
	 */
	public MailSentVO selectSentMailDetail(@Param("mailMessageId")String mailMessageId);

	/**
	 * 한 명의 보낸 메일함 가지고 오기
	 * @return
	 */
	public List<MailSentVO> selectSentMailList(PaginationInfo paging);
	
	/**
	 * 보낸 메일을 추가하기 전, data base에 존재하는 메일이 있는지 확인
	 * @param sentMail
	 * @return
	 */
	public int selectSentMailExist(MailSentVO sentMail);
	
	/** 
	 * 보낸 메일함 추가(data base 및 api처리)
	 * @return
	 */
	public int insertSentMail(MailSentVO sentMail);
	
	/**
	 * 보낸 메일 삭제(data base 및 api 처리)
	 * @param rmailMessageid
	 * @return
	 */
	public int deleteSentMail(@Param("mailMessageid")String mailMessageid);
	
	
	/**
	 * empMail로 employee 정보가 있는지 없는지 확인
	 * @param empMail
	 * @return
	 */
	public EmployeeVO selectSearchMail(@Param("empMail")String empMail);

}
