package kr.or.ddit.gmail.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.GenericData;

import kr.or.ddit.employee.dao.GoogleOAuthMapper;
import kr.or.ddit.employee.vo.OAuthVO;
import lombok.extern.slf4j.Slf4j;

/**
 * gmail과 관련된 작업을 요청할 경우 해당 클래스에서 그 요청을 받아서 access token 사용 가능 여부를 확인 access
 * token을 사용할 수 없다면 refresh token을 사용해서 새 access token을 발급받음 새로 발급 받은 access
 * token은 data base에 update를 해야함
 * 
 * 생길 수 있는 오류 - access token 만료일 경우 401 오류 - access token이 취소되었을 경우 401 오류 - 잘못된
 * access token일 경우 401 오류)
 *
 */

@Slf4j
@Service
public class GoogleOAuthCheckServiceImpl implements GoogleOAuthCheckService{
	@Inject
	private GoogleOAuthMapper googleOAuthMapper;
	
	@Autowired
	private ServletContext servletContext;
	
	private String realPath;
	private static final String CREDENTIALS_FILE_PATH 
		= "/WEB-INF/oauth/credentials.json";
	
	@PostConstruct
	public void init() throws FileNotFoundException {
		realPath = servletContext.getRealPath(CREDENTIALS_FILE_PATH);

	    if (realPath == null) {
	        throw new FileNotFoundException("OAuth credentials file not found in WEB-INF/oauth/credentials.json");
	    }
	}
	
	@Override
	public OAuthVO getUsableAccessToken(OAuthVO myOAuth, String empMail) {
		
		if(myOAuth!=null) {
			String dataBaseAccessToken = myOAuth.getOauthAccess();
			// access token 확인하는 로직
			if (accessTokenUsable(dataBaseAccessToken)) {
				// access token 사용 가능하다면, 그대로 사용...
				return myOAuth;
			} else {
				// access token 사용이 불가능하다면 새로운 access token 요청
				OAuthVO updateOAuth = getNewAccessToken(myOAuth);
				return updateOAuth;
			}
		} else {
			OAuthVO myDataBaseOauth = googleOAuthMapper.selectOAuth(empMail);
			OAuthVO updateOAuth = getNewAccessToken(myDataBaseOauth);
			return updateOAuth;
		}
		
	}
	
	@Override
	public boolean accessTokenUsable(String myAccessToken) {

		try {
			// Google 사용자 프로필 API 호출
			// ?alt=json은 응답 데이터를 JSON 형식으로 받을 것을 지정
			String accessTokenUsableapiurl = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
			
			HttpRequestFactory httpRequestFactory = new NetHttpTransport().createRequestFactory();
			GenericUrl getAccessTokenUrl = new GenericUrl(accessTokenUsableapiurl);
			
			
			HttpRequest request = httpRequestFactory.buildGetRequest(getAccessTokenUrl);
			HttpHeaders headers = new HttpHeaders();
			// Authorization header의 Bearer 속성은 OAuth 2.0에서 사용하는 토큰 유형!!
			// 그래서 oauth 관련 document나 설명에 많이 나옴 기억하면 좋을 듯
			headers.setAuthorization("Bearer "+myAccessToken);
			headers.setAccept("application/json");
			request.setHeaders(headers);
			request.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

			request.execute();
			
			return true;

		} catch (Exception e) {
			// 오류가 발생시
			return false;
		}
	}

	@Override
	public OAuthVO getNewAccessToken(OAuthVO oAuth){
		// web 사이트의 정보(google drive에서 받은 json 파일) 가져오기
		
		// 이 코드 OAuth 처음 인증할 때 쓰는 코드랑 똑같아서.. 중복 줄이고 싶은데 어떻게 하는 게 좋을지....
		
		// JSON 파싱을 위한 JsonFactory
		JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

		// Google OAuth 클라이언트 인증 정보 파일 경로
//		String credentialsFilePath = "C:/Dev/dev_finalProject/googleoauthconfig/credentials.json";
		
		try {
			// 클라이언트 인증 정보 객체 (Google Client Secrets)
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
					jsonFactory,
					new InputStreamReader(new FileInputStream(realPath)));
			// json 파일 내의 client id
			String clientId = clientSecrets.getDetails().getClientId();
			// json 파일 내의 client secret
			String clientSecret = clientSecrets.getDetails().getClientSecret();
	
			
			// access token을 새로 요청할 때 쓰일 refresh token(파라미터로 받은 OAuthVO에서 꺼냄)
			// 이 때 파라미터로 받은 OAuthVO는 로그인 시에 session에 담겨서 온 OAuthVO임
			String refreshToken = oAuth.getOauthRefresh();
	
			// refresh token을 사용해서 새로운 access token을 발급받는 api 호출
			String tokenURL = "https://oauth2.googleapis.com/token";
			// api 주소를 통해 URL 객체 생성
			URL url = new URL(tokenURL);
			// HttpURLConnection는 java에서 제공하는데, http/https 통신을 위한 클래스임!!
			// API를 호출할 때 주로 사용하는 class이고 간단한 http 통신에도 사용한다고 함
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// request methos : post 요청
			connection.setRequestMethod("POST");
			// request header Content-Type : application/x-www-form-urlencoded
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// 출력 스트림 사용 활성화(요청 본문을 전송할 수 있도록 해줌, post 방식으로 보내줘야하기 때문에 본문이 있어야 함!)
			connection.setDoOutput(true);
	
			// request body
			String requestBody = String.format("client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token",
					clientId, clientSecret, refreshToken);
	
			try (OutputStream outputStream = connection.getOutputStream()) {
				outputStream.write(requestBody.getBytes());
				outputStream.flush();
			}
	
				// 응답 처리
				if (connection.getResponseCode() == 200) {
					 // InputStream을 통해 응답 데이터를 읽음
				    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
		
				    // JSON 응답을 파싱(google api에서 제공하는 객체로 사용하기..)
				    JsonObjectParser parser = new JsonObjectParser(jsonFactory);
				    GenericData jsonResponse = parser.parseAndClose(reader, GenericData.class);
				    
				    // jsonResponse 객체
				    // {classInfo=[], {access_token=새로 발급받은 access token 번호, expires_in=access token 만료시간, scope=허용받은 스코프 범위, token_type=Bearer, id_token=얘는 뭔지 잘 모르겠긴 함..}}
				    
				    // 새로 받은 access token
				    String newAccessToken = (String) jsonResponse.get("access_token");
					// 새로 받은 access token의 만료시간
//				    Long newExpiresInSeconds = ((Long)jsonResponse.get("expires_in")).longValue();
				    
				    // 새로 받은 access token의 정보를 oAuth 객체에 담기(해당 객체에 담긴 employee id랑 email의 값은 바뀌지 않으니 그대로 사용)
				    oAuth.setOauthAccess(newAccessToken);
//				    oAuth.setExpirationTime(newExpiresInSeconds);
				    
				    // 데이터베이스 업데이트
				    googleOAuthMapper.updateOAuth(oAuth);
				    
				    return oAuth;
				} else {
					// 응답 실패시.. 그냥 원래 있던 oAuth를 보내면 안되겠지..????
					throw new RuntimeException("Failed to refresh token: " + connection.getResponseMessage());
//					return oAuth;
				}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	
}
