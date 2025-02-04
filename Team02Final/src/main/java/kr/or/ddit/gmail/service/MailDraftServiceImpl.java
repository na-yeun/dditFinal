package kr.or.ddit.gmail.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.dao.MailDraftMapper;
import kr.or.ddit.gmail.vo.MailDraftVO;
@Service
public class MailDraftServiceImpl implements MailDraftService {
	
	@Inject
	private MailDraftMapper mapper;
	
	@Override
	public String readLastApiCallTime(String empMail) {
		return mapper.selectLastApiCallTime(empMail);
	}

	@Override
	public List<MailDraftVO> readDraftMailList(PaginationInfo paging) {
		int totalRecord = mapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		return mapper.selectDraftMailList(paging);
	}

	@Override
	public MailDraftVO readDraftMailDetail(String messageId) {
		return mapper.selectDraftMailDetail(messageId);
	}

	@Override
	public int readDraftMailExist(MailDraftVO draftMail) {
		return mapper.selectDraftMailExist(draftMail);
	}

	@Override
	public int createDraftMail(MailDraftVO draftMail) {
		// TODO Auto-generated method stub
		return mapper.insertDraftMail(draftMail);
	}

	@Override
	public int deleteDraftMail(String draftId) {
		return mapper.deleteDraftMail(draftId);
	}

}
