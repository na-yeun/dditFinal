package kr.or.ddit.roomTime.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import kr.or.ddit.annotation.RootContextWebConfig;
@RootContextWebConfig
class RoomTimeServiceImplTest {

	@Inject
	private RoomTimeService service;
	
	@Test
	void testReadRoomTime() {
		assertNotNull(service.readRoomTime("ROOM001"));
	}

	@Test
	void testReadRoomTimeList() {
		assertDoesNotThrow(()->service.readRoomTimeList());
	}

	@Test
	void testCreateRoomTime() {
		fail("Not yet implemented");
	}

	@Test
	void testModifyRoomTime() {
		fail("Not yet implemented");
	}

	@Test
	void testRemoveRoomTime() {
		fail("Not yet implemented");
	}

}
