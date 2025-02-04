package kr.or.ddit.timeReservation.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.timeReservation.dao.TimeReservationMapper;
import kr.or.ddit.timeReservation.vo.TimeReservationVO;
@Service
public class TimeReservationServiceImpl implements TimeReservationService {

	@Inject
	private TimeReservationMapper dao;
	
	@Override
	public boolean createTimeReser(TimeReservationVO timeReser) {
		// TODO Auto-generated method stub
		return dao.insertTimeReser(timeReser)>0;
	}

	@Override
	public TimeReservationVO readTimeReser(String reserId) {
		// TODO Auto-generated method stub
		return dao.selectTimeReser(reserId);
	}

	@Override
	public List<TimeReservationVO> selectTimeReserList() {
		// TODO Auto-generated method stub
		return dao.selectTimeReserList();
	}

	@Override
	public boolean modifyTimeReser(TimeReservationVO timeReser) {
		// TODO Auto-generated method stub
		return dao.updateTimeReser(timeReser)>0;
	}

	@Override
	public boolean removeTimeReser(String reserId) {
		// TODO Auto-generated method stub
		return dao.deleteTimeReser(reserId)>0;
	}

}
