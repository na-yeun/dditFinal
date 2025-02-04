package kr.or.ddit.gmail.service;

import java.util.List;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.gmail.vo.MailAttachmentVO;

public interface MailAttachmentService {
	
	/**
	 * 메일 id 한 개의 첨부파일 리스트
	 * @param mailmessageId
	 * @return
	 */
	public List<MailAttachmentVO> readAttachmentList(String mailmessageId);

	/**
	 * 데이터베이스 중복 insert를 막기 위한 존재여부 확인
	 * @param mailAttachment
	 * @return
	 */
	public int readAttachmentExist(MailAttachmentVO mailAttachment);
	
	/**
	 * 첨부파일 한 개의 상세정보 조회
	 * @param mailAttachment : MAILMESSAGE_ID / MAILATTACHMENT_ID
	 * @return
	 */
	public MailAttachmentVO readAttachmentDetail(MailAttachmentVO mailAttachment);
	
	/**
	 * 첨부파일 한 개 추가
	 * @param mailAttachment
	 * @return
	 */
	public int createMailAttachment(MailAttachmentVO mailAttachment);
	
	/**
	 * 첨부파일 한 개 삭제
	 * @param mailmessageId
	 * @return
	 */
	public int deleteMailAttachment(String mailmessageId);

}
