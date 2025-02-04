package kr.or.ddit.employee.service;

import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential;

public interface GoogleUserInfoAPIService {
	
	public String getUserEmail(Credential credential) throws IOException;

}
