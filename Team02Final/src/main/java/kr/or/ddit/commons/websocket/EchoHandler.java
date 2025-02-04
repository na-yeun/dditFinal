package kr.or.ddit.commons.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequestMapping("/echo")
public class EchoHandler extends TextWebSocketHandler{
	// 사용자 ID -> WebSocketSession ID 매핑
    private static final ConcurrentHashMap<String, String> userToWebSocketMap = new ConcurrentHashMap<>();
    // WebSocketSession ID -> WebSocketSession 매핑
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 사용자 고유 식별자 가져오기 (쿼리 파라미터나 헤더에서 추출)
        String userId = getUserIdFromSession(session);

        if (userId != null) {
            userToWebSocketMap.put(userId, session.getId());
            sessions.put(session.getId(), session);
            log.info("😍 WebSocket 연결 완료: 사용자 ID = {}, WebSocket 세션 ID = {}", userId, session.getId());
        } else {
            log.warn("😍 사용자 ID를 찾을 수 없습니다. WebSocketSession ID: {}", session.getId());
        }

        log.info("😍 현재 저장된 WebSocket 세션 목록: {}", sessions.keySet());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);

        if (userId != null) {
            userToWebSocketMap.remove(userId);
        }
        sessions.remove(session.getId());
        log.info("😍 WebSocket 연결 종료: WebSocket 세션 ID = {}", session.getId());
    }

    // 특정 사용자에게 메시지 전송
    public void sendMessageToUser(String userId, String message) throws Exception {
    	log.info("여기 오니");
    	log.info(userId);
        String webSocketSessionId = userToWebSocketMap.get(userId);
        if (webSocketSessionId != null) {
            WebSocketSession session = sessions.get(webSocketSessionId);
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(message));
                log.info("😍 메시지 전송 성공: 사용자 ID = {}, 메시지 = {}", userId, message);
            } else {
                log.warn("😍 WebSocketSession이 유효하지 않음: 사용자 ID = {}", userId);
            }
        } else {
            log.warn("😍 사용자 ID에 해당하는 WebSocketSession을 찾을 수 없음: {}", userId);
        }
    }

    // WebSocketSession에서 사용자 ID 가져오기
    private String getUserIdFromSession(WebSocketSession session) {
        // 쿼리 파라미터에서 사용자 ID를 추출
        String query = session.getUri().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                    return keyValue[1]; // userId 반환
                }
            }
        }
        return null; // 사용자 ID가 없으면 null 반환
    }
	
//	// 연결된 클라이언트 세션을 저장
//    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//    
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//    	// HttpSession ID 가져오기
//        sessions.put(session.getId(), session); // WebSocketSession 저장
//        log.info("😍 현재 저장된 WebSocket 세션 목록: {}", sessions.keySet());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//    	log.info("😍😍😍😍😍😍😍WebSocket 연결 종료: {}",session.getId());
//        sessions.remove(session.getId());
//    }
//    
//
//
//    // 특정 클라이언트에게 메시지 전송
//    public void sendMessageToClient(String sessionId, String message) throws Exception {
//    	log.info("😍😍😍😍😍😍 메시지 전송 요청: 세션 ID = {}, 메시지 = {}", sessionId, message);
//        log.info("😍😍😍😍😍😍 현재 저장된 세션 목록: {}", sessions.keySet());
//    	
//    	WebSocketSession session = sessions.get(sessionId);
//        if (session != null && session.isOpen()) {
//            session.sendMessage(new TextMessage(message));
//        }
//    }
    
   
    
}
