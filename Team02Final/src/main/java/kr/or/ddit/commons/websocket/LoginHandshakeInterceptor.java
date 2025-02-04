package kr.or.ddit.commons.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.security.AccountVOWrapper;
import lombok.extern.slf4j.Slf4j;
@Slf4j
//웹소켓 핸드셰이크 과정에서 로그인 여부를 확인하는 것
//LoginHandshakeInterceptor: HandshakeInterceptor 인터페이스를 구현하여 WebSocket Handshake 과정을 가로챔
public class LoginHandshakeInterceptor implements HandshakeInterceptor{
//Spring WebSocket에서 Handshake 과정 중 사용자를 인증하고, WebSocket 연결 시 필요한 데이터를 설정하기 위한 Interceptor를 구현
//클라이언트가 로그인된 상태인지 확인하고, 필요한 사용자 정보를 attributes에 저장	
	 
	//beforeHandshake>>>WebSocket 연결 요청이 들어왔을 때 실행
    @Override
    public boolean beforeHandshake(ServerHttpRequest request
    		, ServerHttpResponse response
    		, WebSocketHandler wsHandler
    		, Map<String, Object> attributes
    		) {

        log.info("Handshake 요청 수신: {}", request.getURI());

        // Spring Security의 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
        	//현재 사용자 인증정보를 가져오기
            AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
            AccountVO account = principal.getAccount();
            EmployeeVO myEmp = (EmployeeVO) account;

            // 사원번호 저장
            String empId = myEmp.getEmpId();
            //인증된 사용자면 연결허용
            log.info("WebSocket 연결 허용: empId={}", empId);

            // attributes에 사원번호 저장
            attributes.put("username", empId); // 사원번호를 username으로 사용
            attributes.put("empName", myEmp.getEmpName());
            return true;
        }

        log.warn("WebSocket 연결 차단: 인증되지 않은 사용자");
        return false; // 인증되지 않은 사용자 차단
    }

    // WebSocket 연결 완료 후 실행
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("WebSocket Handshake 완료");
    }
		
}
