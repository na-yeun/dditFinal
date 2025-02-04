package kr.or.ddit.roomReservation.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.roomReservation.vo.RoomReservationVO;

@Mapper
public interface RoomReservationMapper {

	/**
	 * @param roomReser
	 * 하나의 룸에 예약을 할때
	 */
	public int insertRoomReser(RoomReservationVO roomReser);
	/**
	 * @param reserId
	 * 예약 하나를 조회
	 */
	public RoomReservationVO selectRoomReser(String reserId);
	
	/**
	 * @return 모든 방의 예약한 현황을 조회
	 */
	public List<RoomReservationVO> selectRoomReserList();
	
	public int updateRoomReser(@Param("roomId") String roomId, @Param("timeCode") String timeCode);
	
	/**
	 * @param reserId
	 * @return 예약을 삭제
	 */
	public int deleteRoomReser(String reserId);
	
	/**
	 * @param roomId
	 * @return 한 시설의 예약 현황을 조회
	 */
	public List<RoomReservationVO> selectRoomEmpList(String roomId);
	
}
