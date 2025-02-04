package kr.or.ddit.message.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.vo.SendMessageVO;

public interface SendMessageService {
	/**
	 * @param paging
	 * @return 페이칭 처리가 된 쪽지 발신함
	 */
	//public List<SendMessageVO> readSendMessageList(String empId);
	public List<SendMessageVO> readSendMessageList(PaginationInfo<SendMessageVO> paging, String empId);
	/**
	 * @param smesId
	 * @return 발신 쪽지 하나
	 */
	public List<SendMessageVO> readSendMessageOne(String smesId);
	/**
	 * @param sendMessage
	 * @return 쪽지 전송 ==> 인서트가 되면서 수신함에도 인서트가 되어야함
	 */
	public ServiceResult insertSendMessage(SendMessageVO sendMessage);
	
	/**
	 * @param sendMessage
	 * @return==> 업데이트는 읽음여부가 yn이 되는거다.,,,
	 */
	public ServiceResult updateSendMessage(SendMessageVO sendMessage);
	/**
	 * @param smesId
	 * @return 발신함에 있는 쪽지 삭제
	 */
	public ServiceResult deleteSendMessage(String smesId);
	
}
