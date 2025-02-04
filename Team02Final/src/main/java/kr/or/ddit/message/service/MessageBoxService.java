package kr.or.ddit.message.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.vo.MessageBoxVO;
import kr.or.ddit.message.vo.ReceiveMessageVO;

public interface MessageBoxService {
	public List<MessageBoxVO> readBoxMessageList(PaginationInfo<MessageBoxVO> paging,String empId);
	//public List<MessageBoxVO> readBoxMessageList(String empId);
	public MessageBoxVO readBoxMessageOne(String mboxId);
	public ServiceResult createBoxMessage(MessageBoxVO boxMessage);
	public ServiceResult modifyBoxMessage(MessageBoxVO boxMessage);
	public ServiceResult removeBoxMessage(String mboxId);
}
