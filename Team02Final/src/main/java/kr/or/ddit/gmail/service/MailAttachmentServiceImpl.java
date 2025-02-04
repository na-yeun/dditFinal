package kr.or.ddit.gmail.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.dao.MailAttachmentMapper;
import kr.or.ddit.gmail.vo.MailAttachmentVO;
@Service
public class MailAttachmentServiceImpl implements MailAttachmentService {
	@Inject
	private MailAttachmentMapper mapper;
	
	@Override
	public List<MailAttachmentVO> readAttachmentList(String mailmessageId){
		return mapper.selectAttachmentList(mailmessageId);
	}
	

	@Override
	public int readAttachmentExist(MailAttachmentVO mailAttachment) {
		return mapper.selectAttachmentExist(mailAttachment);
	}
	
	@Override
	public MailAttachmentVO readAttachmentDetail(MailAttachmentVO mailAttachment) {
		return mapper.selectAttachmentDetail(mailAttachment);
	}
	
	@Override
	public int createMailAttachment(MailAttachmentVO mailAttachment) {
		return mapper.insertMailAttachment(mailAttachment);
	}


	@Override
	public int deleteMailAttachment(String mailmessageId) {
		return mapper.deleteMailAttachment(mailmessageId);
	}

	

}
