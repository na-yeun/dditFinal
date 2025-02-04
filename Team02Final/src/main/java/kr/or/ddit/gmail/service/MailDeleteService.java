package kr.or.ddit.gmail.service;

import java.util.List;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.vo.MailDeleteVO;

public interface MailDeleteService {
	/**
	 * 한 명의 계정으로 마지막 api 요청을 보낸 시간
	 * 
	 * @param rmailAccount
	 * @return
	 */
	public String readLastApiCallTime(String empMail);
	
	
	/**
	 * 한 명의 삭제 메일함 가지고 오기
	 * @return
	 */
	public List<MailDeleteVO> readDeleteMailList(PaginationInfo paging);
	
	/**
	 * 메일 한 개의 상세 정보 가지고 오기
	 * @param messageId
	 * @return
	 */
	public MailDeleteVO readDeleteMailDetail(String messageId);
	
	
	/**
	 * 삭제메일을 추가하기 전, data base에 존재하는 메일이 있는지 확인
	 * @param receivedMail
	 * @return
	 */
	public int readDeleteMailExist(MailDeleteVO deleteMail);
	
	
	/** 
	 * 삭제 메일함 추가(data base 및 api처리)
	 * @return
	 */
	public int createDeleteMail(MailDeleteVO deleteMail);
	
	/**
	 * 삭제 메일 영구삭제(data base 및 api 처리)
	 * @param rmailMessageid
	 * @return
	 */
	public int deleteDeleteMail(String mailMessageid);
}
