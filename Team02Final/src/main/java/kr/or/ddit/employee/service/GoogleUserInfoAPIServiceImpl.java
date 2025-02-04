package kr.or.ddit.employee.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;

@Service
public class GoogleUserInfoAPIServiceImpl implements GoogleUserInfoAPIService {

	@Override
	public String getUserEmail(Credential credential) throws IOException {
		// Google UserInfo API 호출
		String apiurl = "https://www.googleapis.com/oauth2/v3/userinfo";
		
		
		// HttpRequestFactory를 생성하여 요청 보냄
        HttpRequestFactory requestFactory = credential.getTransport().createRequestFactory(credential);
        GenericUrl url = new GenericUrl(apiurl);

        // 요청 실행 및 응답 파싱
        HttpRequest request = requestFactory.buildGetRequest(url);
        request.setParser(new JsonObjectParser(GsonFactory.getDefaultInstance()));

        // UserInfo API 응답 결과를 Map으로 변환
        Map<String, Object> userInfo = request.execute().parseAs(Map.class);

        // 이메일 정보 가져오기
        String email = (String) userInfo.get("email");

        return email; // 이메일 반환
	}

}
