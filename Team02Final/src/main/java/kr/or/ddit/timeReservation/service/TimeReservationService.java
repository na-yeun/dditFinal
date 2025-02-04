package kr.or.ddit.timeReservation.service;

import java.util.List;

import kr.or.ddit.timeReservation.vo.TimeReservationVO;

public interface TimeReservationService {
	public boolean createTimeReser(TimeReservationVO timeReser);
	public TimeReservationVO readTimeReser(String reserId);
	public List<TimeReservationVO> selectTimeReserList();
	public boolean modifyTimeReser(TimeReservationVO timeReser);
	public boolean removeTimeReser(String reserId);
}
