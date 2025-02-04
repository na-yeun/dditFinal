package kr.or.ddit.timeReservation.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.timeReservation.vo.TimeReservationVO;
@Mapper
public interface TimeReservationMapper {
	public int insertTimeReser(TimeReservationVO timeReser);
	public TimeReservationVO selectTimeReser(String timeCode);
	public List<TimeReservationVO> selectTimeReserList();
	public int updateTimeReser(TimeReservationVO timeReser);
	public int deleteTimeReser(String timeCode);
}
