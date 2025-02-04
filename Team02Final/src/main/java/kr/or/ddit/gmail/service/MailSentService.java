package kr.or.ddit.gmail.service;

import java.util.List;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.gmail.vo.MailSentVO;

public interface MailSentService {
	/**
	 * 한 명의 계정으로 마지막 api 요청을 보낸 시간
	 * 
	 * @param rmailAccount
	 * @return
	 */
	public String readLastApiCallTime(String empMail);
	
	
	/**
	 * 한 명의 보낸 메일함 가지고 오기
	 * @return
	 */
	public List<MailSentVO> readSentMailList(PaginationInfo paging);
	
	
	/**
	 * 메일 한 개의 상세 정보 가지고 오기
	 * @param messageId
	 * @return
	 */
	public MailSentVO readSentMailDetail(String messageId);
	
	
	/**
	 * 보낸 메일을 추가하기 전, data base에 존재하는 메일이 있는지 확인
	 * @param sentMail
	 * @return
	 */
	public int readSentMailExist(MailSentVO sentMail);
	
	
	/** 
	 * 보낸 메일함 추가(data base 및 api처리)
	 * @return
	 */
	public int createSentMail(MailSentVO sentMail);
	
	/**
	 * 보낸 메일 삭제(data base 및 api 처리)
	 * @param rmailMessageid
	 * @return
	 */
	public int deleteSentMail(String mailMessageId);
	
	
	/**
	 * empMail로 employee 정보가 있는지 없는지 확인
	 * @param empMail
	 * @return
	 */
	public EmployeeVO readSearchMail(String empMail);
	

}
