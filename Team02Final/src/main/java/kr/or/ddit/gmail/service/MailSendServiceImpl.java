package kr.or.ddit.gmail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


import kr.or.ddit.gmail.vo.MailAttachmentVO;
import kr.or.ddit.gmail.vo.MailImportantVO;
import kr.or.ddit.gmail.vo.MailReceivedVO;
import kr.or.ddit.gmail.vo.MailSendDTO;
import kr.or.ddit.gmail.vo.MailSentVO;
@Service
public class MailSendServiceImpl implements MailSendService {

	@Override
	public MailSendDTO sentToSendDTO(MailSentVO myMail, String messageId) {
		MailSendDTO forward = new MailSendDTO();
		forward.setMessageId(messageId);

		StringBuffer content = new StringBuffer();
		content.append("<b>-----Original Message-----<br>");
		content.append("From: "+myMail.getEmpMail()+"<br>"); // 내가 보낸 메일 전달이기 때문에 from은 나
		content.append("To: "+myMail.getSmailTo()+"<br>"); // 원본의 받은 사람
		if(StringUtils.isBlank(myMail.getSmailCc())) {
			content.append("Cc: <br>");
		} else {
			content.append("Cc: "+myMail.getSmailCc()+"<br>");
		}
		
		content.append("Sent: "+myMail.getSmailDate()+"</b><br>");
		
		if(StringUtils.isBlank(myMail.getSmailSubject())) {
			content.append("Subject: <br>");
			forward.setMailSubject("Fwd : ");
		} else {
			content.append("Subject: "+myMail.getSmailSubject()+"<br>");
			forward.setMailSubject("Fwd : "+myMail.getSmailSubject());
		}
		
		
		forward.setMailContent(content.toString());
		
		boolean hasAttach = false;
		List<MailAttachmentVO> attachList = myMail.getMailAttachmentList();
		for(MailAttachmentVO attach : attachList) {
			if(attach.getMailattachmentHashcode()!=null) {
				hasAttach=true;
				break;
			}
		}


		if(hasAttach) {
			forward.setOriginalMailFiles(attachList);
		}
		return forward;
	}

	@Override
	public MailSendDTO receivedToSendDTO(MailReceivedVO myMail, String messageId) {
		MailSendDTO forward = new MailSendDTO();
		forward.setMessageId(messageId);

		StringBuffer content = new StringBuffer();
		content.append("<b>-----Original Message-----<br>");
		content.append("From: "+myMail.getRmailFrom()+"<br>"); // 내가 받은 메일 전달이기 때문에 from은 보낸 사람
		content.append("To: "+myMail.getEmpMail()+"<br>"); // 원본의 받은 사람 나야나!!
		if(StringUtils.isBlank(myMail.getRmailCc())) {
			content.append("Cc: <br>");
		} else {
			content.append("Cc: "+myMail.getRmailCc()+"<br>");
		}
		
		content.append("Sent: "+myMail.getRmailDate()+"</b><br>");
		
		if(StringUtils.isBlank(myMail.getRmailSubject())) {
			content.append("Subject: <br>");
			forward.setMailSubject("Fwd : ");
		} else {
			content.append("Subject: "+myMail.getRmailSubject()+"<br>");
			forward.setMailSubject("Fwd : "+myMail.getRmailSubject());
		}
		
		
		forward.setMailContent(content.toString());
		
		boolean hasAttach = false;
		List<MailAttachmentVO> attachList = myMail.getMailAttachmentList();
		for(MailAttachmentVO attach : attachList) {
			if(attach.getMailattachmentHashcode()!=null) {
				hasAttach=true;
				break;
			}
		}

		if(hasAttach) {
			forward.setOriginalMailFiles(attachList);
		}
		return forward;
	}

	@Override
	public MailSendDTO importantToSendDTO(MailImportantVO myMail, String messageId) {
		MailSendDTO forward = new MailSendDTO();
		forward.setMessageId(messageId);

		StringBuffer content = new StringBuffer();
		content.append("<b>-----Original Message-----<br>");
		content.append("From: "+myMail.getImailFrom()+"<br>"); 
		content.append("To: "+myMail.getImailTo()+"<br>"); 
		if(StringUtils.isBlank(myMail.getImailCc())) {
			content.append("Cc: <br>");
		} else {
			content.append("Cc: "+myMail.getImailCc()+"<br>");
		}
		
		content.append("Sent: "+myMail.getImailDate()+"</b><br>");
		
		if(StringUtils.isBlank(myMail.getImailSubject())) {
			content.append("Subject: <br>");
			forward.setMailSubject("Fwd : ");
		} else {
			content.append("Subject: "+myMail.getImailSubject()+"<br>");
			forward.setMailSubject("Fwd : "+myMail.getImailSubject());
		}

		forward.setMailContent(content.toString());
		
		boolean hasAttach = false;
		List<MailAttachmentVO> attachList = myMail.getMailAttachmentList();
		for(MailAttachmentVO attach : attachList) {
			if(attach.getMailattachmentHashcode()!=null) {
				hasAttach=true;
				break;
			}
		}

		if(hasAttach) {
			forward.setOriginalMailFiles(attachList);
		}
		return forward;
	}

	@Override
	public MailSendDTO replyToReceived(MailReceivedVO myMail, String messageId) {
		MailSendDTO reply = new MailSendDTO();
		List<String> toList = new ArrayList<>();
		
		// 이메일 추출을 위한 정규식
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(emailRegex);
		
		if (StringUtils.isNotBlank(myMail.getRmailReplyTo())) {
			// 전달자가 지정되어있음
			// "," 기준으로 분리
	        String[] parts = myMail.getRmailReplyTo().split(",");

	        // 이메일 추출 및 출력
	        for (String part : parts) {
	            Matcher matcher = pattern.matcher(part.trim());
	            if (matcher.find()) {
	            	toList.add(matcher.group());
	            }
	        }
	        
		} else {
			// 전달자가 지정되어있지 않으면 메일 보낸 사람한테 답장하면 됨
			
			// "," 기준으로 분리
	        String[] parts = myMail.getRmailFrom().split(",");
	        // 이메일 추출 및 출력
	        for (String part : parts) {
	            Matcher matcher = pattern.matcher(part.trim());
	            if (matcher.find()) {
	            	toList.add(matcher.group());
	            }
	        }
			
		}
		reply.setMailTo(toList);

		reply.setMessageId(messageId);

		StringBuffer content = new StringBuffer();
		content.append("<b>-----Original Message-----<br>");
		content.append("From: " + myMail.getRmailFrom() + "<br>");
		content.append("To: " + myMail.getEmpMail()+"<br>"); 
		if(StringUtils.isBlank(myMail.getRmailCc())) {
			content.append("Cc: <br>");
		} else {
			content.append("Cc: "+myMail.getRmailCc()+"<br>");
		}
		
		content.append("Sent: "+myMail.getRmailDate()+"</b><br>");
		
		if(StringUtils.isBlank(myMail.getRmailSubject())) {
			content.append("Subject: <br>");
			reply.setMailSubject("Re : ");
		} else {
			content.append("Subject: "+myMail.getRmailSubject()+"<br>");
			reply.setMailSubject("Re : "+myMail.getRmailSubject());
		}
		

		reply.setMailContent(content.toString());
		
		return reply;
	}

	@Override
	public MailSendDTO replyToImportant(MailImportantVO myMail, String messageId) {
		MailSendDTO reply = new MailSendDTO();
		List<String> toList = new ArrayList<>();

		// 이메일 추출을 위한 정규식
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(emailRegex);
		
		if (StringUtils.isBlank(myMail.getImailReplyTo())) {
			// 전달자가 지정되어있음
			// "," 기준으로 분리
	        String[] parts = myMail.getImailReplyTo().split(",");

	        // 이메일 추출 및 출력
	        for (String part : parts) {
	            Matcher matcher = pattern.matcher(part.trim());
	            if (matcher.find()) {
	            	toList.add(matcher.group());
	            }
	        }
	        
		} else {
			// 이메일 추출을 위한 정규식
	        String[] parts = myMail.getImailFrom().split(",");
	        // 이메일 추출 및 출력
	        for (String part : parts) {
	            Matcher matcher = pattern.matcher(part.trim());
	            if (matcher.find()) {
	            	toList.add(matcher.group());
	            }
	        }
			
		}
		reply.setMailTo(toList);

		reply.setMessageId(messageId);

		StringBuffer content = new StringBuffer();
		content.append("<b>-----Original Message-----<br>");
		content.append("From: " + myMail.getImailFrom() + "<br>");
		content.append("To: " + myMail.getEmpMail()+"<br>"); 
		if(StringUtils.isBlank(myMail.getImailCc())) {
			content.append("Cc: <br>");
		} else {
			content.append("Cc: "+myMail.getImailCc()+"<br>");
		}
		
		content.append("Sent: "+myMail.getImailDate()+"</b><br>");
		
		if(StringUtils.isBlank(myMail.getImailSubject())) {
			content.append("Subject: <br>");
			reply.setMailSubject("Re : ");
		} else {
			content.append("Subject: "+myMail.getImailSubject()+"<br>");
			reply.setMailSubject("Re : "+myMail.getImailSubject());
		}
		

		reply.setMailContent(content.toString());
		
		return reply;
	}

}
