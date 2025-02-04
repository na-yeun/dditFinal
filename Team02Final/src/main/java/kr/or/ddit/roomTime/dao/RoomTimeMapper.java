package kr.or.ddit.roomTime.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.roomTime.vo.RoomTimeVO;
@Mapper
public interface RoomTimeMapper {
	
	public List<RoomTimeVO> selectRoomTimeList();
	
	public List<RoomTimeVO> selectRoomTime(String reserId);
	
	public int insertRoomTime(RoomTimeVO roomTime);
	
	public int updateRoomTime(RoomTimeVO roomTime);
	
	public int deleteRoomTime(String reserId);
	
	public void updateRoomTimeYn();
	public int updateRoomReser(@Param("roomId") String roomId, @Param("timeCode") String timeCode);
	public void updateTimeYn(@Param("roomId") String roomId, @Param("timeCodes") List<String> timeCodes);
}

