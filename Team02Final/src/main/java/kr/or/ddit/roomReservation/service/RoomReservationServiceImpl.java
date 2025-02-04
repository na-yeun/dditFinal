package kr.or.ddit.roomReservation.service;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.roomReservation.dao.RoomReservationMapper;
import kr.or.ddit.roomReservation.vo.RoomReservationVO;
import kr.or.ddit.roomTime.dao.RoomTimeMapper;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class RoomReservationServiceImpl implements RoomReservationService {

	@Inject
	private RoomReservationMapper dao;
	
	@Inject
	private RoomTimeMapper timdDao;
	
	  @Override
	  @Transactional
	    public ServiceResult createRoomReser(RoomReservationVO roomReser) {
		  try {
			  //내가 클릭한 timeCode를 list로 가져와서 하나하나 꺼내서 넣어줘서 singleReservation 담아줌
		        for (String timeCode : roomReser.getTimeCodes()) {
		            RoomReservationVO singleReservation = new RoomReservationVO();
		            singleReservation.setRoomId(roomReser.getRoomId());
		            singleReservation.setReserCause(roomReser.getReserCause());
		            singleReservation.setEmpId(roomReser.getEmpId());
		            singleReservation.setTimeCode(timeCode);

		            dao.insertRoomReser(singleReservation); //담아준 데이터를 디비에 삽입해준다~
		        }
		        return ServiceResult.OK;
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ServiceResult.FAIL;
		    }
	    }
	
	
	
//	@Override
//	public ServiceResult createRoomReser(RoomReservationVO roomReser) {
//		if (dao.insertRoomReser(roomReser) > 0) {
//			return ServiceResult.OK;
//		} else {
//			return ServiceResult.FAIL;
//		}
//	}

	@Override
	public RoomReservationVO readRoomReser(String reserId) {
		// TODO Auto-generated method stub
		return dao.selectRoomReser(reserId);
	}

	@Override
	public List<RoomReservationVO> readRoomReserList() {
		// TODO Auto-generated method stub
		return dao.selectRoomReserList();
	}

	@Override
	public ServiceResult modifyRoomReser(String roomId, String timeCode) {
		if (dao.updateRoomReser(roomId, timeCode) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult removeRoomReser(String reserId) {
		if (dao.deleteRoomReser(reserId) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}



	@Override
	public List<RoomReservationVO> readRoomEmpList(String roomId) {
		
		return dao.selectRoomEmpList(roomId);
	}

}
