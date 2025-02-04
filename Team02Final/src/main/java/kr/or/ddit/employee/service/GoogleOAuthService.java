package kr.or.ddit.employee.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.employee.vo.OAuthVO;

public interface GoogleOAuthService {
	

	public String getAuthorizationUrl() throws FileNotFoundException, IOException, GeneralSecurityException;
	
	public Credential getCredentialFromCode(String code) throws IOException ;
	
	/**
	 * 로그인시에 미리 session에 저장된 oauth 정보를 가지고
	 * 그 안에 있는 access token을 체크하여 return 하는 메소드
	 * 
	 * @param session
	 * @return 사용 가능할 경우 그대로 return, 사용 불가능할 경우 refresh token을 사용해서 재발급
	 */
	public OAuthVO getUsableAccessToken(OAuthVO myOAuth, String empMail);
	/**
	 * access token이 사용 가능한 토큰인지 확인하는 메소드
	 * 
	 * @param myAccessToken : 해당 메소드를 요청하는 곳에서, session에 저장되어있는 OAuth 정보를 꺼내고, 또
	 *                      access token 꺼내서 파라미터로 전달
	 * @return : access token 사용이 가능하면 true, 불가능하면 false
	 */
	public boolean accessTokenUsable(String myAccessToken);
	/**
	 * access token을 사용할 수 없을 때, refresh token을 사용해서 새로은 access token을 받는 메소드
	 * 
	 * @param oAuthVO : session에 담겨있는 현재 oauth 정보
	 * @return 새로운 access token(+만료시간)을 담은 oauth 정보
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public OAuthVO getNewAccessToken(OAuthVO oAuth);
	/**
	 * @param 새로 받은 access token과 access token 만료 시간이 담긴 OAuthVO
	 * @return data base에 update한 결과(성공이면 OK, 실패면 FAIL)
	 */
	public ServiceResult modifyOAuth(OAuthVO oAuth);
	
	/**
	 * @param 회원가입시 입력한 oauth  정보
	 * @return data base insert 결과(성공OK, 실패FAIL)
	 */
	public ServiceResult createOAuth(OAuthVO oAuth);
	
	

}
