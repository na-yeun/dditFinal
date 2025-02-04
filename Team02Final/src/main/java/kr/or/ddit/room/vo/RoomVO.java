package kr.or.ddit.room.vo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.commons.annotation.ValidFileExtension;
import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.commons.validate.UpdateGroup;
import kr.or.ddit.commons.vo.CommonCodeVO;
import kr.or.ddit.roomReservation.vo.RoomReservationVO;
import kr.or.ddit.roomTime.vo.RoomTimeVO;
import lombok.Data;
import net.coobird.thumbnailator.Thumbnails;

@Data
public class RoomVO {
	private int rnum;
	private String roomId;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	@Size(min=3, max=4, groups = InsertGroup.class)
	private String roomHosu;
	@NotBlank(groups = {InsertGroup.class, UpdateGroup.class})
	private String roomName;
	@NotNull(groups = {InsertGroup.class, UpdateGroup.class})
	@Min(value = 1, groups = {InsertGroup.class, UpdateGroup.class})
	@Max(value = 300, groups = {InsertGroup.class, UpdateGroup.class})
	private Long roomNum;
	
	private byte[] roomImg;
	
	@ValidFileExtension(allowedExtensions = {"jpg", "png", "gif"}, message = "이미지 파일의 형식이 맞지 않습니다.")
	private MultipartFile roomImage;
	
	public void setRoomImage(MultipartFile roomImage) throws IOException {
		if(roomImage==null || roomImage.isEmpty()) return; 
		this.roomImage = roomImage;
		this.roomImg = processImage(roomImage);
	}
	
	public String getBase64Img() {
		if(roomImg!=null && roomImg.length>0) {
			return Base64.getEncoder().encodeToString(roomImg);
		}else {
			return null;
		}
	}
	
	private String roomDetail;
	
	private String roomYn;
	//@NotBlank(groups = InsertGroup.class)
	private String roomGory;
	
	private List<RoomTimeVO> roomTimeList;
	private List<RoomReservationVO> roomReserList;
	
	// 이미지 압축 메서드 (Thumbnailator 사용)
    private byte[] compressImage(MultipartFile image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 이미지 압축 (예: 800px 크기 제한, 품질 70%)
        Thumbnails.of(image.getInputStream())
                  .size(300, 300)  // 크기 설정
                  .outputQuality(0.5)  // 품질 설정 (0.0 ~ 1.0)
                  .toOutputStream(outputStream);

        return outputStream.toByteArray();  // 압축된 이미지를 byte[]로 반환
    }
    
    
    private byte[] processImage(MultipartFile file) throws IOException {
    	if (file == null || file.isEmpty()) {
            return null;
        }
        return compressImage(file); // 이미지 압축 로직 호출
    }
    
    
    private CommonCodeVO comCode;
}
