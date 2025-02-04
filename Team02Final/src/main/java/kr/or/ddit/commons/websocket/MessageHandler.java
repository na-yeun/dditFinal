package kr.or.ddit.commons.websocket;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.function.Failable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // Spring에서 관리하는 Bean으로 등록
//웹소켓으로만 한것,,, sockjs는 사용 안함 근데 이걸로도 해도댐. js에서 new sockjs이렇게 해서 websocket처럼 선언해서 넣어주기만 하면 끝!
public class MessageHandler extends TextWebSocketHandler {
	
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	 
	@Resource(name="wsSessionList")
	private CopyOnWriteArrayList<WebSocketSession> wsSessionList = new CopyOnWriteArrayList<>();;
	
	@PostConstruct
	public void init() {
		log.info("websocket session list : {}", wsSessionList);
	}

	private Map<String, String> getPathVariables(WebSocketSession session) {
		String uri = session.getUri().toString();
		UriTemplate uriTmpl = new UriTemplate("/{connectType}/stomp/{var1}/{var2}");
		return uriTmpl.match(uri);
	}
	
	//이게 유저의 정보를 가져오는 것
	private String getUserName(WebSocketSession session) {
		return Optional.ofNullable(session.getPrincipal())
				.map(Principal::getName)
				.orElse("익명이");
	}
	
	
	//WebSocketSession을 통해 사용자가 연결되었을 때 실행되는 메소드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		wsSessionList.addIfAbsent(session);
		Map<String, String> variables = getPathVariables(session); 
		log.info("경로 변수들 : {}", variables);
		String userName = getUserName(session);
		log.info("{} 가 {} 연결 수립 : var1 : {}, var2 : {}", 
					userName, variables.get("connectType"), variables.get("var1"), variables.get("var2"));
	}
	
	//클라이언트가 메세지를 보내면 여기서 받는것--> 그 뒤에 연결된 클라이언트에게 다 메세지를 보내주는것 
	//json으로 할 수 있는 방법 생각하기
	@Override
	@MessageMapping("/sendMessage")
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// echo 메시지 전송
		 try {
		        // JSON 메시지 파싱
		        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

		        String recipient = (String) payload.get("recipient");
		        String content = (String) payload.get("content");
		        String sender = getUserName(session); // 발신자 정보

		        // 메시지에 발신자 정보 추가
		        payload.put("sender", sender);

		        // 특정 사용자에게 메시지 전송
		        wsSessionList.forEach(Failable.asConsumer(ws -> {
		            String recipientName = getUserName(ws); // WebSocket 세션에서 사용자 이름 추출
		            if (recipientName.equals(recipient)) {
		                ws.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload))); // JSON으로 응답 전송
		            }
		        }));
		    } catch (Exception e) {
		        log.error("메시지 처리 중 오류 발생", e);
		        session.sendMessage(new TextMessage("메시지 처리 실패: " + e.getMessage()));
		    }
	}
	//연결을 종료하고 wsSessionList 안에있는 연결이 종료된 세션을 제거
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		wsSessionList.remove(session);
		Map<String, String> variables = getPathVariables(session);
		String userName = getUserName(session);
		log.info("{} 가 {} 연결 종료 : var1 : {}, var2 : {}", 
					userName, variables.get("connectType"), variables.get("var1"), variables.get("var2"));
	}
}
