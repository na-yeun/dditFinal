package kr.or.ddit.commons.websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
	//WebSocket 클라이언트가 서버에 연결할 때, 서버는 클라이언트를 인증하거나 사용자 정보를 연결에 매핑할 수 있음
	//이 과정에서 determineUser  메서드를 오버라이딩하여 사용자를 식별하고 WebSocket 세션에 저장
	
	 @Override
	protected Principal determineUser(
        ServerHttpRequest request,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
		 //WebSocket 연결 전에 설정된 사용자 속성에서 username(여기서는 empId)을 가져온다
		 // LoginHandshakeInterceptor에서 설정된 empId를 저장한 후에 사원번호를 attributes 에 저장한걸 여기서 사용
	        String empId = (String) attributes.get("username");
	        if (empId == null) { //만약 empId가 없으면 예외를 던져.
	            throw new IllegalStateException("사원번호(username)가 WebSocket attributes에 없습니다.");
	        }
	        log.info("empId>>{}",empId);
	        // CustomPrincipal 생성
	        // CustomPrincipal을 사용하여 클라이언트를 식별
	        // CustomPrincipal에는 empId가 있음, 이를 통해서 클라이 언트를 구분하는 것
	        // 이후에 WebSocket 세션의 Principal로 사용
	        return new CustomPrincipal(empId);
	    }

}

