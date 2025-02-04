package kr.or.ddit.commons.websocket;

import java.security.Principal;

//WebSocket 통신에서 사용자 식별을 사원번호(empId)로 대체하기 위해서
public class CustomPrincipal implements Principal {

	private String empId; // 사원번호

    public CustomPrincipal(String empId) {
        this.empId = empId;
    }

    @Override
    public String getName() {
        return this.empId; // 사원번호를 반환
    }
}
