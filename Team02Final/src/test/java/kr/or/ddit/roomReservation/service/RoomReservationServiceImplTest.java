package kr.or.ddit.roomReservation.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.roomReservation.vo.RoomReservationVO;
@RootContextWebConfig
class RoomReservationServiceImplTest {
	@Inject
	private RoomReservationService service;
	
	@Test
	void testCreateRoomReser() {
		RoomReservationVO roomReser = new RoomReservationVO();
//		roomReser.setReserId("20241211");
		roomReser.setRoomId("R00001"); 
		roomReser.setTimeCode("T001");// 예: 시설 호수
//		roomReser.setTimeCode("제 12회의실"); // 예: 시설 이름
		roomReser.setReserCause("111"); // 예: 수용 인
		roomReser.setReserStatus("예약완료"); // 테스트에서는 이미지를 null로 처리
	  
		roomReser.setEmpId("EMP001"); // 예: 이용 가능 여부

	    // 2. Service 호출 및 결과 검증
	    ServiceResult result = service.createRoomReser(roomReser);
	}

	@Test
	void testReadRoomReser() {
		assertNotNull(service.readRoomReser("202412110038"));
	}

	@Test
	void testReadRoomReserList() {
		assertDoesNotThrow(()->service.readRoomReserList());
	}

	@Test
	void testModifyRoomReser() {
		fail("Not yet implemented");
	}

	@Test
	void testRemoveRoomReser() {
		fail("Not yet implemented");
	}
	@Test
	void testReadRoomEmpList() {
		assertNotNull(service.readRoomEmpList("R00001"));
	}

}
