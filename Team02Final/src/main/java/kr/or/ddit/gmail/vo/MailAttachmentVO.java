package kr.or.ddit.gmail.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailAttachmentVO {
	
	private String empId;
	private String empMail;
	private String mailmessageId;
	private String mailattachmentId;
	private String mailattachmentName;
	private String mailattachmentMimetype;
	private String mailattachmentHashcode;
}
