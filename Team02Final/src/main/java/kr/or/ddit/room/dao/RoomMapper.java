package kr.or.ddit.room.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.question.vo.QuestionVO;
import kr.or.ddit.room.vo.RoomVO;
@Mapper
public interface RoomMapper {
	public int insertRoom(RoomVO room);
	public RoomVO selectRoom(String roomId);
	/**
	 * 시설 리스트를 조회
	 *  페이징 처리 해야함
	 * @return
	 */
	public List<RoomVO> selectRoomList(@Param("paging")PaginationInfo<RoomVO> paging);
	public int selectTotalRecord(@Param("paging")PaginationInfo<RoomVO> paging);
	public int updateRoom(RoomVO room);
	public int deleteRoom(String roomId);
	public int deleteRoomImage(@Param("roomId") String roomId); // 기존 이미지 삭제
	public int roomCount(@Param("roomId")String roomId);
	
}
