package kr.or.ddit.gmail;

public enum MailSendType {
	SEND("messages/send"), // 보내기
	DRAFT("drafts"); // 임시저장
	
	
	private final String value;
	
	MailSendType(String value) {
        this.value = value;
    }
	
	public String getValue() {
		return value;
	}
	
	

}
