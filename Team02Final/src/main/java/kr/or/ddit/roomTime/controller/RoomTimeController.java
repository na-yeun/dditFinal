package kr.or.ddit.roomTime.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.ddit.account.vo.AccountVO;
import kr.or.ddit.commons.paging.PaginationInfo;
import kr.or.ddit.employee.vo.EmployeeVO;
import kr.or.ddit.question.vo.QuestionVO;
import kr.or.ddit.room.dao.RoomMapper;
import kr.or.ddit.room.vo.RoomVO;
import kr.or.ddit.roomReservation.dao.RoomReservationMapper;
import kr.or.ddit.roomReservation.vo.RoomReservationVO;
import kr.or.ddit.roomTime.dao.RoomTimeMapper;
import kr.or.ddit.roomTime.service.RoomTimeService;
import kr.or.ddit.roomTime.vo.RoomTimeVO;
import kr.or.ddit.security.AccountVOWrapper;
import kr.or.ddit.timeReservation.dao.TimeReservationMapper;
import kr.or.ddit.timeReservation.vo.TimeReservationVO;

@Controller
@RequestMapping("/{companyId}/roomTime")
public class RoomTimeController {
	@Inject
	 private RoomTimeMapper roomTimeMapper;
	
	@Inject
	private RoomTimeService service;
	
	
	@Inject
	private RoomMapper roomMapper;
	@Inject
	private RoomReservationMapper roomReserMapper;
	
	@Inject
	private TimeReservationMapper timeReserMapper;
	
	@ModelAttribute("roomList")
	public  List<RoomVO> roomList(PaginationInfo<RoomVO> paging ) {
		return roomMapper.selectRoomList(paging);
	}
	@ModelAttribute("room")
	public RoomVO room(String roomId) {
		return roomMapper.selectRoom(roomId);
	}
	
	
	@ModelAttribute("timeList")
	public  List<TimeReservationVO> timeList() {
		return timeReserMapper.selectTimeReserList();
	}
	
	@ModelAttribute("roomReser")
	public  List<RoomReservationVO> roomReser(String roomId) {
		return roomReserMapper.selectRoomEmpList(roomId);
	}
	
	public static final String MODELNAME = "roomTime";
	
	@ModelAttribute(MODELNAME)
	   public RoomTimeVO roomTime() {
	      return new RoomTimeVO();
	   }
	
		/*
		 * @GetMapping public String getRoomTime(@PathVariable("companyId") String
		 * companyId, Model model ) { List<RoomTimeVO> roomTime =
		 * service.readRoomTimeList(); model.addAttribute("roomTime",roomTime);
		 * 
		 * return "room/roomList";
		 * 
		 * }
		 */
	
	
	
	@GetMapping("{roomId}")
	public String getRoomTimeOne(
			Authentication authentication,
			@PathVariable("companyId") String companyId,
			@PathVariable("roomId") String roomId, 
			Model model) {
			
			roomTimeMapper.updateRoomTimeYn();
		
			AccountVOWrapper principal = (AccountVOWrapper) authentication.getPrincipal();
			AccountVO account = principal.getAccount();
			EmployeeVO myEmp = (EmployeeVO) account;
			
			
			
			if (myEmp == null) {
				throw new IllegalStateException("로그인이 필요합니다.");
			}
			RoomVO room = roomMapper.selectRoom(roomId);
			
			 // 방 상태 확인
		    if (room == null || !"1".equals(room.getRoomYn())) {
		        // 상태가 이용 가능(1)이 아니면 접근 차단
		    	model.addAttribute("errorMessage", "이 방은 현재 이용할 수 없습니다.");
		       return "redirect:/room"; // 메인페이지로 리다이렉트
		    }
			
		    // 방 예약 및 시간 정보 가져오기
			List<RoomReservationVO> roomReserList = roomReser(roomId);
			List<RoomTimeVO> roomTimeList = service.readRoomTime(roomId);
			
			
			model.addAttribute("myEmp", myEmp);
			model.addAttribute("room", room);
			model.addAttribute("roomReserList", roomReserList);
        	model.addAttribute("roomTime", roomTimeList);
          
        	 // 상세 페이지로 이동
		    return "room/roomDetail";
	}
	
	
}
