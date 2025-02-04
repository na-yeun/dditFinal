package kr.or.ddit.message.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.dao.SendMessageMapper;
import kr.or.ddit.message.vo.SendMessageVO;
import kr.or.ddit.room.vo.RoomVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendMessageServiceImpl implements SendMessageService {

	@Inject
	private SendMessageMapper dao;

	
	public List<SendMessageVO> readSendMessageList(PaginationInfo<SendMessageVO> paging, String empId) {
		if (paging != null) {
            int totalRecord = dao.selectTotalRecord(paging, empId);
            paging.setTotalRecord(totalRecord);
        }
		
		return  dao.selectSendMessageList(paging, empId);
				
	}

	@Override
	public List<SendMessageVO> readSendMessageOne(String smesId) {
		// TODO Auto-generated method stub
		return dao.selectSendMessageOne(smesId);
	}
	@Transactional
	@Override
	public ServiceResult insertSendMessage(SendMessageVO sendMessage) {
		int rowsAffected = dao.insertSendMessage(sendMessage);

		// 디버깅: SMES_ID 출력
		System.out.println("SMES_ID: " + sendMessage.getSmesId());

		if (rowsAffected > 0 && sendMessage.getSmesId() != null) {
			return ServiceResult.OK;
		} else {
			System.err.println("Insert failed or SMES_ID is null.");
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult updateSendMessage(SendMessageVO sendMessage) {
		if (dao.updateSendMessage(sendMessage) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult deleteSendMessage(String smesId) {
		if (dao.deleteSendMessage(smesId) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

}
