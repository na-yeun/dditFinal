package kr.or.ddit.gmail.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.commons.validate.MailSendGroup;
import lombok.Data;

@Data
public class MailSendDTO {
	private String messageId; // messageId가 있을 경우, 전달메일임
	
	private String mailFrom;
	
	@NotEmpty(groups = MailSendGroup.class)
	private List<String> mailTo;
	
	private List<String> mailCc;
	
	private List<String> mailBcc;
	
	@NotBlank(groups = MailSendGroup.class)
	private String mailSubject;
	
	private String mailContent;
	// 메일 발송용
	private List<MultipartFile> mailFiles;
	
	// 메일 답장 / 전달 시 (원본 파일)을 담은 List
	private List<MailAttachmentVO> originalMailFiles;
	

}
