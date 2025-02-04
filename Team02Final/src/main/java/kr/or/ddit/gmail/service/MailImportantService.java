package kr.or.ddit.gmail.service;

import java.util.List;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;

public interface MailImportantService {
	/**
	 * 한 명의 계정으로 마지막 api 요청을 보낸 시간
	 * 
	 * @param imailAccount
	 * @return
	 */
	public String readLastApiCallTime(String empMail);
	
	
	/**
	 * 한 명의 받은 메일함 가지고 오기
	 * @return
	 */
	public List<MailImportantVO> readImportantMailList(PaginationInfo paging);
	
	/**
	 * 메일 한 개의 상세 정보 가지고 오기
	 * @param messageId
	 * @return
	 */
	public MailImportantVO readImportantMailDetail(String messageId);
	
	
	/**
	 * 받은 메일을 추가하기 전, data base에 존재하는 메일이 있는지 확인
	 * @param receivedMail
	 * @return
	 */
	public int readImportantMailExist(MailImportantVO importantMail);
	
	
	/** 
	 * 받은 메일함 추가(data base 및 api처리)
	 * @return
	 */
	public int createImportantMail(MailImportantVO importantMail);
	
	/**
	 * 받은 메일 삭제(data base 및 api 처리)
	 * @param rmailMessageid
	 * @return
	 */
	public int deleteImportantMail(String mailMessageid);
}
