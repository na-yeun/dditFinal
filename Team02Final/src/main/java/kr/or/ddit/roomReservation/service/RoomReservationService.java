package kr.or.ddit.roomReservation.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.roomReservation.vo.RoomReservationVO;

public interface RoomReservationService {
	public ServiceResult createRoomReser(RoomReservationVO roomReser);
	public RoomReservationVO readRoomReser(String reserId);
	public List<RoomReservationVO> readRoomReserList();
	public ServiceResult modifyRoomReser(String roomId, String timeCode);
	public ServiceResult removeRoomReser(String reserId);
	public List<RoomReservationVO> readRoomEmpList(String roomId);
}
