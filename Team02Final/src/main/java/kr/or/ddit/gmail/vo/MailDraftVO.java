package kr.or.ddit.gmail.vo;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailDraftVO {
	private int rnum;
	
	
	private String empId;
	private String empMail;
	private String mailMessageId;
	private String dmailDraftId;
	private String dmailTo;
	private String dmailSubject;
	private String dmailDate;
	private String dmailContentType;
	private String dmailContent;
	private String dmailCc;
	private String dmailBcc;
	private String dmailReplyTo;
	private String dmailCalltime;
	
	private List<MailAttachmentVO> mailAttachmentList;

	public MailDraftVO(String empId, String empMail, String mailMessageId, String dmailDraftId, String dmailTo,
			String dmailSubject, String dmailDate, String dmailContentType, String dmailContent, String dmailCc,
			String dmailBcc, String dmailReplyTo, String dmailCalltime) {
		this.empId = empId;
		this.empMail = empMail;
		this.mailMessageId = mailMessageId;
		this.dmailDraftId = dmailDraftId;
		this.dmailTo = dmailTo;
		this.dmailSubject = dmailSubject;
		this.dmailDate = dmailDate;
		this.dmailContentType = dmailContentType;
		this.dmailContent = dmailContent;
		this.dmailCc = dmailCc;
		this.dmailBcc = dmailBcc;
		this.dmailReplyTo = dmailReplyTo;
		this.dmailCalltime = dmailCalltime;
	}
	
	

}
