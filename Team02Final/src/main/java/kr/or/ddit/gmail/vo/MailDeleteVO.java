package kr.or.ddit.gmail.vo;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailDeleteVO {
	private int rnum;
	
	
	private String empId;
	private String empMail;
	private String mailMessageId;
	private String delmailTo;
	private String delmailFrom;
	
	private String delmailSubject;
	private String delmailDate;
	private String delmailContentType;
	private String delmailContent;
	private String delmailCc;
	private String delmailBcc;
	private String delmailReplyTo;
	private String delmailCalltime;
	
	private List<MailAttachmentVO> mailAttachmentList;
	
	// 출력용
	private List<String> delmailToList;
	private List<String> delmailFromList;
	private List<String> delmailCcList;
	private List<String> delmailBccList;

	public MailDeleteVO(String empId, String empMail, String mailMessageId, String delmailTo, String delmailFrom,
			String delmailSubject, String delmailDate, String delmailContentType, String delmailContent,
			String delmailCc, String delmailBcc, String delmailReplyTo, String delmailCalltime) {
	
		this.empId = empId;
		this.empMail = empMail;
		this.mailMessageId = mailMessageId;
		this.delmailTo = delmailTo;
		this.delmailFrom = delmailFrom;
		this.delmailSubject = delmailSubject;
		this.delmailDate = delmailDate;
		this.delmailContentType = delmailContentType;
		this.delmailContent = delmailContent;
		this.delmailCc = delmailCc;
		this.delmailBcc = delmailBcc;
		this.delmailReplyTo = delmailReplyTo;
		this.delmailCalltime = delmailCalltime;
	}
}
