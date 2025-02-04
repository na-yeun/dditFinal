package kr.or.ddit.room.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.annotation.RootContextWebConfig;
import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.room.vo.RoomVO;

@Transactional
@RootContextWebConfig
class RoomServiceImplTest {

	@Inject
	private RoomService service;
	
	@Test
	void testCreateRoom() {
		 RoomVO room = new RoomVO();
		    room.setRoomHosu("101"); // 예: 시설 호수
		    room.setRoomName("회의실"); // 예: 시설 이름
		    room.setRoomNum((long) 10); // 예: 수용 인원
		    room.setRoomImg(null); // 테스트에서는 이미지를 null로 처리
		    room.setRoomDetail("테스트 회의실입니다."); // 예: 시설 설명
		    room.setRoomYn("Y"); // 예: 이용 가능 여부

		    // 2. Service 호출 및 결과 검증
		    ServiceResult result = service.createRoom(room);
		    
	}
	

	@Test
	void testReadRoom() {
		assertNotNull(service.readRoom("R201"));
		
	
	}

	@Test
	void testReadRoomList() {
		/* assertDoesNotThrow(()->service.readRoomList()); */
	}

	@Test
	void testModifyRoom() {
		 // 1. 초기화된 RoomVO 객체 생성
	    RoomVO room = service.readRoom("R00001");
	    if (room == null) {
	        fail("테스트 실패: RoomVO 객체가 null입니다.");
	    }

	    // 2. 수정할 데이터 설정
	    room.setRoomHosu("101"); // 예: 시설 호수
	    room.setRoomName("회의실"); // 예: 시설 이름
	    room.setRoomNum(10L); // 예: 수용 인원
	    room.setRoomImg(null); // 테스트에서는 이미지를 null로 처리
	    room.setRoomDetail("테스트 회의실입니다."); // 예: 시설 설명
	    room.setRoomYn("Y"); // 예: 이용 가능 여부

	    // 3. Service 호출 및 결과 검증
	    ServiceResult result = service.modifyRoom(room);

	    // 4. 결과 확인
	    assertEquals(ServiceResult.OK, result, "방 수정에 실패했습니다.");
	}

	@Test
	void testRemoveRoom() {
		 // 삭제 전에 데이터가 존재하는지 확인
	    RoomVO room = service.readRoom("R00001");
	    assertNotNull(room, "삭제 전 데이터가 존재해야 합니다.");

	    // 삭제 실행
	    ServiceResult result = service.removeRoom("R00001");
	    assertEquals(ServiceResult.OK, result, "삭제는 성공적으로 이루어져야 합니다.");

	    // 삭제 후 데이터가 없는지 확인
	    RoomVO deletedRoom = service.readRoom("R00001");
	    assertNull(deletedRoom, "삭제 후 데이터는 존재하지 않아야 합니다.");

		
	}

}
