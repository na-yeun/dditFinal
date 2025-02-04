package kr.or.ddit.gmail.service;

import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;
import kr.or.ddit.gmail.vo.MailSendDTO;
import kr.or.ddit.gmail.vo.MailSentVO;

public interface MailSendService {
	public MailSendDTO sentToSendDTO(MailSentVO myMail, String messageId);

	public MailSendDTO receivedToSendDTO(MailReceivedVO myMail, String messageId);
	
	public MailSendDTO importantToSendDTO(MailImportantVO myMail, String messageId);
	
	public MailSendDTO replyToReceived(MailReceivedVO myMail, String messageId);
	
	public MailSendDTO replyToImportant(MailImportantVO myMail, String messageId);
	

}
