package kr.or.ddit.employee.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.store.FileDataStoreFactory;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.employee.dao.GoogleOAuthMapper;
import kr.or.ddit.employee.vo.OAuthVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GoogleOAuthServiceImpl implements GoogleOAuthService {
	@Autowired
	private ServletContext servletContext;
	@Inject
	private GoogleOAuthMapper googleOAuthMapper;
	
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	// 허용 범위
	private static final List<String> SCOPES = Arrays.asList(
			// gmail api
			// 민감하지 않은 범위
			// 임시보관된 이메일 관리 및 이메일 전송
			"https://www.googleapis.com/auth/gmail.addons.current.action.compose",
			// 사용자가 부가기능과 상호작용할 때 이메일 메시지 보기
			"https://www.googleapis.com/auth/gmail.addons.current.message.action",
			// 이메일 라벨 확인 및 수정
			"https://www.googleapis.com/auth/gmail.labels",

			// 민감한 범위
			// 부가기능 실행 시 이메일 메시지 메타데이터 확인
			"https://www.googleapis.com/auth/gmail.addons.current.message.metadata",
			// 부가기능 실행 시 이메일 메시지 보기
			"https://www.googleapis.com/auth/gmail.addons.current.message.readonly",
			// 나를 대신하여 이메일 전송
			"https://www.googleapis.com/auth/gmail.send",

			// 제한된 범위
			// Gmail에서 모든 이메일 확인, 작성, 전송, 영구 삭제
			"https://mail.google.com/",
			// Gmail 계정에서 이메일 읽기, 작성 및 전송
			"https://www.googleapis.com/auth/gmail.modify",
			// 임시보관 메일 관리 및 이메일 전송
			"https://www.googleapis.com/auth/gmail.compose",
			// 이메일 메시지 및 설정 보기
			"https://www.googleapis.com/auth/gmail.readonly",
			// Gmail 편지함에 이메일 추가
			"https://www.googleapis.com/auth/gmail.insert",
			// Gmail의 이메일 설정 및 필터 확인, 수정, 생성 또는 변경
			"https://www.googleapis.com/auth/gmail.settings.basic",
			// 내 메일을 관리할 수 있는 사용자를 포함한 중요한 메일 설정 관리
			"https://www.googleapis.com/auth/gmail.settings.sharing",

			// google user info api
			// 계정 정보(이메일) 가지고 오는 scope
			"https://www.googleapis.com/auth/userinfo.email");

	// Google OAuth 클라이언트 인증 정보 파일 경로
	// google cloud에서 oauth 인증 설정한 후에 다운받은 그 json 파일임
	// 해당 파일을 통해 인증 절차를 진행해야 하기 때문에 꼭 필요함
	private static final String CREDENTIALS_FILE_PATH 
		= "/WEB-INF/oauth/credentials.json";
	
	// 클라이언트 인증 정보 객체 (Google Client Secrets)
	private GoogleClientSecrets clientSecrets;
	// Google Authorization Flow 객체 (인증 및 토큰 관리)
	private GoogleAuthorizationCodeFlow flow;
	
	private String realPath;

	@PostConstruct
	public void init() throws IOException, GeneralSecurityException {
	    // 실제 파일 경로 가져오기
	    realPath = servletContext.getRealPath(CREDENTIALS_FILE_PATH);

	    if (realPath == null) {
	        throw new FileNotFoundException("WEB-INF/oauth/credentials.json 못 찾음");
	    }

	    // 클라이언트 인증 정보 로드 (JSON 파일에서 읽음)
	    clientSecrets = GoogleClientSecrets.load(
	            GsonFactory.getDefaultInstance(),
	            new InputStreamReader(new FileInputStream(realPath))
	    );

	    // Authorization Flow를 초기화
	    flow = new GoogleAuthorizationCodeFlow.Builder(
	            GoogleNetHttpTransport.newTrustedTransport(),
	            GsonFactory.getDefaultInstance(),
	            clientSecrets,
	            SCOPES
	    )
	    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
	    .setAccessType("offline")
	    .build();
	}

	@Override
	public String getAuthorizationUrl() throws FileNotFoundException, IOException, GeneralSecurityException {
		// 리디렉션 URI(인증을 위한 URL)
		String redirectionUri = flow.newAuthorizationUrl()
				// 인증 요청 URL을 생성하고 리디렉션 URI 설정(google cloud에서 내가 설정한 그 url)
				.setRedirectUri("https://localhost/work2gether/google-oauthcheck.do")
				// 한 번 인증한 계정의 경우 두 번째 인증 요청시에는 refresh token이 자동 발급되지 않음..!!
				// 그래서 회원가입을 할 때 구글 계정 인증 절차까지는 완료했다가 어떠한 이유로 회원가입을 마무리 하지 않았다면
				// 다시 회원가입을 하려고 다시 구글 계정 인증 요청을 할 때 refresh token은 발급되지 않음
				// 그래서 인증 요청을 할 때마다 refresh token이 새로 발급되도록 하는 설정이 필요함
				// prompt=consent가 그 설정임
				.set("prompt", "consent")
				// 최종적으로 url을 만듬
				.build();
		return redirectionUri;
	}

	@Override
	public Credential getCredentialFromCode(String code) throws IOException {
		// 인증 코드()로 액세스 토큰을 요청하고 GoogleTokenResponse를 얻음
		GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
				.setRedirectUri("https://localhost/work2gether/google-oauthcheck.do") // 리디렉션 URI
				.execute();

		// GoogleTokenResponse를 Credential로 변환
		Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

		return credential; // Credential 객체 반환

	}

	@Override
	public OAuthVO getUsableAccessToken(OAuthVO myOAuth, String empMail) {
		if(myOAuth!=null) {
			String dataBaseAccessToken = myOAuth.getOauthAccess();
			// access token 확인하는 로직
			// access token은 만료 기한이 있기 때문에 경우에 따라서는 해당 access token이 만료되었을 수 있음
			// 만료된 access token을 통해 인증 혹은 api 호출을 진행하려고 하면 오류가 뜨기 때문에
			// 만료 되었는지, 아닌지를 확인해야함
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
	public OAuthVO getNewAccessToken(OAuthVO oAuth) {
		JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

		try {
			// 클라이언트 인증 정보 객체 (Google Client Secrets)
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
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
			String requestBody = String.format(
					"client_id=%s&client_secret=%s&refresh_token=%s&grant_type=refresh_token", clientId, clientSecret,
					refreshToken);

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
				// {classInfo=[], {access_token=새로 발급받은 access token 번호, expires_in=access token
				// 만료시간, scope=허용받은 스코프 범위, token_type=Bearer, id_token=...}}

				// 새로 받은 access token
				String newAccessToken = (String) jsonResponse.get("access_token");

				oAuth.setOauthAccess(newAccessToken);
//				    oAuth.setExpirationTime(newExpiresInSeconds);

				// 데이터베이스 업데이트
				googleOAuthMapper.updateOAuth(oAuth);

				return oAuth;
			} else {
				// 응답 실패시.. 
				throw new RuntimeException("refresh token 오류 : " + connection.getResponseMessage());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public ServiceResult modifyOAuth(OAuthVO oAuth) {
		// service를 통해 mapper 실행하는 코드 작성해야함
		int result = googleOAuthMapper.updateOAuth(oAuth);

		if (result > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}
	
	public ServiceResult createOAuth(OAuthVO oAuth) {
		int result = googleOAuthMapper.insertOAuth(oAuth);

		if (result > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

}
