package kr.or.ddit.timeReservation.vo;

import java.util.List;

import kr.or.ddit.roomTime.vo.RoomTimeVO;
import lombok.Data;
@Data
public class TimeReservationVO {
	private String timeCode;
	private String timeRange;
	
	
	private List<RoomTimeVO> roomTimeList;
}
