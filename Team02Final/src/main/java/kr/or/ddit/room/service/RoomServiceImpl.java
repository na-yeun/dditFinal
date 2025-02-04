package kr.or.ddit.room.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.commons.enumpkg.ServiceResult;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.question.vo.QuestionVO;
import kr.or.ddit.room.dao.RoomMapper;
import kr.or.ddit.room.vo.RoomVO;
@Service
public class RoomServiceImpl implements RoomService {

	@Inject
	private RoomMapper dao;
	
	@Override
	public ServiceResult createRoom(RoomVO room) {
		if (dao.insertRoom(room) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public RoomVO readRoom(String roomId) {
		// TODO Auto-generated method stub
		return dao.selectRoom(roomId);
	}

	@Override
	public List<RoomVO> readRoomList(PaginationInfo<RoomVO> paging) {
		paging.setTotalRecord(dao.selectTotalRecord(paging));
		List<RoomVO> roomList = dao.selectRoomList(paging);
		return roomList;
	}

	@Override
	public ServiceResult modifyRoom(RoomVO room) {
		if (dao.updateRoom(room) > 0) {
			return ServiceResult.OK;
		} else {
			return ServiceResult.FAIL;
		}
	}

	@Override
	public ServiceResult removeRoom(String roomId) {
		 int roomYn = dao.roomCount(roomId); // 예약된 건수가 있는지 확인

		    if (roomYn > 0) {
		        // 삭제 불가능: 예약된 건수가 있는 경우
		    	return ServiceResult.FAIL;
		    }

		    // 예약이 없을 경우 삭제 진행
		    if (dao.deleteRoom(roomId) > 0) {
		        return ServiceResult.OK; // 삭제 성공
		    } else {
		        return ServiceResult.FAIL; // 삭제 실패
		    }
	}

	@Override
	public ServiceResult deleteRoomImage(String roomId) {
		int rowcnt = dao.deleteRoomImage(roomId);
		if(rowcnt>0) return ServiceResult.OK;
		else return ServiceResult.FAIL;
		
	}

	

}
