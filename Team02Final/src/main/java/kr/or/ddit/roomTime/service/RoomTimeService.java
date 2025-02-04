package kr.or.ddit.roomTime.service;

import java.util.List;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.roomTime.vo.RoomTimeVO;

public interface RoomTimeService {
	public List<RoomTimeVO> readRoomTime(String roomId);
	public List<RoomTimeVO> readRoomTimeList();
	public ServiceResult createRoomTime(RoomTimeVO roomTime);
	public ServiceResult modifyRoomTime(RoomTimeVO roomTime);
	public ServiceResult removeRoomTime(String roomId);
	public void updateRoomTimeYn();
	public ServiceResult modifyRoomReser(String roomId, String timeCode);
}
