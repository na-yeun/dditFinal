package kr.or.ddit.message.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.vo.ReceiveMessageVO;
import kr.or.ddit.message.vo.SendMessageVO;

public interface ReceiveMessageService {
	
	public List<ReceiveMessageVO> readReceiveMessageList(PaginationInfo<ReceiveMessageVO> paging, String empId);
	public ReceiveMessageVO readReceiveMessageOne(String rmesId);
	public ServiceResult createReceiveMessage(ReceiveMessageVO resMessage);
	public ServiceResult modifyReceiveMessage(String rmesId);
	public ServiceResult removeReceiveMessage(String rmesId);
}
