package kr.or.ddit.message.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.message.dao.ReceiveMessageMapper;
import kr.or.ddit.message.vo.ReceiveMessageVO;


@Service
public class ReceiveMessageServiceImple implements ReceiveMessageService {

	@Inject
	private ReceiveMessageMapper dao;
	
	
	@Override
	public List<ReceiveMessageVO> readReceiveMessageList(PaginationInfo<ReceiveMessageVO> paging, String empId) {
		if (paging != null) {
            int totalRecord = dao.selectTotalRecord(paging, empId);
            paging.setTotalRecord(totalRecord);
        }
		return dao.selectReceiveMessageList(paging, empId);
	}

	@Override
	public ReceiveMessageVO readReceiveMessageOne(String rmesId) {
		// TODO Auto-generated method stub
		return dao.selectReceiveMessageOne(rmesId);
	}

	@Override
	public ServiceResult createReceiveMessage(ReceiveMessageVO resMessage) {
		if (dao.insertReceiveMessage(resMessage)> 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult modifyReceiveMessage(String rmesId) {
		if (dao.updateReceiveMessage(rmesId)> 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult removeReceiveMessage(String rmesId) {
		if (dao.deleteReceiveMessage(rmesId) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

}
