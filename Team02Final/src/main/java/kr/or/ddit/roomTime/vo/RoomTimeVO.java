package kr.or.ddit.roomTime.vo;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.room.vo.RoomVO;
import kr.or.ddit.roomReservation.vo.RoomReservationVO;
import kr.or.ddit.timeReservation.vo.TimeReservationVO;
import lombok.Data;
import lombok.ToString;



@Data
public class RoomTimeVO {
	
	private String roomId;
	private String timeCode;
	private String roomtimeYn;
	
	private byte[] troomImg;
	private MultipartFile troomImage; 
	
	public void setRoomImage(MultipartFile troomImage) throws IOException {
		if(troomImage==null || troomImage.isEmpty()) return; 
		this.troomImage = troomImage;
		this.troomImg = troomImage.getBytes();
	}
	
	public String getBase64Img() {
		if(troomImg!=null && troomImg.length>0) {
			return Base64.getEncoder().encodeToString(troomImg);
		}else {
			return "";
		}
	}
	
	@ToString.Exclude
	private RoomVO room;//부모님을 데려오는 방법
	@ToString.Exclude
	private TimeReservationVO timeReser;
	
	
	private List<RoomReservationVO> roomReserList;//자식을 가져오는 방법
	
}
