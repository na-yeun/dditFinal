package kr.or.ddit.gmail.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.commons.validate.MailSendGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailSentVO {
	
	
	
	
	public MailSentVO(String empId, String empMail, String mailMessageId, String smailTo, String smailSubject,
			String smailDate, String smailContentType, String smailContent, String smailCc, String smailBcc,
			String smailReplyTo, String smailCalltime) {
		
		this.empId = empId;
		this.empMail = empMail;
		this.mailMessageId = mailMessageId;
		this.smailTo = smailTo;
		this.smailSubject = smailSubject;
		this.smailDate = smailDate;
		this.smailContentType = smailContentType;
		this.smailContent = smailContent;
		this.smailCc = smailCc;
		this.smailBcc = smailBcc;
		this.smailReplyTo = smailReplyTo;
		this.smailCalltime = smailCalltime;
	}

	private int rnum;
	
	private String empId;
	private String empMail;
	private String mailMessageId;
	private String smailTo;
	private String smailSubject;
	private String smailDate;
	private String smailContentType;
	private String smailContent;
	private String smailCc;
	private String smailBcc;
	private String smailReplyTo;
	private String smailCalltime;
	// 화면 출력용
	private List<String> smailToList;
	private List<String> smailCcList;
	private List<String> smailBccList;
	
	private List<MailAttachmentVO> mailAttachmentList;

}
