package kr.or.ddit.gmail.vo;

import java.util.List;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailImportantVO {
	
	
	public MailImportantVO(String empId, String empMail, String mailMessageId, String imailTo, String imailFrom,
			String imailSubject, String imailDate, String imailContentType, String imailContent, String imailCc,
			String imailBcc, String imailReplyTo, String imailCalltime) {
		
		this.empId = empId;
		this.empMail = empMail;
		this.mailMessageId = mailMessageId;
		this.imailTo = imailTo;
		this.imailFrom = imailFrom;
		this.imailSubject = imailSubject;
		this.imailDate = imailDate;
		this.imailContentType = imailContentType;
		this.imailContent = imailContent;
		this.imailCc = imailCc;
		this.imailBcc = imailBcc;
		this.imailReplyTo = imailReplyTo;
		this.imailCalltime = imailCalltime;
	}

	
	private int rnum;
	
	
	private String empId;
	private String empMail;
	private String mailMessageId;
	private String imailTo;
	private String imailFrom;
	private String imailSubject;
	private String imailDate;
	private String imailContentType;
	private String imailContent;
	private String imailCc;
	private String imailBcc;
	private String imailReplyTo;
	private String imailCalltime;
	
	// 화면 출력용
	private List<String> imailToList;
	private List<String> imailFromList;
	private List<String> imailCcList;
	private List<String> imailBccList;
	
	private List<MailAttachmentVO> mailAttachmentList;
}
