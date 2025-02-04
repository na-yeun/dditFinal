package kr.or.ddit.roomTime.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.roomTime.dao.RoomTimeMapper;
import kr.or.ddit.roomTime.vo.RoomTimeVO;

@Service
public class RoomTimeServiceImpl implements RoomTimeService {

	@Inject
	private RoomTimeMapper dao;
	
	
	@Override
	public List<RoomTimeVO> readRoomTime(String reserId) {
		// TODO Auto-generated method stub
		return dao.selectRoomTime(reserId);
	}

	@Override
	public List<RoomTimeVO> readRoomTimeList() {
		// TODO Auto-generated method stub
		return dao.selectRoomTimeList();
	}

	@Override
	public ServiceResult createRoomTime(RoomTimeVO roomTime) {
		if (dao.insertRoomTime(roomTime) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
		
	}

	@Override
	public ServiceResult modifyRoomTime(RoomTimeVO roomTime) {
		if (dao.updateRoomTime(roomTime) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult removeRoomTime( String reserId) {
		if (dao.deleteRoomTime(reserId) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public void updateRoomTimeYn() {
		dao.updateRoomTimeYn();
	}
	@Transactional
	@Override
	public ServiceResult modifyRoomReser(String roomId, String timeCode) {
		if (dao.updateRoomReser(roomId, timeCode) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}


}
