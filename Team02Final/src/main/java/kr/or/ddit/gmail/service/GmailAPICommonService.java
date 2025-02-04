package kr.or.ddit.gmail.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.http.HttpResponse;
import com.google.api.services.gmail.model.Message;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.employee.vo.OAuthVO;
import kr.or.ddit.gmail.MailSendType;
import kr.or.ddit.gmail.vo.MailSendDTO;

/**
 * Gmail API에서 공통적으로 사용하는 코드1
 * 첨부파일다운로드, 메일삭제, 메일영구삭제, 라벨추가(삭제)
 * @author kimny
 *
 */
public interface GmailAPICommonService {
	/**
	 * 첨부파일 다운로드
	 * @param messageId
	 * @param attachId
	 * @param oAuth
	 * @return
	 */
	public byte[] getAttachment(String messageId, String attachId, OAuthVO oAuth);

	
	/**
	 * 메일 영구 삭제
	 * @param messageId
	 * @param oAuth
	 * @return
	 */
	public ServiceResult deleteMail(String messageId, OAuthVO oAuth);
	
	/**
	 * 임시저장 메일 영구 삭제
	 * @param messageId
	 * @param oAuth
	 * @return
	 */
	public ServiceResult trashDraftMail(String messageId, OAuthVO oAuth);
	
	/**
	 * 메일 라벨 추가(삭제 혹은 중요메일)
	 * @return
	 */
	public ServiceResult addMailLables(String messageId, OAuthVO myNewOAuth, String[] labels);
	
	/**
	 * 메일 라벨 삭제(삭제 혹은 중요메일)
	 * @param messageId
	 * @param oAuth
	 * @return
	 */
	public ServiceResult disMailLables(String messageId, OAuthVO myNewOAuth, String[] labels);
	
	/**
	 * 메일 라벨 추가 및 삭제 모두
	 * @param messageId
	 * @param myNewOAuth
	 * @param addLabels
	 * @param disLabels
	 * @return
	 */
	public ServiceResult addAndDisMailLables(String messageId, OAuthVO myNewOAuth, String[] addLabels, String[] disLabels);
	
	/**
	 * 메일 삭제 혹은 복구
	 * @param messageId
	 * @param myNewOAuth
	 * @return
	 */
	public ServiceResult trashMail(String messageId, OAuthVO myNewOAuth, String type);
	
	
	/**
	 * 메일 객체 만드는 메소드
	 * @param mail
	 * @return
	 */
	public Message mailMessage(MailSendDTO mail, String forwardMessageId, OAuthVO myNewOAuth) throws AddressException, MessagingException, IOException;
	
	
	/**
	 * 메일 발송
	 * @param myNewOAuth
	 * @param message
	 */
	public HttpResponse sendMail(MailSendType type, OAuthVO myNewOAuth, Message message);
	
	
	

	
}
