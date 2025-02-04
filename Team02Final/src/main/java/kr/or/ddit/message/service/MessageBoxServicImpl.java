package kr.or.ddit.message.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.dao.MessageBoxMapper;
import kr.or.ddit.message.vo.MessageBoxVO;
import kr.or.ddit.message.vo.ReceiveMessageVO;

@Service
public class MessageBoxServicImpl implements MessageBoxService {

	@Inject
	private MessageBoxMapper dao;
	
	@Override
	public List<MessageBoxVO> readBoxMessageList(PaginationInfo<MessageBoxVO> paging, String empId) {
		if (paging != null) {
            int totalRecord = dao.selectTotalRecord(paging, empId);
            paging.setTotalRecord(totalRecord);
        }
		return dao.selectBoxMessageList(paging,empId);
	}

	@Override
	public MessageBoxVO readBoxMessageOne(String mboxId) {
		// TODO Auto-generated method stub
		return dao.selectBoxMessageOne(mboxId);
	}

	@Override
	public ServiceResult createBoxMessage(MessageBoxVO boxMessage) {
		if (dao.insertBoxMessage(boxMessage)> 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult modifyBoxMessage(MessageBoxVO boxMessage) {
		if (dao.updateBoxMessage(boxMessage)> 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult removeBoxMessage(String mboxId) {
		if (dao.deleteBoxMessage(mboxId)> 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

}
