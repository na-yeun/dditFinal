package kr.or.ddit.gmail.vo;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailReceivedVO {
	
	
	private int rnum;
	private String empId; // 사번
	private String empMail; // 메일주소
	private String mailMessageId; // 메일 한 개의 고유 ID
	private String rmailFrom; // 메일 보낸 사람
	private String rmailCc; // 메일의 참조
	private String rmailReplyTo; // 회신메일주소
	private String rmailDate; // 메일수신날짜
	private String rmailSubject; // 메일제목
	private String rmailContent; // 메일 내용
	private String rmailContentType; // 메일 타입
	private String rmailCalltime; // api 호출 시간
	
	// 화면 출력용
	private List<String> rmailFromList;
	// 화면 출력용
	private List<String> rmailCcList;
	
	public MailReceivedVO(String empId, String empMail, String mailMessageId, String rmailFrom, String rmailCc,
			String rmailReplyTo, String rmailDate, String rmailSubject, String rmailContent, String rmailContentType,
			String rmailCalltime) {
		this.empId = empId;
		this.empMail = empMail;
		this.mailMessageId = mailMessageId;
		this.rmailFrom = rmailFrom;
		this.rmailCc = rmailCc;
		this.rmailReplyTo = rmailReplyTo;
		this.rmailDate = rmailDate;
		this.rmailSubject = rmailSubject;
		this.rmailContent = rmailContent;
		this.rmailContentType = rmailContentType;
		this.rmailCalltime = rmailCalltime;
	}
	
	private List<MailAttachmentVO> mailAttachmentList;
	
}
