package kr.or.ddit.event.loginGmailEvent;

import org.springframework.context.ApplicationEvent;

import kr.or.ddit.employee.vo.OAuthVO;

public class LoginSuccessEvent extends ApplicationEvent{
	private final OAuthVO myOAuth;

	public LoginSuccessEvent(Object source, OAuthVO myOAuth) {
		super(source);
		this.myOAuth = myOAuth;
	}
	
	public OAuthVO getMyOAuth() {
		return myOAuth;
	}
}

