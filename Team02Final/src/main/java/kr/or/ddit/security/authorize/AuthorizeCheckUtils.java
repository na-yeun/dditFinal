package kr.or.ddit.security.authorize;

import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("authCheck") // bean의 id를 변경함. 원래는 authorizeCheckUtils 임.
public class AuthorizeCheckUtils {
	// 사전 체크
	
	public <T> boolean preCheck(Authentication authentication, T...parameters) { // before 위빙
		log.info("user : {}, parameters : {}", authentication.getName(),Arrays.toString(parameters));
		return false; // true => 다 통과 시키겠다. false => 아무도 접근을 못함.
	}
	
	public <T> boolean postCheck(Authentication authentication, T returnObject) { // 얘는 반환 객체가 중요함.
		log.info("user : {}, returnObject : {}", authentication.getName(),returnObject);
		return false; // true => 다 통과 시키겠다. false => 아무도 접근을 못함.
	}
	
	// 사후 체크
}
