package kr.or.ddit.commons.websocket;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker 
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	 @Override
	 public  boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		    messageConverters.add(new MappingJackson2MessageConverter());
		    return true; // 컨버터를 설정한 경우 true 반환
		}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// 메시지 브로커 설정
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// STOMP 엔드포인트 등록
		registry.addEndpoint("/stomp").setHandshakeHandler(new CustomHandshakeHandler()) // STOMP 엔드포인트 등록 + Custom HandshakeHandler 설정
				.addInterceptors(new HttpSessionHandshakeInterceptor(), new LoginHandshakeInterceptor()) // 핸드셰이크 핸들러 설정
				.withSockJS(); // SockJS 활성화 -->  withSockJS 이거는 Spring WebSocket에서 사용하는 메소드 SockJS 활성화함
	}
}