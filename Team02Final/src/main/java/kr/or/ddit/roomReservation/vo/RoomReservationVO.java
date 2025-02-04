package kr.or.ddit.roomReservation.vo;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import kr.or.ddit.commons.validate.InsertGroup;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.organitree.vo.DepartmentVO;
import kr.or.ddit.room.vo.RoomVO;
import kr.or.ddit.roomTime.vo.RoomTimeVO;
import kr.or.ddit.timeReservation.vo.TimeReservationVO;
import lombok.Data;
import lombok.ToString;

@Data
public class RoomReservationVO {
	private String reserId;
	private String timeCode;
	private String roomId;
	@NotBlank(groups = InsertGroup.class)
	private String reserCause;
	private String reserStatus;
	private String empId;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate reserDate;
	 private List<String> timeRanges; // 시간 범위 배열
	 private List<String> timeCodes;  // 시간 코드 배열

	
	@ToString.Exclude
	private RoomTimeVO roomTime; //부모는 이렇게 받아오기
	@ToString.Exclude
	private RoomVO room;
	@ToString.Exclude
	private TimeReservationVO timeReser;
	@ToString.Exclude
	private EmployeeVO employee;
	private DepartmentVO  department;
	
}
