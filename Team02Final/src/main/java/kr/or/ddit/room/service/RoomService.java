package kr.or.ddit.room.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.question.vo.QuestionVO;
import kr.or.ddit.room.vo.RoomVO;

public interface RoomService {
	public ServiceResult createRoom(RoomVO room);
	public RoomVO readRoom(String roomId);
	public List<RoomVO> readRoomList(PaginationInfo<RoomVO> paging);
	public ServiceResult modifyRoom(RoomVO room);
	public ServiceResult removeRoom(String roomId);
	public ServiceResult deleteRoomImage(String roomId); // 기존 이미지 삭제
	
}
